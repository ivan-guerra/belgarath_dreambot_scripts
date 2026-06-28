package org.dreambot.merlin.woodcutting.nodes;

import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.merlin.common.WalkingUtils;
import org.dreambot.merlin.woodcutting.Tree;

/**
 * Task node for walking to the area of a specific type of tree in the game.
 */
public class WalkToTreeAreaTask extends TaskNode {
  private static final int MAX_TREE_DIST = 7;
  private final Tree selectedTree;

  /**
   * Constructs a new WalkToTreeAreaTask for the specified tree type.
   *
   * @param selectedTree The type of tree to walk to.
   */
  public WalkToTreeAreaTask(Tree selectedTree) {
    this.selectedTree = selectedTree;
  }

  @Override
  public boolean accept() {
    boolean isInTreeArea = Players.getLocal().distance(selectedTree.getLocation()) <= MAX_TREE_DIST;

    return !isInTreeArea;
  }

  @Override
  public int execute() {
    Logger.info("Walking to " + selectedTree.getName() + " area.");
    WalkingUtils.walkToTile(selectedTree.getLocation());
    return 1000;
  }
}
