package org.dreambot.merlin.common;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;

/**
 * Utility class for managing walking behavior.
 */
public class WalkingUtils {
  private static int tilesBeforeWeWalkAgain = Calculations.random(2, 8);
  private static int currentWalkCooldown = 0;
  private static long currentTime = 0;

  /**
   * Walks to the specified tile if the cooldown has expired and the player is
   * allowed to walk.
   *
   * @param tile The target tile to walk to.
   */
  public static void walkToTile(Tile tile) {
    if (System.currentTimeMillis() < currentTime + currentWalkCooldown) {
      Logger.info("Walk cooldown!");
      return;
    }

    if (!Walking.shouldWalk(tilesBeforeWeWalkAgain)) {
      Logger.info("Should not walk until closer to target.");
      return;
    }

    Walking.walk(tile);
    currentTime = System.currentTimeMillis();
    currentWalkCooldown = Calculations.random(800, 1400);
    tilesBeforeWeWalkAgain = Calculations.random(2, 8);
  }
}
