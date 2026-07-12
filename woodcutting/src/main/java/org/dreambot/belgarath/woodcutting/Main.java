package org.dreambot.belgarath.woodcutting;

import java.awt.Graphics2D;
import java.util.concurrent.atomic.AtomicReference;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.impl.TaskScript;
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

/**
 * The Main class is the entry point for the Belgarath's Woodcutting script. It extends TaskScript
 * and manages the execution of various woodcutting tasks.
 */
@ScriptManifest(
    name = "Belgarath's Woodcutting",
    author = "Belgarath",
    description = "Belgarath's power chopping script.",
    category = Category.WOODCUTTING,
    version = 0.1)
public class Main extends TaskScript {
  private final AntiBanTask antiBan;
  private final String nonMiscItemRegex = "axe|logs|scroll";
  private AtomicReference<Tree> currTree = new AtomicReference<>(Tree.Normal);
  private AtomicReference<Axe> currAxe = new AtomicReference<>(Axe.BRONZE);

  /** Constructor for the Main class. Initializes the anti-ban task. */
  public Main() {
    this.antiBan = new AntiBanTask(this);
  }

  /** Paint method rendering anti-ban information on the screen. */
  @Override
  public void onPaint(Graphics2D g) {
    antiBan.onPaint(g);
  }

  /**
   * Initializes the script by adding various woodcutting tasks to the task list. This method is
   * called when the script starts.
   */
  @Override
  public void onStart() {
    addNodes(
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
        new ChopTreeTask(currTree));
  }
}
