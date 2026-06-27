package org.dreambot.merlin;

import org.dreambot.api.script.AbstractScript;

/**
 * Base class for all Merlin sub-scripts. Provides access to the main
 * AbstractScript instance for lifecycle control.
 */
public abstract class MerlinScript {
  /**
   * The main AbstractScript instance.
   */
  protected final AbstractScript script;

  /**
   * Constructs a new MerlinScript with the given AbstractScript instance.
   *
   * @param script the main AbstractScript instance
   */
  public MerlinScript(AbstractScript script) {
    this.script = script;
  }

  /**
   * Called when the script starts. Override this method to perform any
   * initialization or setup required for the sub-script.
   */
  public void onStart() {
  }

  /**
   * Called repeatedly while the script is running. Override this method to
   * implement the main logic of the sub-script. Return an integer indicating
   * the delay (in milliseconds) before the next loop iteration.
   *
   * @return the delay before the next loop iteration
   */
  public abstract int onLoop();

  /**
   * Called when the script is paused. Override this method to handle any
   * necessary actions when the script is paused.
   */
  public void onPause() {
  }

  /**
   * Called when the script is resumed after being paused. Override this method
   * to handle any necessary actions when the script is resumed.
   */
  public void onResume() {
  }

  /**
   * Called when the script is stopped. Override this method to perform any
   * cleanup or finalization required for the sub-script.
   */
  public void onExit() {
  }

  /**
   * Stops the main script. This method can be called from within the sub-script
   * to terminate the script execution.
   */
  protected void stop() {
    script.stop();
  }
}
