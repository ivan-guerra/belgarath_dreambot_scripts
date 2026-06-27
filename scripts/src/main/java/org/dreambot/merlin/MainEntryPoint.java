package org.dreambot.merlin;

import java.awt.Graphics2D;
import java.util.Optional;

import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.PaintListener;
import org.dreambot.merlin.woodcutting.WoodcuttingScript;

/**
 * Main entry point for Merlin's DreamBot script collection.
 * Delegates execution to the selected {@link MerlinScript} implementation.
 */
@ScriptManifest(name = "Merlin's Scripts", author = "Merlin", description = "Merlin's DreamBot script collection.", category = Category.MISC, version = 0.1)
public class MainEntryPoint extends AbstractScript {
  /** The currently selected script to run. */
  private MerlinScript selectedScript;

  /** Constructs a new MainEntryPoint instance. */
  public MainEntryPoint() {
  }

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
  }

  @Override
  public void onPaint(Graphics2D g) {
    if (selectedScript instanceof PaintListener) {
      ((PaintListener) selectedScript).onPaint(g);
    }
  }

  @Override
  public int onLoop() {
    if (selectedScript != null)
      return selectedScript.onLoop();
    return 100;
  }

  @Override
  public void onPause() {
    if (selectedScript != null)
      selectedScript.onPause();
  }

  @Override
  public void onResume() {
    if (selectedScript != null)
      selectedScript.onResume();
  }

  @Override
  public void onExit() {
    if (selectedScript != null)
      selectedScript.onExit();
  }
}
