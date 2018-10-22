package com.morgan;

import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.common.exception.RemoteUnregisterException;
import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.selenium.proxy.DefaultRemoteProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// https://github.com/paypal/SeLion/blob/develop/server/src/main/java/com/paypal/selion/proxy/SeLionRemoteProxy.java

/**
 * 1. start server
       java -cp .:dependencies/* org.openqa.grid.selenium.GridLauncherV3 -role hub -timeout 180
 * 2. start client node
       java -cp .:dependencies/* org.openqa.grid.selenium.GridLauncherV3 -role node \
            -host 192.168.33.10 -port 5550 \
            -hub http://192.168.33.1:4444/grid/register \
            -timeout 180

 java -cp .:lib/* org.openqa.grid.selenium.GridLauncherV3 \
 -role node -timeout 180 \
 -host 127.0.0.1 -port 5550 \
 -hub http://127.0.0.1:4444/grid/register \
 -proxy com.morgan.RemoteProxy



 DEBUG_SERVER PARAMs:
        org.openqa.grid.selenium.GridLauncherV3
        -role hub -timeout 180

 DEBUG_NODE PARAMs:
        org.openqa.grid.selenium.GridLauncherV3
        -role node -timeout 180 -hub http://127.0.0.1:4444/grid/register -proxy com.morgan.RemoteProxy

 */

