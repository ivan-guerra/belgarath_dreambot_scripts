package org.dreambot.merlin.woodcutting.nodes;

import java.util.concurrent.atomic.AtomicReference;

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.merlin.common.Utility;
import org.dreambot.merlin.woodcutting.Axe;

/**
 * Task node for equipping an axe in the game.
 */
public class EquipAxeTask extends TaskNode {
  private final AtomicReference<Axe> currAxe;

  /**
   * Constructs a new EquipAxeTask with the given AtomicReference to the current
   * axe.
   *
   * @param currAxe an AtomicReference to the current axe being used
   */
  public EquipAxeTask(AtomicReference<Axe> currAxe) {
    this.currAxe = currAxe;
  }

  /**
   * Determines whether the player is currently wielding an axe.
   *
   * @return true if the player is not wielding an axe, false otherwise
   */
  @Override
  public boolean accept() {
    return !Utility.isEquipped(currAxe.get().getName());
  }

  /**
   * Equips the current axe if it is in the player's inventory, or withdraws it
   * from the bank if it is not.
   *
   * @return 1000 if the task was successful, -1 if it failed
   */
  @Override
  public int execute() {
    final String axeName = currAxe.get().getName();

    if (Utility.isInInventory(axeName)) {
      if (Utility.equipItem(axeName)) {
        Logger.info("Equipped " + axeName + ".");
      } else {
        Logger.error("Failed to equip " + axeName + ".");
        return -1;
      }
    } else if (Bank.open()) {
      if (Utility.withdrawItemFromBank(axeName)) {
        Logger.info("Withdrew axe " + axeName + " from bank.");
      } else {
        Logger.error("Failed to withdraw axe " + axeName + " from bank.");
        return -1;
      }
    }
    return 1000;
  }
}
