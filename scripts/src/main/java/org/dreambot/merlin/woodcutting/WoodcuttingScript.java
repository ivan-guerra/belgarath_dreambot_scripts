package org.dreambot.merlin.woodcutting;

import java.awt.Graphics2D;
import java.util.concurrent.atomic.AtomicReference;

import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.script.listener.PaintListener;
import org.dreambot.merlin.MerlinScript;
import org.dreambot.merlin.common.AntiBanTask;
import org.dreambot.merlin.common.PlayerAvoidanceTask;
import org.dreambot.merlin.woodcutting.nodes.ChopTreeTask;
import org.dreambot.merlin.woodcutting.nodes.DropLogsTask;
import org.dreambot.merlin.woodcutting.nodes.EquipAxeTask;
import org.dreambot.merlin.woodcutting.nodes.UpgradeAxeTask;
import org.dreambot.merlin.woodcutting.nodes.UpgradeTreeTask;
import org.dreambot.merlin.woodcutting.nodes.WalkToTreeAreaTask;

/**
 * Main script class for the woodcutting bot.
 */
public class WoodcuttingScript extends MerlinScript implements PaintListener {
  private final AntiBanTask antiBan;
  private AtomicReference<Tree> currTree = new AtomicReference<>(Tree.Normal);
  private AtomicReference<Axe> currAxe = new AtomicReference<>(Axe.BRONZE);

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
  public TaskNode[] getNodes() {
    return new TaskNode[] { this.antiBan, new UpgradeTreeTask(currTree), new UpgradeAxeTask(currAxe),
        new EquipAxeTask(currAxe),
        new WalkToTreeAreaTask(currTree),
        new DropLogsTask(),
        new PlayerAvoidanceTask<Tree>(currTree),
        new ChopTreeTask(currTree) };
  }
}
