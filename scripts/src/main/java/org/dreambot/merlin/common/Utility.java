package org.dreambot.merlin.common;

import org.dreambot.api.Client;
import org.dreambot.api.data.GameState;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
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
  private static final long DROP_TIMEOUT_MS = 3000;
  private static final long POLL_DELAY_MS = 100;
  private static final long WORLD_HOP_TIMEOUT_MS = 5000;
  private static final long OPEN_TAB_TIMEOUT_MS = 3000;
  private static final long EQUIP_ITEM_TIMEOUT_MS = 5000;
  private static final long WITHDRAW_TIMEOUT_MS = 5000;
  private static final long DEPOSIT_TIMEOUT_MS = 5000;
  private static final long BANK_CLOSE_TIMEOUT_MS = 2000;

  /** Private constructor to prevent instantiation of the Utility class. */
  private Utility() {
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
    // Drop items by columns in a 28 slot inventory (0-27), starting from the
    // top-left and going down each column
    int[] ordered_slots = { 0, 4, 8, 12, 16, 20, 24, 1, 5, 9, 13, 17, 21, 25, 2, 6, 10, 14, 18, 22, 26,
        3, 7, 11, 15, 19, 23, 27 };

    // Open the inventory tab before attempting to drop items
    if (!Utility.openInventoryTab()) {
      Logger.error("Failed to open inventory tab.");
      return false;
    }

    Keyboard.pressShift();
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

  /**
   * Checks if the player has an item equipped that matches the specified item
   * name.
   *
   * @param itemName The name of the item to check for (case-insensitive).
   * @return true if the player has the item equipped, false otherwise.
   */
  public static boolean isEquipped(String itemName) {
    return Equipment.contains(item -> item != null && item.getName().toLowerCase().contains(itemName.toLowerCase()));
  }

  /**
   * Equips an item from the inventory if it is not already equipped.
   *
   * @param itemName The name of the item to equip (case-insensitive).
   * @return true if the item is equipped or was successfully equipped, false
   *         otherwise.
   */
  public static boolean equipItem(String itemName) {
    // Open the inventory tab to access the item to equip
    if (!Utility.openInventoryTab()) {
      Logger.error("Failed to open inventory tab.");
      return false;
    }

    Item itemHandle = Inventory
        .get(item -> item != null && item.getName().toLowerCase().equals(itemName.toLowerCase()));
    if (itemHandle != null && itemHandle.interact()) {
      return Sleep.sleepUntil(() -> Utility.isEquipped(itemName), EQUIP_ITEM_TIMEOUT_MS);
    }
    return false;
  }

  /**
   * Checks if the player has an item in their inventory that matches the
   * specified item name.
   *
   * @param itemName The name of the item to check for (case-insensitive).
   * @return true if the player has the item in their inventory, false otherwise.
   */
  public static boolean isInInventory(String itemName) {
    return Inventory.contains(item -> item != null && item.getName().toLowerCase().contains(itemName.toLowerCase()));
  }

  /**
   * Withdraws an item from the bank if it is available, depositing all other
   * items first.
   *
   * @param itemName The name of the item to withdraw (case-insensitive).
   * @return true if the item was successfully withdrawn, false otherwise.
   */
  public static boolean withdrawItemFromBank(String itemName) {
    if (!Bank.isOpen()) {
      Logger.error("Called withdrawItemFromBank() but bank is not open.");
      return false;
    }

    if (!Bank.depositAllItems() || !Sleep.sleepUntil(() -> Inventory.isEmpty(), DEPOSIT_TIMEOUT_MS)) {
      Logger.error("Failed to deposit all items in bank.");
      return false;
    }

    if (Bank.contains(itemName)) {
      if (Bank.withdraw(itemName, 1)) {
        return Sleep.sleepUntil(() -> Inventory.contains(itemName), WITHDRAW_TIMEOUT_MS);
      }
    }
    return false;
  }

  /**
   * Closes the bank interface if it is currently open.
   *
   * @return true if the bank was successfully closed or was already closed,
   *         false otherwise.
   */
  public static boolean closeBank() {
    if (Bank.isOpen()) {
      Bank.close();
      return Sleep.sleepUntil(() -> !Bank.isOpen(), BANK_CLOSE_TIMEOUT_MS);
    }
    return true; // Bank is already closed
  }
}
