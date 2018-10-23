package com.morgan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;

public class Utils {
  // https://github.com/SeleniumHQ/selenium/wiki/DesiredCapabilities#used-by-the-selenium-server-for-browser-selection
  // @see org.openqa.selenium.remote.DesiredCapabilities

  public static Set<String> BROWSERS = new HashSet<>(Arrays.asList(
      "android",
      "chrome",
      "firefox",
//      "htmlunit",
      "internet explorer",
      "MicrosoftEdge",
      "iPhone",
      "iPad",
      "opera",
      "safari"
  ));

  public static Set<String> PLATFORMS = new HashSet<>(Arrays.asList(
      "WINDOWS",
      "XP",
      "VISTA",
      "MAC",
      "LINUX",
      "UNIX",
      "ANDROID",
      "ANY"
  ));


  public static void executeCommand(String commandScript) {
    System.out.println("[" + commandScript + "] script start running**********");
    try {
      ProcessBuilder pb = new ProcessBuilder("bash", "-c", commandScript);
      final Process p = pb.start();
      // use p'stdout to construct a new input stream to read the content
      BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line;
      while ((line = br.readLine()) != null) {
        System.out.println(line);
      }

      // use p'stderr to construct a new input stream to read the content
      br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
      while ((line = br.readLine()) != null) {
        System.err.println(line);
      }

    } catch (Exception ex) {
      System.out.println(ex);
    }
    System.out.println("[" + commandScript + "] script end running**********");


  }
}

