package org.dreambot.merlin.common;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.utilities.Logger;

/**
 * Collection of anti-ban methods to make the bot less detectable.
 */
public class AntiBan {
  private final AbstractScript script;

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
   * Simulates going AFK by sleeping for a random period of time.
   *
   * @param min_min minimum minutes to sleep
   * @param max_min maximum minutes to sleep
   */
  private void goAfk(int min_min, int max_min) {
    int period_ms = Calculations.random(min_min * 60000, max_min * 60000);

    Logger.log("Anti-ban: Going AFK for " + period_ms / 1000 + "sec");
    script.sleep(period_ms);
    Logger.log("Anti-ban: Back from AFK");
  }
}
