package com.morgan;

import org.junit.Test;
import org.openqa.grid.web.servlets.Utils;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest {
  /**
   * Rigorous Test :-)
   */
  @Test
  public void shouldAnswerWithTrue() {
    assertTrue(true);
  }

  @Test
  public void testProcessBuilder() {
    Utils.executeCommand("vagrant global-status");
    assertTrue(true);
  }
}
