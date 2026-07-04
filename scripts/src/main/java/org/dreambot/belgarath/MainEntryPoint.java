package org.dreambot.belgarath;

import java.awt.Graphics2D;
import java.util.Optional;

import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.impl.TaskScript;
import org.dreambot.api.script.listener.PaintListener;
import org.dreambot.belgarath.woodcutting.WoodcuttingScript;

/**
 * Main entry point for Belgarath's DreamBot script collection. Delegates execution to the selected
 * {@link BelgarathScript} implementation.
 */
@ScriptManifest(
    name = "Belgarath's Scripts",
    author = "Belgarath",
    description = "Belgarath's DreamBot script collection.",
    category = Category.MISC,
    version = 0.1)
public class MainEntryPoint extends TaskScript {
  /** The currently selected script to run. */
  private BelgarathScript selectedScript;

  /** Constructs a new MainEntryPoint instance. */
  public MainEntryPoint() {}

  @Override
  public void onPaint(Graphics2D g) {
    if (selectedScript instanceof PaintListener) {
      ((PaintListener) selectedScript).onPaint(g);
    }
  }

  /**
   * Initializes the selected script based on the provided parameters. Defaults to {@link
   * WoodcuttingScript} if no valid parameters are provided.
   */
  @Override
  public void onStart() {
    switch (Optional.ofNullable(getSDNParameters()).orElse("")) {
      case "Woodcutting":
        selectedScript = new WoodcuttingScript(this);
        break;
      default:
        // Dev mode default
        selectedScript = new WoodcuttingScript(this);
        break;
    }
    selectedScript.onStart();
    addNodes(selectedScript.getNodes());
  }

  @Override
  public void onPause() {
    if (selectedScript != null) selectedScript.onPause();
  }

  @Override
  public void onResume() {
    if (selectedScript != null) selectedScript.onResume();
  }

  @Override
  public void onExit() {
    if (selectedScript != null) selectedScript.onExit();
  }
}
