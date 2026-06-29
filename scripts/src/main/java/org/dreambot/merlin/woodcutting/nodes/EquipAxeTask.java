package org.dreambot.merlin.woodcutting.nodes;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.merlin.common.Utility;
import org.dreambot.merlin.woodcutting.Axe;

/**
 * Task node for equipping an axe from the player's inventory or withdrawing one
 * from the bank if not already equipped.
 */
public class EquipAxeTask extends TaskNode {
  /**
   * Determines whether the player is currently wielding an axe.
   *
   * @return true if the player is not wielding an axe, false otherwise
   */
  @Override
  public boolean accept() {
    final String axeSubStr = "axe";
    final boolean hasAxeEquipped = Equipment
        .contains(item -> item != null && item.getName().toLowerCase().contains(axeSubStr));

    return !hasAxeEquipped;
  }

  /**
   * Attempts to equip an axe from the player's inventory or withdraw one from
   * the bank if not already equipped.
   *
   * @return 1000 if the task was successful, -1 if it failed
   */
  @Override
  public int execute() {
    Logger.info("Attempting to equip an axe from inventory.");
    if (!equipAxe()) {
      Logger.info("No axe in inventory, attempting to withdraw one from the bank.");
      if (Bank.open()) {
        Logger.info("Bank opened, attempting to withdraw and equip an axe.");
        if (!withdrawAxeFromBank() || !equipAxe()) {
          Logger.error("Failed to withdraw or equip an axe, stopping script.");
          return -1;
        }
      }
    }
    return 1000;
  }

  /**
   * Equips an axe from the inventory if not already equipped.
   *
   * @return true if an axe is equipped or was successfully equipped, false
   *         otherwise
   */
  private boolean equipAxe() {
    final String axeSubStr = "axe";
    final String interactOption = "Wield";
    final long wieldTimeoutMs = 5000;

    // Open the inventory tab to access the axe
    if (!Utility.openInventoryTab()) {
      Logger.error("Failed to open inventory tab.");
      return false;
    }

    // Find an axe in the inventory and attempt to wield it
    Item axe = Inventory.get(item -> item != null && item.getName().toLowerCase().contains(axeSubStr));
    if (axe != null && axe.interact(interactOption)) {
      return Sleep.sleepUntil(
          () -> Equipment.contains(item -> item != null && item.getName().toLowerCase().contains(axeSubStr)),
          wieldTimeoutMs);
    }
    return false;
  }

  /**
   * Withdraws the best axe available in the nearest bank that the player
   * has the required woodcutting and attack levels to use.
   *
   * @return true if an axe was successfully withdrawn, false if no suitable
   *         axe was found or the bank could not be opened.
   */
  private boolean withdrawAxeFromBank() {
    final int woodcutLevel = Skills.getRealLevel(Skill.WOODCUTTING);
    final int attackLevel = Skills.getRealLevel(Skill.ATTACK);
    final long withDrawTimeoutMs = 5000;

    if (!Bank.isOpen()) {
      Logger.error("Called withdrawAxeFromBank() but bank is not open.");
      return false;
    }

    if (!Bank.depositAllItems()) {
      Logger.error("Failed to deposit all items in bank.");
      return false;
    }

    for (Axe axe : Axe.values()) {
      if (woodcutLevel >= axe.getWoodcutLvlReq() && attackLevel >= axe.getAttackLvlReq()
          && Bank.contains(axe.getName())) {
        if (Bank.withdraw(axe.getName(), 1)) {
          boolean withdrawn = Sleep.sleepUntil(() -> Inventory.contains(axe.getName()), withDrawTimeoutMs);
          if (!Bank.close()) {
            Logger.error("Failed to close bank after withdrawing axe.");
            return false;
          }
          return withdrawn;
        }
      }
    }

    Logger.error(
        "No suitable axe found in bank for current levels (WC: " + woodcutLevel + ", ATK: " + attackLevel + ").");
    return false;
  }
}
