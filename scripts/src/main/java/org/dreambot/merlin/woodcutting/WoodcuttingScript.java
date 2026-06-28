package org.dreambot.merlin.woodcutting;

import java.awt.Graphics2D;
import java.util.concurrent.atomic.AtomicReference;

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
import org.dreambot.merlin.woodcutting.nodes.UpgradeTreeTask;
import org.dreambot.merlin.woodcutting.nodes.WalkToTreeAreaTask;

/**
 * Main script class for the woodcutting bot.
 */
public class WoodcuttingScript extends MerlinScript implements PaintListener {
  private final AntiBanTask antiBan;
  private AtomicReference<Tree> currTree = new AtomicReference<>(Tree.Normal);

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
    int woodcutLevel = Skills.getRealLevel(Skill.WOODCUTTING);
    Tree best = Tree.Normal;

    // Find the best tree the player can chop based on their woodcutting level
    for (int i = Tree.values().length - 1; i >= 0; i--) {
      Tree t = Tree.values()[i];
      if (woodcutLevel >= t.getLevelReq()) {
        best = t;
        break;
      }
    }

    currTree.set(best);
    Logger.info("Starting woodcutting script. Current tree: " + currTree.get().getName());
  }

  @Override
  public TaskNode[] getNodes() {
    return new TaskNode[] { this.antiBan, new UpgradeTreeTask(currTree), new EquipAxeTask(),
        new WalkToTreeAreaTask(currTree),
        new DropLogsTask(),
        new ChopTreeTask(currTree) };
  }
}
