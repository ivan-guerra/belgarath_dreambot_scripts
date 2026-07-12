package org.dreambot.merlin.woodcutting.nodes;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.merlin.common.Utility;

/** Task node for dropping logs from the player's inventory when it is full. */
public class DropLogsTask extends TaskNode {
  /**
   * Checks if the player's inventory is full and needs to drop logs.
   *
   * @return true if the inventory is full, false otherwise
   */
  @Override
  public boolean accept() {
    return Inventory.isFull();
  }

  /**
   * Drops logs from the player's inventory to free up space.
   *
   * @return 1000 if the logs were dropped successfully, -1 if the drop failed
   */
  @Override
  public int execute() {
    Logger.info("Inventory is full, dropping logs...");
    if (!Utility.dropVerticalOrdering("logs|scroll")) {
      Logger.error("Failed to drop logs, stopping script.");
      return -1;
    }
    return 1000;
  }
}
