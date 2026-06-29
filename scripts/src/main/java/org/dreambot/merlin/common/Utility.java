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
import org.dreambot.api.utilities.Logger;
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
   * Drops all items in the inventory that match the specified item name, using
   * vertical ordering.
   *
   * @param itemName The name of the item to drop (case-insensitive).
   * @return true if the items were successfully dropped, false otherwise.
   */
  public static boolean dropVerticalOrdering(String itemName) {
    Keyboard.pressShift();
    // Drop items by columns in a 28 slot inventory (0-27), starting from the
    // top-left and going down each column
    int[] ordered_slots = { 0, 4, 8, 12, 16, 20, 24, 1, 5, 9, 13, 17, 21, 25, 2, 6, 10, 14, 18, 22, 26,
        3, 7, 11, 15, 19, 23, 27 };

    // Open the inventory tab before attempting to drop items
    if (!Utility.openInventoryTab()) {
      Logger.error("Failed to open inventory tab.");
      return false;
    }

    for (int slot : ordered_slots) {
      Item item = Inventory.getItemInSlot(slot);
      if (item != null && item.getName().toLowerCase().contains(itemName.toLowerCase())) {
        if (item.interact()) {
          Sleep.sleepUntil(() -> Inventory.getItemInSlot(slot) == null, DROP_TIMEOUT_MS, POLL_DELAY_MS);
        }
      }
    }
    Keyboard.releaseShift();

    return true;
  }

  /**
   * Hops to a random world based on the player's membership status (members or
   * free-to-play).
   *
   * @return true if the world hop was successful, false otherwise.
   */
  public static boolean hopWorld() {
    return Client.getMembershipLeft() > 0 ? hopToMembersWorld() : hopToF2PWorld();
  }

  /**
   * Hops to a random members (P2P) world that is not a PvP world and has no
   * minimum level requirement.
   *
   * @return true if the world hop was successful, false otherwise.
   */
  public static boolean hopToMembersWorld() {
    World world = Worlds.getRandomWorld(w -> !w.isF2P() && !w.isPVP() && w.getMinimumLevel() == 0);
    if (world != null) {
      WorldHopper.hopWorld(world);
      return Sleep.sleepUntil(() -> Client.getGameState() != GameState.HOPPING, WORLD_HOP_TIMEOUT_MS);
    }
    return false;
  }

  /**
   * Hops to a random free-to-play (F2P) world that is not a PvP world and has no
   * minimum level requirement.
   *
   * @return true if the world hop was successful, false otherwise.
   */
  public static boolean hopToF2PWorld() {
    World world = Worlds.getRandomWorld(w -> w.isF2P() && !w.isPVP() && w.getMinimumLevel() == 0);
    if (world != null) {
      WorldHopper.hopWorld(world);
      return Sleep.sleepUntil(() -> Client.getGameState() != GameState.HOPPING, WORLD_HOP_TIMEOUT_MS);
    }
    return false;
  }

  /**
   * Checks if another player is currently using the specified game object.
   * A player is considered to be using the node if they are within 2 tiles of it
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

      // Check if the player is within 2 tiles of the target object and playing
      // the animation
      if ((p.distance(target) <= 2) && p.isAnimating()) {
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

  /**
   * Closes all open interfaces by pressing the Escape key.
   */
  public static void closeAllInterfaces() {
    Keyboard.pressEsc();
  }
}
