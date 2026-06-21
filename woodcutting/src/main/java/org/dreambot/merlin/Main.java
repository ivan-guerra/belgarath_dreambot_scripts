package org.dreambot.merlin;

import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.Calculations;

import org.dreambot.merlin.common.AntiBan;

import java.util.ArrayList;
import java.util.List;

@ScriptManifest(name = "Tree Woodcutting", author = "Merlin", description = "Woodcutting just trees.", category = Category.WOODCUTTING, version = 0.1)
public class Main extends AbstractScript {
  private final int POLL_DELAY_MS = Calculations.random(1200, 2000);
  private final int TREE_RESPAWN_TIME_MS = Calculations.random(30000, 45000);
  private final int DROP_TIMEOUT_MS = Calculations.random(3000, 4000);
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

  private void DropVerticalOrdering() {
    Keyboard.pressShift();
    // Drop items in columns in a 28 slot inventory (0-27), starting from the
    // top-left and going down each column
    int[] ordered_slots = { 0, 4, 8, 12, 16, 20, 24, 1, 5, 9, 13, 17, 21, 25, 2, 6, 10, 14, 18, 22, 26,
        3, 7, 11, 15, 19, 23, 27 };
    for (int slot : ordered_slots) {
      Item item = Inventory.getItemInSlot(slot);
      if (item != null && "Logs".equals(item.getName())) {
        if (item.interact()) {
          sleepUntil(() -> Inventory.getItemInSlot(slot) == null, DROP_TIMEOUT_MS, POLL_DELAY_MS);
        }
      }
    }
    Keyboard.releaseShift();
  }

  @Override
  public int onLoop() {
    if (!Inventory.contains("Bronze axe")) {
      Logger.log("No axe found, stopping script.");
      stop();
      return 0;
    }

    if (Inventory.isFull()) {
      Logger.log("Inventory full, dropping logs...");
      DropVerticalOrdering();
    }

    antiBan.run();

    GameObject tree = getTargetTree();
    if (tree != null) {
      Logger.log("Chopping tree at " + tree.getTile());
      tree.interact("Chop down");
      sleepUntil(() -> !tree.exists(), () -> Players.getLocal().isAnimating(), TREE_RESPAWN_TIME_MS, POLL_DELAY_MS);
    } else {
      Logger.log("All trees gone, waiting for respawn...");
      sleepUntil(() -> getTargetTree() != null, TREE_RESPAWN_TIME_MS, POLL_DELAY_MS);
    }

    return Calculations.random(300, 1000);
  }
}
