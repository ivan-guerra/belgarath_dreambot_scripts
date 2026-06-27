package org.dreambot.merlin;

import org.dreambot.api.script.AbstractScript;

/**
 * Base class for all Merlin sub-scripts. Provides access to the main
 * AbstractScript instance for lifecycle control.
 */
public abstract class MerlinScript {
  protected final AbstractScript script;

  public MerlinScript(AbstractScript script) {
    this.script = script;
  }

  public void onStart() {
  }

  public abstract int onLoop();

  public void onPause() {
  }

  public void onResume() {
  }

  public void onExit() {
  }

  protected void stop() {
    script.stop();
  }
}
