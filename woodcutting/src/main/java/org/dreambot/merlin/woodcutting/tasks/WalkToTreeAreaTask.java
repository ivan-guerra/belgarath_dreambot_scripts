package org.dreambot.merlin.woodcutting.tasks;

import java.util.concurrent.atomic.AtomicReference;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.merlin.common.WaitTimer;
import org.dreambot.merlin.common.WalkingUtils;
import org.dreambot.merlin.woodcutting.Tree;

/** Task node for walking to the area of a specific type of tree in the game. */
public class WalkToTreeAreaTask extends TaskNode {
  private static final int MAX_TREE_DIST = 7;
  private final AtomicReference<Tree> currTree;
  private final WaitTimer waitTimer = new WaitTimer(1000, 2000);

  /**
   * Constructs a new WalkToTreeAreaTask for the specified tree type.
   *
   * @param tree The type of tree to walk to.
   */
  public WalkToTreeAreaTask(AtomicReference<Tree> tree) {
    this.currTree = tree;
  }

  /**
   * Checks if the player is within the area of the specified tree type.
   *
   * @return true if the player is not in the tree area, false otherwise
   */
  @Override
  public boolean accept() {
    final boolean isInTreeArea =
        Players.getLocal().distance(currTree.get().getLocation()) <= MAX_TREE_DIST;

    return !isInTreeArea;
  }

  /**
   * Walks to the area of the specified tree type.
   *
   * @return a human-like randomised delay in milliseconds before the next task execution
   */
  @Override
  public int execute() {
    Logger.info("Walking to " + currTree.get().getName() + " area.");

    WalkingUtils.walkToTile(currTree.get().getLocation());

    return waitTimer.next();
  }
}
