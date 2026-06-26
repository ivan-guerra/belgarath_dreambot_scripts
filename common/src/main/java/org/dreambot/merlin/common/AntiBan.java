package org.dreambot.merlin.common;

import java.awt.Color;
import java.awt.Graphics2D;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.utilities.Logger;

/**
 * Collection of anti-ban methods to make the bot less detectable.
 */
public class AntiBan {
  private final AbstractScript script;
  private volatile long afkEndTime = 0;

  /**
   * @param script the running script used to invoke sleep
   */
  public AntiBan(AbstractScript script) {
    this.script = script;
  }

  /**
   * Rolls with a 2% chance to go AFK for a random period between 2-3 minutes.
   */
  public void run() {
    int roll = Calculations.random(0, 100);

    if (roll < 2) {
      goAfk(2, 3);
    }
  }

  /**
   * Draws an AFK countdown timer if currently AFK. Call from the script's
   * onPaint.
   *
   * @param g the graphics context
   */
  public void onPaint(Graphics2D g) {
    long remaining_msec = afkEndTime - System.currentTimeMillis();
    if (remaining_msec > 0) {
      long secs = remaining_msec / 1000;
      g.setFont(g.getFont().deriveFont(22f));
      g.setColor(Color.WHITE);
      // Bottom-left of the OSRS fixed-mode game viewport (512x334)
      g.drawString("AFK Break: " + secs / 60 + "m " + secs % 60 + "s", 10, 320);
    }
  }

  /**
   * Simulates going AFK by sleeping for a random period of time.
   *
   * @param min_min minimum minutes to sleep
   * @param max_min maximum minutes to sleep
   */
  private void goAfk(int min_min, int max_min) {
    int period_ms = Calculations.random(min_min * 60000, max_min * 60000);
    afkEndTime = System.currentTimeMillis() + period_ms;

    Logger.info("Anti-ban: Going AFK for " + period_ms / 1000 + "sec");
    script.sleep(period_ms);
    Logger.info("Anti-ban: Back from AFK");

    afkEndTime = 0;
  }
}
