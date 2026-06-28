package org.dreambot.merlin.woodcutting;

import java.awt.Graphics2D;

import javax.swing.JOptionPane;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.script.listener.PaintListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.merlin.MerlinScript;
import org.dreambot.merlin.common.AntiBanTask;
import org.dreambot.merlin.woodcutting.nodes.ChopTreeTask;
import org.dreambot.merlin.woodcutting.nodes.DropLogsTask;
import org.dreambot.merlin.woodcutting.nodes.EquipAxeTask;
import org.dreambot.merlin.woodcutting.nodes.WalkToTreeAreaTask;

/**
 * Main script class for the woodcutting bot.
 */
public class WoodcuttingScript extends MerlinScript implements PaintListener {
  private final AntiBanTask antiBan;
  private Tree selectedTree;

  /**
   * Constructs a new WoodcuttingScript with the given AbstractScript instance.
   *
   * @param script the main AbstractScript instance
   */
  public WoodcuttingScript(AbstractScript script) {
    super(script);
    this.antiBan = new AntiBanTask(script);
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
      return;
    }

    Logger.info("Selected tree type: " + selectedTree.getName());

    if (Skills.getRealLevel(Skill.WOODCUTTING) < selectedTree.getLevelReq()) {
      Logger.error("Your woodcutting level is too low to cut " + selectedTree.getName() + "s, stopping script.");
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
    }
    return result[0];
  }

  @Override
  public TaskNode[] getNodes() {
    return new TaskNode[] { this.antiBan, new EquipAxeTask(), new WalkToTreeAreaTask(selectedTree),
        new DropLogsTask(),
        new ChopTreeTask(selectedTree) };
  }
}
