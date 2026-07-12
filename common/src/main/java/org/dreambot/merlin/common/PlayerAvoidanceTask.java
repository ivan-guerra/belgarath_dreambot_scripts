package org.dreambot.merlin.common;

import java.util.concurrent.atomic.AtomicReference;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;

/**
 * Task node for avoiding other players using a specified resource in the game.
 *
 * @param <T> the type of the resource to avoid
 */
public class PlayerAvoidanceTask<T> extends TaskNode {
  private AtomicReference<T> resource;
  private final WaitTimer waitTimer = new WaitTimer(1000, 2000);

  /**
   * Constructs a new PlayerAvoidanceTask with the given AtomicReference to the resource to avoid.
   *
   * @param resource an AtomicReference to the resource to avoid
   */
  public PlayerAvoidanceTask(AtomicReference<T> resource) {
    this.resource = resource;
  }

  /**
   * Determines whether another player is currently using the specified resource.
   *
   * @return true if another player is using the resource, false otherwise
   */
  @Override
  public boolean accept() {
    final GameObject obj = GameObjects.closest(resource.get().toString());

    return (obj != null && Utility.isSomeoneElseUsingNode(obj));
  }

  /**
   * Hops to a different world if another player is using the specified resource.
   *
   * @return a human-like randomised delay in milliseconds before the next task execution, or -1 if
   *     an error occurred during the process
   */
  @Override
  public int execute() {
    Logger.info("Another player is using the " + resource.get() + ". Hopping worlds...");
    if (!Utility.hopWorld()) {
      Logger.error("Failed to hop worlds.");
      return -1;
    }
    return waitTimer.next();
  }
}
