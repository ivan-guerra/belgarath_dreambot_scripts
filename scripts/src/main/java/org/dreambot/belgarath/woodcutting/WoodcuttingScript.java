package org.dreambot.belgarath.woodcutting;

import java.awt.Graphics2D;
import java.util.concurrent.atomic.AtomicReference;

import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.script.listener.PaintListener;
import org.dreambot.belgarath.BelgarathScript;
import org.dreambot.belgarath.common.AntiBanTask;
import org.dreambot.belgarath.common.BankMiscItemsTask;
import org.dreambot.belgarath.common.PlayerAvoidanceTask;
import org.dreambot.belgarath.woodcutting.nodes.AcquireAxeTask;
import org.dreambot.belgarath.woodcutting.nodes.ChopTreeTask;
import org.dreambot.belgarath.woodcutting.nodes.DragonAxeSpecTask;
import org.dreambot.belgarath.woodcutting.nodes.DropLogsTask;
import org.dreambot.belgarath.woodcutting.nodes.EquipAxeTask;
import org.dreambot.belgarath.woodcutting.nodes.UpgradeAxeTask;
import org.dreambot.belgarath.woodcutting.nodes.UpgradeTreeTask;
import org.dreambot.belgarath.woodcutting.nodes.WalkToTreeAreaTask;

/** Main script class for the woodcutting bot. */
public class WoodcuttingScript extends BelgarathScript implements PaintListener {
  private final AntiBanTask antiBan;
  private final String nonMiscItemRegex = "axe|logs|scroll";
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
    return new TaskNode[] {
      this.antiBan,
      new UpgradeTreeTask(currTree),
      new UpgradeAxeTask(currAxe),
      new AcquireAxeTask(currAxe),
      new EquipAxeTask(currAxe),
      new BankMiscItemsTask(nonMiscItemRegex),
      new WalkToTreeAreaTask(currTree),
      new DropLogsTask(),
      new PlayerAvoidanceTask<Tree>(currTree),
      new DragonAxeSpecTask(currAxe),
      new ChopTreeTask(currTree)
    };
  }
}