public class MyRemoteProxy extends DefaultRemoteProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyRemoteProxy.class);

    private static final int DEFAULT_MAX_SESSIONS_ALLOWED = 50;
    private static final int CONNECTION_TIMEOUT = 30000;

    private volatile boolean scheduledShutdown;
    private volatile int totalSessionsCompleted, totalSessionsStarted;

    private final long proxyStartMillis;
    private final String machine;
    private final NodeRecycleThread nodeRecycleThread;
    private final Lock accessLock;

    /**
     * @param request  a {@link RegistrationRequest} request which represents the basic information that is to be consumed by
     *                 the grid when it is registering a new node.
     * @param registry a {@link GridRegistry} object that represents the Grid's registry.
     * @throws IOException
     */
    public MyRemoteProxy(RegistrationRequest request, GridRegistry registry) throws IOException {
        super(request, registry);

        proxyStartMillis = System.currentTimeMillis();
        scheduledShutdown = false;
        totalSessionsCompleted = 0;
        totalSessionsStarted = 0;
        machine = getRemoteHost().getHost();
        nodeRecycleThread = new NodeRecycleThread(getId());
        accessLock = new ReentrantLock();

        // Log initialization info
        final int port = getRemoteHost().getPort();

        StringBuffer info = new StringBuffer();
        info.append("New proxy instantiated for the node ").append(machine).append(":").append(port);
        LOGGER.info(info.toString());
        info = new StringBuffer();
        if (isEnabledMaxUniqueSessions()) {
            info.append("SeLionRemoteProxy will attempt to recycle the node ");
            info.append(machine).append(":").append(port).append(" after ").append(getMaxSessionsAllowed())
                    .append(" unique sessions");
        } else {
            info.append("SeLionRemoteProxy will not attempt to recycle the node ");
            info.append(machine).append(":").append(port).append(" based on unique session counting.");
        }
        LOGGER.info(info.toString());

//    // detect presence of SeLion servlet capabilities on proxy
//    canForceShutdown = isSupportedOnNode(NodeForceRestartServlet.class);
//    canAutoUpgrade = isSupportedOnNode(NodeAutoUpgradeServlet.class);
//    canViewLogs = isSupportedOnNode(LogServlet.class);
    }

    /**
     * @return whether the proxy has reached the max unique sessions
     */
    private boolean isMaxUniqueSessionsReached() {
        if (!isEnabledMaxUniqueSessions()) {
            return false;
        }
        return totalSessionsStarted >= getMaxSessionsAllowed();
    }

    @Override
    public TestSession getNewSession(Map<String, Object> requestedCapability) {


        // verification should be before lock to avoid unnecessarily acquiring lock
        if (isMaxUniqueSessionsReached() || scheduledShutdown) {

            return logSessionInfo();
        }

        try {
            accessLock.lock();

            // As per double-checked locking pattern need to have check once again
            // to avoid spawning additional session then maxSessionAllowed
            if (isMaxUniqueSessionsReached() || scheduledShutdown) {

                return logSessionInfo();
            }

            TestSession session = super.getNewSession(requestedCapability);
            if (session != null) {
                // count ONLY if the session was a valid one
                totalSessionsStarted++;
                if (isMaxUniqueSessionsReached()) {
                    startNodeRecycleThread();
                }
                LOGGER.info("Beginning session #" + totalSessionsStarted + " (" + session.toString() + ")");
            }

            return session;
        } finally {
            accessLock.unlock();
        }
    }

    private TestSession logSessionInfo() {
        LOGGER.info("Was max sessions reached? " + (isMaxUniqueSessionsReached()) + " on node " + getId());
        LOGGER.info("Was this a scheduled shutdown? " + (scheduledShutdown) + " on node " + getId());
        return null;
    }

    private void startNodeRecycleThread() {
        if (!getNodeRecycleThread().isAlive()) {
            getNodeRecycleThread().start();
        }
    }

    private void stopNodeRecycleThread() {
        if (getNodeRecycleThread().isAlive()) {
            try {
                getNodeRecycleThread().shutdown();
                getNodeRecycleThread().join(2000); // Wait no longer than 2x the recycle thread's loop
            } catch (InterruptedException e) { // NOSONAR
                // ignore
            }
        }
    }

    @Override
    public void afterSession(TestSession session) {
        totalSessionsCompleted++;
        LOGGER.info("Completed session #" + totalSessionsCompleted + " (" + session.toString() + ")");
        LOGGER.info("Total number of slots used: " + getTotalUsed() + " on node: " + getId());
    }

    private void teardown(String reason) {
        addNewEvent(new RemoteUnregisterException(String.format("Unregistering node %s because %s.",
                getId(), reason)));
        teardown();
    }

    /**
     * Thread will recycle the node when all active sessions are completed
     */
    class NodeRecycleThread extends Thread {
        private static final int DEFAULT_TIMEOUT = 0; // Waits forever
        private volatile boolean running;
        private final String nodeId;

        NodeRecycleThread(String nodeId) {
            super();
            running = false;
            this.nodeId = nodeId;
        }

        @Override
        public void run() {

            running = true;
            int timeout = getThreadWaitTimeout();
            int expired = 0;

            LOGGER.info("Started NodeRecycleThread with " + ((timeout == 0) ? "no" : "a " + timeout + " second")
                    + " timeout for node " + nodeId);
            while (keepLooping(expired, timeout)) {
                try {
                    sleep(1000);
                    expired += 1;
                } catch (InterruptedException e) {
                    if (running) {
                        // SEVERE, only if shutdown() was not called
                        LOGGER.error(e.getMessage(), e);
                    }
                    running = false;
                    LOGGER.warn("NodeRecycleThread was interrupted.");

                    return;
                }
            }

            if (wasExpired(expired, timeout)) {
                LOGGER.info("Timeout occurred while waiting for sessions to complete. Shutting down the node.");
            } else {
                LOGGER.info("All sessions are complete. Shutting down the node.");
            }
//            forceNodeShutdown();

        }

        int getThreadWaitTimeout() {
            final String key = "nodeRecycleThreadWaitTimeout";
            return config.custom.containsKey(key) ? Integer.parseInt(config.custom.get(key)) : DEFAULT_TIMEOUT;
        }

        private boolean keepLooping(int expired, int timeout) {
            return (getTotalUsed() > 0) && running && ((expired < timeout) || (timeout == 0));
        }

        private boolean wasExpired(int expired, int timeout) {
            return (expired >= timeout) && (timeout != 0);
        }

        public void shutdown() {

            running = false;
            LOGGER.info("Shutting down NodeRecycleThread for node " + nodeId);
            interrupt();

        }
    }

    /**
     * @return the {@link NodeRecycleThread} associated with this proxy
     */
    private NodeRecycleThread getNodeRecycleThread() {
        return nodeRecycleThread;
    }

    /**
     * disables the max session count for this node.
     */
    private void disableMaxSessions() {
        LOGGER.warn("Disabling max unique sessions for Node " + getId());
        config.custom.put("uniqueSessionCount", "-1");
    }

    /**
     * @return an integer value which represents the number of unique sessions this proxy allows for before
     * automatically spinning up a {@link NodeRecycleThread}
     */
    private int getMaxSessionsAllowed() {
        final String key = "uniqueSessionCount";
        return config.custom.containsKey(key) ? Integer.parseInt(config.custom.get(key)) : DEFAULT_MAX_SESSIONS_ALLOWED;
    }

    /**
     * @return total uptime since proxy came online in minutes
     */
    public long getUptimeInMinutes() {
        return TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - proxyStartMillis);
    }

    /**
     * @return total number of sessions completed since proxy came online
     */
    public int getTotalSessionsComplete() {
        return totalSessionsCompleted;
    }

    /**
     * @return total number of sessions started since proxy came online
     */
    public int getTotalSessionsStarted() {
        return totalSessionsStarted;
    }

    /**
     * @return <code>true</code> or <code>false</code>, whether the proxy is scheduled for recycle
     */
    public boolean isScheduledForRecycle() {
        return getNodeRecycleThread().isAlive();
    }

    /**
     * @return whether the proxy is enabled to limit the number of unique sessions before triggering a graceful shutdown
     */
    private boolean isEnabledMaxUniqueSessions() {
        return (getMaxSessionsAllowed() > 0);
    }

}
