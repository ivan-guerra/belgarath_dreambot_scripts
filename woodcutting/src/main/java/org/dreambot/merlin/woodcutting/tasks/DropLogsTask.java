package org.dreambot.merlin.woodcutting.tasks;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.merlin.common.Utility;
import org.dreambot.merlin.common.WaitTimer;

/** Task node for dropping logs from the player's inventory when it is full. */
public class DropLogsTask extends TaskNode {
  private final WaitTimer waitTimer = new WaitTimer(1000, 2000);

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
   * @return a human-like randomised delay in milliseconds before the next task execution, or -1 if
   *     an error occurred during the process
   */
  @Override
  public int execute() {
    Logger.info("Inventory is full, dropping logs...");
    if (!Utility.dropVerticalOrdering("logs|scroll")) {
      Logger.error("Failed to drop logs, stopping script.");
      return -1;
    }
    return waitTimer.next();
  }
}
