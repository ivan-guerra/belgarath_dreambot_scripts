package org.dreambot.merlin.common;

import java.util.concurrent.atomic.AtomicReference;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.merlin.woodcutting.Tree;

/**
 * A task that checks if another player is using the specified tree and hops
 * worlds if necessary.
 */
public class PlayerAvoidanceTask extends TaskNode {
  private AtomicReference<Tree> currTree;

  /**
   * Constructs a new PlayerAvoidanceTask for the specified tree type.
   *
   * @param tree The type of tree to monitor for player activity.
   */
  public PlayerAvoidanceTask(AtomicReference<Tree> tree) {
    this.currTree = tree;
  }

  @Override
  public boolean accept() {
    GameObject tree = GameObjects.closest(currTree.get().getName());
    return Utility.isSomeoneElseUsingNode(tree);
  }

  @Override
  public int execute() {
    if (!Utility.hopWorld()) {
      Logger.error("Failed to hop worlds.");
      return -1;
    }
    return 1000;
  }
}
