package org.dreambot.merlin;

import java.awt.Graphics2D;

import javax.swing.JOptionPane;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.merlin.common.AntiBan;
import org.dreambot.merlin.common.Utility;

@ScriptManifest(name = "Merlin's Woodcutting", author = "Merlin", description = "A leveling focused woodcutting script.", category = Category.WOODCUTTING, version = 0.1)
public class Main extends AbstractScript {
  private static final int MAX_TREE_DIST = 7;
  private final int WIELD_AXE_TIMEOUT_MS = 3000;
  private final AntiBan antiBan = new AntiBan(this);

  private Tree selectedTree;

  /**
   * Enum representing different types of trees available for woodcutting.
   */
  private enum Tree {
    Normal("Tree", 1, 49), Oak("Oak tree", 15, 9), Willow("Willow tree", 30, 9), Maple("Maple tree", 45, 36),
    Yew("Yew tree", 60, 60),
    Magic("Magic tree", 75, 120), Ironwood("Ironwood tree", 80, 120), Redwood("Redwood tree", 90, 120),
    Rosewood("Rosewood tree", 92, 123);

    private final String name;
    private final int levelRequirement;
    private final long respawnTimeSec;

    Tree(String name, int levelRequirement, long respawnTimeSec) {
      this.name = name;
      this.levelRequirement = levelRequirement;
      this.respawnTimeSec = respawnTimeSec;
    }

    public String getName() {
      return name;
    }

    public int getLevelReq() {
      return levelRequirement;
    }

    public long getRespawnTimeMSec() {
      return respawnTimeSec * 1000;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  @Override
  public void onPaint(Graphics2D g) {
    antiBan.onPaint(g);
  }

  @Override
  public void onStart() {
    selectedTree = queryTreeType();
    if (selectedTree == null) {
      Logger.error("No tree type selected, stopping script.");
      stop();
    }
    Logger.info("Selected tree type: " + selectedTree.getName());

    if (!hasLevelReq(selectedTree)) {
      Logger.error("Your woodcutting level is too low to cut " + selectedTree.getName() + "s, stopping script.");
      stop();
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

  @Override
  public int onLoop() {
    if (!equipAxe()) {
      Logger.error("No axe found in inventory or equipped, stopping script.");
      stop();
    }

    if (Inventory.isFull()) {
      Logger.info("Inventory full, dropping logs...");
      Utility.DropVerticalOrdering("logs");
    }

    antiBan.run();

    GameObject tree = findNearestTree();
    if (tree != null) {
      if (Utility.isSomeoneElseUsingNode(tree)) {
        if (Utility.hopWorld()) {
          Logger.info("Hopped to world " + Worlds.getCurrent() + " to avoid competition for the tree.");
          return 600;
        } else {
          Logger.error("Failed to hop worlds, stopping script.");
          stop();
        }
      }

      Logger.info("Chopping tree at " + tree.getTile());
      tree.interact("Chop down");
      Sleep.sleepUntil(() -> !tree.exists() || Inventory.isFull(), () -> Players.getLocal().isAnimating(),
          selectedTree.getRespawnTimeMSec(),
          Utility.POLL_DELAY_MS);
    } else {
      Logger
          .info("No " + selectedTree.getName() + "s found within " + MAX_TREE_DIST + " tiles, waiting for respawn...");
      boolean respawnedTree = Sleep.sleepUntil(() -> findNearestTree() != null, selectedTree.getRespawnTimeMSec(),
          1000);
      if (!respawnedTree) {
        Logger.error("No " + selectedTree.getName() + "s found after waiting " + selectedTree.getRespawnTimeMSec()
            + "ms, stopping script.");
        stop();
      }
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

    Tabs.open(Tab.INVENTORY);
    boolean openedInventory = Sleep.sleepUntil(() -> Tabs.getOpen() == Tab.INVENTORY, WIELD_AXE_TIMEOUT_MS);
    if (!openedInventory) {
      Logger.error("Failed to open inventory tab.");
      return false;
    }

    Item axe = Inventory.get(item -> item != null && item.getName().toLowerCase().contains(axeSubStr));
    if (axe != null && axe.interact(interactOption)) {
      return Sleep.sleepUntil(
          () -> Equipment.contains(item -> item != null && item.getName().toLowerCase().contains(axeSubStr)),
          WIELD_AXE_TIMEOUT_MS);
    }
    return false;
  }

  /**
   * Finds the nearest tree of the selected type within the maximum distance
   * defined by MAX_TREE_DIST.
   *
   * @return The nearest GameObject representing the tree, or null if none found.
   */
  private GameObject findNearestTree() {
    return GameObjects
        .closest(t -> t != null && selectedTree.getName().equals(t.getName()) && t.distance() <= MAX_TREE_DIST);
  }
}
