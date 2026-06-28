package org.dreambot.merlin.woodcutting.nodes;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.merlin.common.Utility;

/**
 * Task node for dropping logs from the player's inventory when it is full.
 */
public class DropLogsTask extends TaskNode {
  @Override
  public boolean accept() {
    return Inventory.isFull();
  }

  @Override
  public int execute() {
    Logger.info("Inventory is full, dropping logs...");
    if (!Utility.dropVerticalOrdering("logs")) {
      Logger.error("Failed to drop logs, stopping script.");
      return -1;
    }
    return 1000;
  }
}
