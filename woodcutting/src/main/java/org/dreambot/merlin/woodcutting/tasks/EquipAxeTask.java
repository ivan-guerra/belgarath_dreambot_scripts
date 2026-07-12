package org.dreambot.merlin.woodcutting.tasks;

import java.util.concurrent.atomic.AtomicReference;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.merlin.common.Utility;
import org.dreambot.merlin.common.WaitTimer;
import org.dreambot.merlin.woodcutting.Axe;

/**
 * Task node responsible for equipping the current axe if it is in the player's inventory and the
 * player meets the attack level requirement.
 */
public class EquipAxeTask extends TaskNode {
  private final AtomicReference<Axe> currAxe;
  private final WaitTimer waitTimer = new WaitTimer(1000, 2000);

  /**
   * Constructs a new EquipAxeTask with the given AtomicReference to the current axe.
   *
   * @param currAxe an AtomicReference to the current axe being used
   */
  public EquipAxeTask(AtomicReference<Axe> currAxe) {
    this.currAxe = currAxe;
  }

  /**
   * Checks if the player needs to equip an axe. The task will be accepted if the player does not
   * have the current axe equipped, but has it in their inventory and meets the attack level
   * requirement.
   *
   * @return true if the task should be executed, false otherwise
   */
  @Override
  public boolean accept() {
    final boolean isAxeEquipped = Utility.isEquipped(currAxe.get().getName());
    final boolean isAxeInInventory = Utility.isInInventory(currAxe.get().getName());
    final boolean isAttackLevelSufficient =
        Skills.getRealLevel(Skill.ATTACK) >= currAxe.get().getAttackLvlReq();

    return (!isAxeEquipped && isAxeInInventory && isAttackLevelSufficient);
  }

  /**
   * Executes the task of equipping the current axe.
   *
   * @return a human-like randomised delay in milliseconds before the next task execution, or -1 if
   *     an error occurred during the process
   */
  @Override
  public int execute() {
    if (!Utility.equipItem(currAxe.get().getName())) {
      Logger.error("Failed to equip " + currAxe.get().getName() + ".");
      return -1;
    }
    return waitTimer.next();
  }
}
