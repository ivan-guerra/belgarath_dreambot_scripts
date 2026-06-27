package org.dreambot.merlin.woodcutting;

import java.awt.Graphics2D;

import javax.swing.JOptionPane;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.listener.PaintListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.merlin.MerlinScript;
import org.dreambot.merlin.common.AntiBan;
import org.dreambot.merlin.common.Utility;

/**
 * Main class for the woodcutting script. This script is purely meant for
 * leveling woodcutting not money making through woodcutting! The script allows
 * the user to select a tree type on startup and will then cut that tree type
 * until the inventory is full, at which point it will drop the logs and
 * continue cutting.
 */
public class WoodcuttingScript extends MerlinScript implements PaintListener {
  private static final int MAX_TREE_DIST = 7;
  private final int WIELD_AXE_TIMEOUT_MS = 3000;
  private static final int WITHDRAW_TIMEOUT_MS = 3000;
  private final AntiBan antiBan;

  private Tree selectedTree;

  /**
   * Enum representing different types of axes available for woodcutting.
   */
  public enum Axe {
    DRAGON("Dragon axe", 61, 60),
    RUNE("Rune axe", 41, 40),
    ADAMANT("Adamant axe", 30, 30),
    MITHRIL("Mithril axe", 20, 20),
    STEEL("Steel axe", 6, 5),
    IRON("Iron axe", 1, 1),
    BRONZE("Bronze axe", 1, 1);

    private final String name;
    private final int woodcutLvlReq;
    private final int attackLvlReq;

    Axe(String name, int woodcutLvlReq, int attackLvlReq) {
      this.name = name;
      this.woodcutLvlReq = woodcutLvlReq;
      this.attackLvlReq = attackLvlReq;
    }

    public String getName() {
      return name;
    }

    public int getWoodcutLvlReq() {
      return woodcutLvlReq;
    }

    public int getAttackLvlReq() {
      return attackLvlReq;
    }
  }

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

  /**
   * Constructs a new WoodcuttingScript with the given AbstractScript instance.
   *
   * @param script the main AbstractScript instance
   */
  public WoodcuttingScript(AbstractScript script) {
    super(script);
    this.antiBan = new AntiBan(script);
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
      return;
    }
    Logger.info("Selected tree type: " + selectedTree.getName());

    if (Skills.getRealLevel(Skill.WOODCUTTING) < selectedTree.getLevelReq()) {
      Logger.error("Your woodcutting level is too low to cut " + selectedTree.getName() + "s, stopping script.");
      stop();
      return;
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

  @Override
  public int onLoop() {
    Utility.closeAllInterfaces();

    // Try to equip an axe from the inventory
    if (!equipAxe()) {
      // If no axe is equipped, try to withdraw one from the bank and equip it
      if (Bank.open()) {
        if (!withdrawAxeFromBank() || !equipAxe()) {
          Logger.error("Failed to withdraw or equip an axe, stopping script.");
          stop();
        }
      } else {
        // Player is in the process of walking to the bank
        return 600;
      }
    }

    // Always check if the inventory is full before attempting to chop a tree
    if (Inventory.isFull()) {
      Logger.info("Inventory full, dropping logs...");
      if (!Utility.dropVerticalOrdering("logs")) {
        Logger.error("Failed to drop logs, stopping script.");
        stop();
      }
    }

    antiBan.run();

    GameObject tree = findNearestTree();
    if (tree != null) {
      // World hop if another player is using the tree to avoid competition/reports
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
      // Stop if no tree has respawned after the expected respawn time
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

    // Check if an axe is already equipped
    boolean axeEquipped = Equipment.contains(item -> item != null && item.getName().toLowerCase().contains(axeSubStr));
    if (axeEquipped) {
      return true;
    }

    // Open the inventory tab to access the axe
    if (!Utility.openInventoryTab()) {
      Logger.error("Failed to open inventory tab.");
      return false;
    }

    // Find an axe in the inventory and attempt to wield it
    Item axe = Inventory.get(item -> item != null && item.getName().toLowerCase().contains(axeSubStr));
    if (axe != null && axe.interact(interactOption)) {
      return Sleep.sleepUntil(
          () -> Equipment.contains(item -> item != null && item.getName().toLowerCase().contains(axeSubStr)),
          WIELD_AXE_TIMEOUT_MS);
    }
    return false;
  }

  /**
   * Withdraws the best axe available in the nearest bank that the player
   * has the required woodcutting and attack levels to use.
   *
   * @return true if an axe was successfully withdrawn, false if no suitable
   *         axe was found or the bank could not be opened.
   */
  private boolean withdrawAxeFromBank() {
    int woodcutLevel = Skills.getRealLevel(Skill.WOODCUTTING);
    int attackLevel = Skills.getRealLevel(Skill.ATTACK);

    if (!Bank.isOpen()) {
      Logger.error("Called withdrawAxeFromBank() but bank is not open.");
      return false;
    }

    for (Axe axe : Axe.values()) {
      if (woodcutLevel >= axe.getWoodcutLvlReq() && attackLevel >= axe.getAttackLvlReq()
          && Bank.contains(axe.getName())) {
        if (Bank.withdraw(axe.getName(), 1)) {
          boolean withdrawn = Sleep.sleepUntil(() -> Inventory.contains(axe.getName()), WITHDRAW_TIMEOUT_MS);
          if (!Bank.close()) {
            Logger.error("Failed to close bank after withdrawing axe.");
            return false;
          }
          return withdrawn;
        }
      }
    }

    Logger.error(
        "No suitable axe found in bank for current levels (WC: " + woodcutLevel + ", ATK: " + attackLevel + ").");
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
