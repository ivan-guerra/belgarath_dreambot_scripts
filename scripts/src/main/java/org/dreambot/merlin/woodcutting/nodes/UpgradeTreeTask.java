package org.dreambot.merlin.woodcutting.nodes;

import java.util.concurrent.atomic.AtomicReference;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.merlin.woodcutting.Tree;

/**
 * Task node for upgrading the tree type based on the player's woodcutting
 * level.
 */
public class UpgradeTreeTask extends TaskNode {
  private final AtomicReference<Tree> tree;

  /**
   * Constructs a new UpgradeTreeTask for the specified tree type.
   *
   * @param tree The current tree type to be upgraded.
   */
  public UpgradeTreeTask(AtomicReference<Tree> tree) {
    this.tree = tree;
  }

  @Override
  public boolean accept() {
    Tree current = tree.get();
    Tree[] trees = Tree.values();
    int nextOrdinal = current.ordinal() + 1;

    if (nextOrdinal >= trees.length) {
      return false;
    }

    Tree nextTree = trees[nextOrdinal];
    return Skills.getRealLevel(Skill.WOODCUTTING) >= nextTree.getLevelReq();
  }

  @Override
  public int execute() {
    Tree current = tree.get();
    Tree nextTree = Tree.values()[current.ordinal() + 1];

    Logger.info("Upgrading tree from " + current.getName() + " to " + nextTree.getName() + ".");
    tree.set(nextTree);

    return 1000;
  }
}
