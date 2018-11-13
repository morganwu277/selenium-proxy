# INSTALL

## Windows 10
1. NotePad++
2. JDK
3. npm
4. web-driver, please review the README.md in jasmine directory
5. Browser: chrome/firefox/ie/edge

## start sequence
1. start node:
    inside the target dir,
    `java -cp .:lib/* org.openqa.grid.selenium.GridLauncherV3 -role node -nodeConfig ../node.json`
    
2. start server
    `java -cp .:lib/* org.openqa.grid.selenium.GridLauncherV3 -role hub -hubConfig ../server.json`

3. access `http://192.168.34.1:4444/grid/admin/NodeManage/def?browserName=safari`


