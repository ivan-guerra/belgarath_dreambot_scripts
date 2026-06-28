package org.dreambot.merlin.common;

import java.awt.Color;
import java.awt.Graphics2D;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;

/**
 * AntiBanTask is a task node that simulates anti-ban behavior.
 */
public class AntiBanTask extends TaskNode {
  private final AbstractScript script;
  private volatile long afkEndTime = 0;

  /**
   * Determines whether the task should be executed based on a random chance.
   *
   * @return true if the task should be executed, false otherwise
   */
  @Override
  public boolean accept() {
    final int roll = Calculations.random(0, 100);
    return roll < 2; // 2% chance to go AFK
  }

  /**
   * Executes the anti-ban behavior by simulating going AFK for a random period of
   * time.
   *
   * @return the sleep time in milliseconds before the next task execution
   */
  @Override
  public int execute() {
    final int min_afk_min = 1; // Minimum AFK time in minutes
    final int max_afk_min = 3; // Maximum AFK time in minutes

    goAfk(min_afk_min, max_afk_min);

    return 1000;
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

  /**
   * Constructs an AntiBan instance with the provided script.
   *
   * @param script the script instance to associate with this AntiBan
   */
  public AntiBanTask(AbstractScript script) {
    this.script = script;
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
}
