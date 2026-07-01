package org.dreambot.merlin.woodcutting.nodes;

import java.util.concurrent.atomic.AtomicReference;

import org.dreambot.api.Client;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.merlin.common.Utility;
import org.dreambot.merlin.woodcutting.Axe;

/**
 * Task node for upgrading the axe based on the player's woodcutting level, attack level, and
 * membership status, hopping worlds if necessary.
 */
public class UpgradeAxeTask extends TaskNode {
  private final AtomicReference<Axe> axe;

  /**
   * Constructs a new UpgradeAxeTask for the specified axe.
   *
   * @param currAxe The current axe to be upgraded.
   */
  public UpgradeAxeTask(AtomicReference<Axe> currAxe) {
    this.axe = currAxe;
  }

  /**
   * Checks if the player can upgrade to a better axe based on their woodcutting and attack levels.
   *
   * @return true if the player can upgrade to a better axe, false otherwise
   */
  @Override
  public boolean accept() {
    return getBestAxe() != axe.get();
  }

  /**
   * Upgrades the axe to the best available axe based on the player's woodcutting and attack levels,
   * hopping worlds if necessary.
   *
   * @return 1000 if the upgrade was successful, -1 if it failed
   */
  @Override
  public int execute() {
    Axe current = axe.get();
    Axe best = getBestAxe();
    Logger.info("Upgrading axe from " + current.getName() + " to " + best.getName() + ".");
    axe.set(best);

    if (best.isMembers() && (Worlds.getCurrent() == null || !Worlds.getCurrent().isMembers())) {
      Logger.info("Best axe is members-only but current world is F2P, hopping to a members world.");
      if (!Utility.hopToMembersWorld()) {
        Logger.error("Failed to hop to a members world.");
        return -1;
      }
    }
    return 1000;
  }

  /**
   * Gets the best available axe based on the player's woodcutting and attack levels, as well as
   * membership status.
   *
   * @return the best available axe
   */
  private Axe getBestAxe() {
    final int woodcuttingLevel = Skills.getRealLevel(Skill.WOODCUTTING);
    final int attackLevel = Skills.getRealLevel(Skill.ATTACK);
    final boolean hasMembership = Client.getMembershipLeft() > 0;

    for (Axe axe : Axe.values()) {
      if (woodcuttingLevel >= axe.getWoodcutLvlReq()
          && attackLevel >= axe.getAttackLvlReq()
          && (!axe.isMembers() || hasMembership)) {
        return axe;
      }
    }
    return Axe.BRONZE;
  }
}
