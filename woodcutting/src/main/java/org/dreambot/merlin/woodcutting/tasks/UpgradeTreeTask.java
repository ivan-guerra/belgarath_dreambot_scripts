package org.dreambot.merlin.woodcutting.tasks;

import java.util.concurrent.atomic.AtomicReference;

import org.dreambot.api.Client;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.merlin.common.Utility;
import org.dreambot.merlin.common.WaitTimer;
import org.dreambot.merlin.woodcutting.Tree;

/**
 * Task node for upgrading the tree type based on the player's woodcutting level and membership
 * status, hopping worlds if necessary.
 */
public class UpgradeTreeTask extends TaskNode {
  private final AtomicReference<Tree> tree;
  private final WaitTimer waitTimer = new WaitTimer(3000, 5000);

  /**
   * Constructs a new UpgradeTreeTask for the specified tree type.
   *
   * @param tree The current tree type to be upgraded.
   */
  public UpgradeTreeTask(AtomicReference<Tree> tree) {
    this.tree = tree;
  }

  /**
   * Checks if the player can upgrade to a better tree type based on their woodcutting level and
   * membership status.
   *
   * @return true if the player can upgrade to a better tree, false otherwise
   */
  @Override
  public boolean accept() {
    return getBestTree() != tree.get();
  }

  /**
   * Upgrades the tree type to the best available tree based on the player's woodcutting level and
   * membership status, hopping worlds if necessary.
   *
   * @return a human-like randomised delay in milliseconds before the next task execution, or -1 if
   *     an error occurred during the process
   */
  @Override
  public int execute() {
    Tree current = tree.get();
    Tree best = getBestTree();
    Logger.info("Upgrading tree from " + current.getName() + " to " + best.getName() + ".");
    tree.set(best);

    if (best.isMembers() && (Worlds.getCurrent() == null || !Worlds.getCurrent().isMembers())) {
      Logger.info("Best tree is P2P but current world is F2P, hopping to a members world.");
      if (!Utility.hopToMembersWorld()) {
        Logger.error("Failed to hop to a members world.");
        return -1;
      }
    }
    return waitTimer.next();
  }

  /**
   * Returns the highest-level tree available to the player based on their woodcutting level and
   * account membership status.
   *
   * @return The best available {@link Tree} for the player.
   */
  private Tree getBestTree() {
    final int woodcutLevel = Skills.getRealLevel(Skill.WOODCUTTING);
    final boolean hasMembership = Client.getMembershipLeft() > 0;
    Tree best = Tree.Normal;

    for (Tree t : Tree.values()) {
      if (woodcutLevel >= t.getLevelReq() && (!t.isMembers() || hasMembership)) {
        best = t;
      }
    }
    return best;
  }
}
