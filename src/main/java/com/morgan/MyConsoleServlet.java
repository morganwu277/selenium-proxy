package com.morgan;

import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.web.servlet.RegistryBasedServlet;
import org.openqa.selenium.remote.http.HttpMethod;
import org.openqa.selenium.remote.http.HttpRequest;

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
    if (request.getPathInfo().equals("/checkBrowserVM")) {

    }
    if (request.getPathInfo().equals("/createBrowserVM")) {

    }
    System.out.println(request.getPathInfo());
    System.out.println(request.getQueryString());
    System.out.println(request.getParameter("browserName"));
    // https://blog.csdn.net/maoyeqiu/article/details/50490249?utm_source=blogxgwz2
    String res = "{\"test\":\"123\"}";
    response.getWriter().print(res);

    Map<String, URL> browserMap = new HashMap<String, URL>();
    getRegistry().getAllProxies()
        .forEach(p -> {
          p.getConfig().capabilities
              .forEach(c -> {
                browserMap.put(c.getBrowserName(), p.getRemoteHost());
              });
        });

    System.out.println(browserMap);

    try {
      getRegistry().getHttpClient(browserMap.get(request.getParameter("browserName")))
          .execute(new HttpRequest(HttpMethod.GET, "/"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    response.getWriter().close();
  }
}
