package org.dreambot.merlin.common;

import org.dreambot.api.Client;
import org.dreambot.api.data.GameState;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;

/** Utility class providing common helper methods for DreamBot scripts. */
public class Utility {
  /** Maximum time in milliseconds to wait for an item to be dropped. */
  private static final int DROP_TIMEOUT_MS = 3000;

  /** Time in milliseconds to poll between state checks. */
  public static final int POLL_DELAY_MS = 100;

  /** Maximum time in milliseconds to wait for a world hop to complete. */
  public static final int WORLD_HOP_TIMEOUT_MS = 5000;

  /** Maximum time in milliseconds to wait for a tab to open. */
  public static final int OPEN_TAB_TIMEOUT_MS = 3000;

  /** Private constructor to prevent instantiation of the Utility class. */
  public Utility() {
    // Private constructor to prevent instantiation
  }

  /**
   * Drops items from the inventory in a vertical order (column-wise) based on the
   * specified item name.
   *
   * @param itemName The name of the item to drop. The method will drop all items
   *                 that contain this name (case-insensitive).
   */
  public static void dropVerticalOrdering(String itemName) {
    Keyboard.pressShift();
    // Drop items by columns in a 28 slot inventory (0-27), starting from the
    // top-left and going down each column
    int[] ordered_slots = { 0, 4, 8, 12, 16, 20, 24, 1, 5, 9, 13, 17, 21, 25, 2, 6, 10, 14, 18, 22, 26,
        3, 7, 11, 15, 19, 23, 27 };

    for (int slot : ordered_slots) {
      Item item = Inventory.getItemInSlot(slot);
      if (item != null && item.getName().toLowerCase().contains(itemName.toLowerCase())) {
        if (item.interact()) {
          Sleep.sleepUntil(() -> Inventory.getItemInSlot(slot) == null, DROP_TIMEOUT_MS, POLL_DELAY_MS);
        }
      }
    }
    Keyboard.releaseShift();
  }

  /**
   * Hops to a random world based on the current world type (F2P or P2P). If the
   * current world is F2P, it will hop to a random F2P world. If the current world
   * is P2P, it will hop to a random P2P world. PvP worlds are excluded from the
   * selection.
   *
   * @return true if the world hop was successful, false otherwise.
   */
  public static boolean hopWorld() {
    boolean isF2P = Worlds.f2p().contains(Worlds.getCurrent());
    World world = null;

    if (isF2P) {
      world = Worlds.getRandomWorld(w -> w.isF2P() && !w.isPVP());
    } else {
      world = Worlds.getRandomWorld(w -> !w.isF2P() && !w.isPVP());
    }

    WorldHopper.hopWorld(world);

    return Sleep.sleepUntil(() -> Client.getGameState() != GameState.HOPPING, WORLD_HOP_TIMEOUT_MS);
  }

  /**
   * Checks if another player is currently using the specified game object.
   * A player is considered to be using the node if they are within 1 tile of it
   * and are currently playing an animation.
   *
   * @param target The game object to check for other players using it.
   * @return true if another player is using the node, false otherwise.
   */
  public static boolean isSomeoneElseUsingNode(GameObject target) {
    // Check if the target node exists
    if (target == null) {
      return false;
    }

    // Iterate through all players within distance
    for (Player p : Players.all()) {
      // Skip null players and skip the local (your) player
      if (p == null || p == Players.getLocal()) {
        continue;
      }

      // Check if the player is within 1 tile of the target object and playing the
      // animation
      if (p.distance(target) <= 1 && p.isAnimating()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Opens the inventory tab and waits until it is open.
   *
   * @return true if the inventory tab was successfully opened, false otherwise.
   */
  public static boolean openInventoryTab() {
    Tabs.open(Tab.INVENTORY);
    return Sleep.sleepUntil(() -> Tabs.getOpen() == Tab.INVENTORY, OPEN_TAB_TIMEOUT_MS);
  }
}
