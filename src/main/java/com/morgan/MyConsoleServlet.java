package com.morgan;

import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.web.servlet.RegistryBasedServlet;
import org.openqa.selenium.remote.http.HttpMethod;
import org.openqa.selenium.remote.http.HttpRequest;

import javax.rmi.CORBA.Util;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MyConsoleServlet extends RegistryBasedServlet {

  public MyConsoleServlet(GridRegistry registry) {
    super(registry);
  }

  public MyConsoleServlet() {
    super(null);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doPost(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    process(req, resp);
  }


  protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(200);
    String res = handleRequest(request);
    response.getWriter().print(res);
    response.getWriter().close();
  }

  private String handleRequest(HttpServletRequest request) {
    //TODO: use jackson for better json read/write
    // https://blog.csdn.net/maoyeqiu/article/details/50490249?utm_source=blogxgwz2
    String resFormat = "{\"result\":\"%s\"}";
    String res = "";
    if (request.getPathInfo().equals("/healthz")) {
      String browserName = request.getParameter("browserName");
      boolean status = isBrowserUp(browserName);
      res = String.format(resFormat, status);
    }

    if (request.getPathInfo().equals("/startBrowserVM")) {
      // here we will invoke vagrant to start a new VM

    }

    return res;

  }

  private boolean isBrowserUp(String browserName) {
    if (!checkBrowserName(browserName)) {
      System.out.println("Error!!! wrong browser name: " + browserName);
      return false;
    }
    boolean result = false;
    Map<String, URL> browserMap = new HashMap<String, URL>();
    getRegistry().getAllProxies().forEach(p -> {
      p.getConfig().capabilities.forEach(c -> {
        browserMap.put(c.getBrowserName(), p.getRemoteHost());
      });
    });
//    System.out.println(browserMap);

    if (browserMap.containsKey(browserName)) {
      try {
        // try to execute an request, if can't connect, means, not up yet
        getRegistry().getHttpClient(browserMap.get(browserName)).execute(new HttpRequest(HttpMethod.GET, "/"));
      } catch (IOException e) {
        result = false;
        e.printStackTrace();
      }
    }
    return result;
  }

  private boolean checkBrowserName(String browserName) {
    return Utils.BROWSERS.contains(browserName);
  }
}
