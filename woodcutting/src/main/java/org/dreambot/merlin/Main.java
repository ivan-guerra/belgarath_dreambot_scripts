package org.dreambot.merlin;

import java.util.ArrayList;
import java.util.List;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.merlin.common.AntiBan;
import org.dreambot.merlin.common.Utility;

@ScriptManifest(name = "Tree Woodcutting", author = "Merlin", description = "Woodcutting just trees.", category = Category.WOODCUTTING, version = 0.1)
public class Main extends AbstractScript {
  private final int TREE_RESPAWN_TIME_MS = Calculations.random(30000, 45000);
  private final int MAX_TREES = 4;
  private final List<Tile> targetTiles = new ArrayList<>();
  private final AntiBan antiBan = new AntiBan(this);

  @Override
  public void onStart() {
    List<GameObject> trees = GameObjects.all(go -> go != null && "Tree".equals(go.getName()));
    trees.sort((a, b) -> Double.compare(a.distance(), b.distance()));

    for (int i = 0; i < Math.min(MAX_TREES, trees.size()); i++) {
      targetTiles.add(trees.get(i).getTile());
      Logger.log("Targeting tree at: " + trees.get(i).getTile());
    }

    if (targetTiles.isEmpty()) {
      Logger.log("No trees found nearby, stopping script.");
      stop();
    }
  }

  private GameObject getTargetTree() {
    for (Tile tile : targetTiles) {
      GameObject tree = GameObjects.closest(
          go -> go != null && "Tree".equals(go.getName()) && go.getTile().equals(tile));
      if (tree != null)
        return tree;
    }
    return null;
  }

  @Override
  public int onLoop() {
    if (!hasAxe()) {
      Logger.log("No axe found in inventory or equipment, stopping script.");
      return -1;
    }

    if (Inventory.isFull()) {
      Logger.log("Inventory full, dropping logs...");
      Utility.DropVerticalOrdering("Logs");
    }

    antiBan.run();

    GameObject tree = getTargetTree();
    if (tree != null) {
      Logger.log("Chopping tree at " + tree.getTile());
      tree.interact("Chop down");
      sleepUntil(() -> !tree.exists(), () -> Players.getLocal().isAnimating(), TREE_RESPAWN_TIME_MS,
          Utility.POLL_DELAY_MS);
    } else {
      Logger.log("All trees gone, waiting for respawn...");
      sleepUntil(() -> getTargetTree() != null, TREE_RESPAWN_TIME_MS, Utility.POLL_DELAY_MS);
    }

    return Calculations.random(300, 1000);
  }

  /** Checks if the player has any axe in their inventory or equipped. */
  private boolean hasAxe() {
    return Inventory.contains(item -> item != null && item.getName().toLowerCase().contains("axe"))
        || Equipment.contains(item -> item != null && item.getName().toLowerCase().contains("axe"));
  }

}
