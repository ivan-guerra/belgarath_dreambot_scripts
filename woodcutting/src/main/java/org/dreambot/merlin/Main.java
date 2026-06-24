package org.dreambot.merlin;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.merlin.common.AntiBan;
import org.dreambot.merlin.common.Utility;

@ScriptManifest(name = "Tree Woodcutting", author = "Merlin", description = "Woodcutting just trees.", category = Category.WOODCUTTING, version = 0.1)
public class Main extends AbstractScript {
  private static final int MAX_TREES = 2;

  private final int TREE_RESPAWN_TIME_MS = Calculations.random(30000, 45000);
  private final AntiBan antiBan = new AntiBan(this);

  private Tree selectedTree;
  private List<Tile> targetTiles = new ArrayList<>();

  /**
   * Enum representing different types of trees available for woodcutting.
   */
  private enum Tree {
    Normal("Tree", 1), Oak("Oak tree", 15), Willow("Willow tree", 30), Maple("Maple tree", 45), Yew("Yew tree", 60),
    Magic("Magic tree", 75), Ironwood("Ironwood tree", 80), Redwood("Redwood tree", 90), Rosewood("Rosewood tree", 92);

    private final String name;
    private final int levelRequirement;

    Tree(String name, int levelRequirement) {
      this.name = name;
      this.levelRequirement = levelRequirement;
    }

    public String getName() {
      return name;
    }

    public int getLevelReq() {
      return levelRequirement;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  @Override
  public void onStart() {
    selectedTree = queryTreeType();
    if (selectedTree == null) {
      Logger.error("No tree type selected, stopping script.");
      stop();
      return;
    }
    Logger.info("Selected tree type: " + selectedTree.getName());

    if (!hasLevelReq(selectedTree)) {
      Logger.error("Your woodcutting level is too low to cut " + selectedTree.getName() + "s, stopping script.");
      stop();
    }

    targetTiles = findNearestTreeTiles(selectedTree);
    if (targetTiles.isEmpty()) {
      Logger.error("No trees of type " + selectedTree.getName() + " found nearby, stopping script.");
      stop();
    }

    if (targetTiles.size() < MAX_TREES) {
      Logger.warn("Found only " + targetTiles.size() + " trees of type " + selectedTree.getName()
          + ". The script will continue with fewer trees.");
    }
  }

  /**
   * Displays a dialog to the user to select a tree type for woodcutting.
   *
   * @return The selected Tree enum value, or null if no selection was made.
   */
  private Tree queryTreeType() {
    Tree[] treeTypes = Tree.values();
    Tree[] result = new Tree[1];

    try {
      javax.swing.SwingUtilities.invokeAndWait(() -> {
        result[0] = (Tree) JOptionPane.showInputDialog(
            null,
            "Select tree type to cut:",
            "Woodcutting Setup",
            JOptionPane.QUESTION_MESSAGE,
            null,
            treeTypes,
            treeTypes[0]);
      });
    } catch (Exception e) {
      Logger.error("Error showing dialog: " + e.getMessage());
      stop();
    }
    return result[0];
  }

  /**
   * Checks if the player has the required woodcutting level to cut the specified
   * tree.
   *
   * @param tree The tree type to check against.
   * @return true if the player meets or exceeds the level requirement, false
   *         otherwise.
   */
  private boolean hasLevelReq(Tree tree) {
    return Skills.getRealLevel(Skill.WOODCUTTING) >= tree.getLevelReq();
  }

  /**
   * Finds the nearest tree tiles of the specified type.
   *
   * @param treeType The type of tree to find.
   * @return A list of tiles where the nearest trees are located.
   */
  private List<Tile> findNearestTreeTiles(Tree treeType) {
    List<GameObject> trees = GameObjects.all(obj -> obj != null && treeType.getName().equals(obj.getName()));
    trees.sort((a, b) -> Double.compare(a.distance(), b.distance()));

    List<Tile> tiles = new ArrayList<>();
    for (int i = 0; i < Math.min(MAX_TREES, trees.size()); i++) {
      tiles.add(trees.get(i).getTile());
    }
    return tiles;
  }

  @Override
  public int onLoop() {
    if (!equipAxe()) {
      Logger.error("No axe found in inventory or equipped, stopping script.");
      return -1;
    }

    if (Inventory.isFull()) {
      Logger.info("Inventory full, dropping logs...");
      Utility.DropVerticalOrdering("logs");
    }

    antiBan.run();

    GameObject tree = getTargetTree();
    if (tree != null) {
      Logger.info("Chopping tree at " + tree.getTile());
      tree.interact("Chop down");
      sleepUntil(() -> !tree.exists(), () -> Players.getLocal().isAnimating(), TREE_RESPAWN_TIME_MS,
          Utility.POLL_DELAY_MS);
    } else {
      Logger.info("All trees gone, waiting for respawn...");
      sleepUntil(() -> getTargetTree() != null, TREE_RESPAWN_TIME_MS, Utility.POLL_DELAY_MS);
    }

    return Calculations.random(300, 1000);
  }

  /**
   * Equips an axe from the inventory if not already equipped.
   *
   * @return true if an axe is equipped or was successfully equipped, false
   *         otherwise
   */
  private boolean equipAxe() {
    String axeSubStr = "axe";
    String interactOption = "Wield";

    boolean axeEquipped = Equipment.contains(item -> item != null && item.getName().toLowerCase().contains(axeSubStr));
    if (axeEquipped) {
      return true;
    }

    Item axe = Inventory.get(item -> item != null && item.getName().toLowerCase().contains(axeSubStr));
    if (axe != null) {
      // TODO: Need to check if the axe was actually equipped, maybe a sleepuntil here
      return Inventory.interact(axe, interactOption);
    }
    return false;
  }

  /**
   * Gets the closest tree of the selected type from the target tiles.
   *
   * @return The closest GameObject representing the tree, or null if none found.
   */
  private GameObject getTargetTree() {
    for (Tile tile : targetTiles) {
      GameObject tree = GameObjects.closest(
          obj -> obj != null && selectedTree.getName().equals(obj.getName()) && obj.getTile().equals(tile));
      if (tree != null)
        return tree;
    }
    return null;
  }
}
