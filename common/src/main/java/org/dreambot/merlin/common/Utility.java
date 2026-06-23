package org.dreambot.merlin.common;

import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;

/** Utility class providing common helper methods for DreamBot scripts. */
public class Utility {
  /** Maximum time in milliseconds to wait for an item to be dropped. */
  private static final int DROP_TIMEOUT_MS = 3000;

  /** Time in milliseconds to poll between state checks. */
  public static final int POLL_DELAY_MS = 100;

  /**
   * Drops items from the inventory in a vertical order (column-wise) based on the
   * specified item name.
   *
   * @param itemName The name of the item to drop. The method will drop all items
   *                 that contain this
   *                 name (case-insensitive).
   */
  public static void DropVerticalOrdering(String itemName) {
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
}
