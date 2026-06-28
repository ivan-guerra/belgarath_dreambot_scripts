package org.dreambot.merlin.woodcutting.nodes;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.merlin.common.Utility;
import org.dreambot.merlin.woodcutting.Tree;

/**
 * Task node for chopping a specific type of tree in the game.
 */
public class ChopTreeTask extends TaskNode {
  private final Tree selectedTree;

  /**
   * Constructs a new ChopTreeTask for the specified tree type.
   *
   * @param selectedTree The type of tree to chop.
   */
  public ChopTreeTask(Tree selectedTree) {
    this.selectedTree = selectedTree;
  }

  @Override
  public boolean accept() {
    return !Inventory.isFull() && (GameObjects.closest(selectedTree.getName()) != null);
  }

  @Override
  public int execute() {
    final long chopTimeoutMs = 3000;
    GameObject tree = GameObjects.closest(selectedTree.getName());

    Logger.info("Chopping " + selectedTree.getName() + " at tile " + tree.getTile() + ".");
    tree.interact("Chop down");
    Sleep.sleepUntil(() -> !tree.exists() || Inventory.isFull(), () -> Players.getLocal().isAnimating(), chopTimeoutMs,
        Utility.POLL_DELAY_MS);

    return 1000;
  }
}
