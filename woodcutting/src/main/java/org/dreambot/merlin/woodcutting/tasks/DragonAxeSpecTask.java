package org.dreambot.merlin.woodcutting.tasks;

import java.util.concurrent.atomic.AtomicReference;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.merlin.common.Utility;
import org.dreambot.merlin.woodcutting.Axe;

/**
 * Task node responsible for activating the Dragon axe special attack if the player is using a
 * Dragon axe, has it equipped, and has enough special attack energy.
 */
public class DragonAxeSpecTask extends TaskNode {
  private final AtomicReference<Axe> currAxe;

  /**
   * Constructs a new DragonAxeSpecTask with the given AtomicReference to the current axe.
   *
   * @param currAxe an AtomicReference to the current axe being used
   */
  public DragonAxeSpecTask(AtomicReference<Axe> currAxe) {
    this.currAxe = currAxe;
  }

  /**
   * Checks if the player can activate the Dragon axe special attack. The task will be accepted if
   * the player is using a Dragon axe, has it equipped, and has enough special attack energy.
   *
   * @return true if the task should be executed, false otherwise
   */
  @Override
  public boolean accept() {
    final boolean isUsingDragonAxe = currAxe.get().getName().equals("Dragon axe");
    final boolean isDragonAxeEquipped = Utility.isEquipped("Dragon axe");
    final boolean isSpecialAttackAvailable = Combat.getSpecialPercentage() >= 100;

    return (isUsingDragonAxe && isDragonAxeEquipped && isSpecialAttackAvailable);
  }

  /**
   * Executes the task of activating the Dragon axe special attack.
   *
   * @return 2500 if the special attack was successfully activated, -1 if it failed
   */
  @Override
  public int execute() {
    if (Combat.toggleSpecialAttack(true)) {
      Logger.info("Activated Dragon axe special attack.");
    } else {
      Logger.error("Failed to activate Dragon axe special attack.");
      return -1;
    }

    return 2500;
  }
}
