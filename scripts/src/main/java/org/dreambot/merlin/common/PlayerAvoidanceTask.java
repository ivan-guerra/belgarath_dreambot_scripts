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

  /**
   * Checks if another player is currently using the specified tree.
   *
   * @return true if another player is using the tree, false otherwise
   */
  @Override
  public boolean accept() {
    GameObject tree = GameObjects.closest(currTree.get().getName());
    return Utility.isSomeoneElseUsingNode(tree);
  }

  /**
   * Hops to a different world if another player is using the specified tree.
   *
   * @return 1000 if the world hop was successful, -1 if it failed
   */
  @Override
  public int execute() {
    if (!Utility.hopWorld()) {
      Logger.error("Failed to hop worlds.");
      return -1;
    }
    return 1000;
  }
}
