package org.dreambot.belgarath.common;

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
    GameObject obj = GameObjects.closest(resource.get().toString());
    return obj != null && Utility.isSomeoneElseUsingNode(obj);
  }

  /**
   * Hops to a different world if another player is using the specified resource.
   *
   * @return 1000 if the task was successful, -1 if it failed
   */
  @Override
  public int execute() {
    Logger.info("Another player is using the " + resource.get() + ". Hopping worlds...");
    if (!Utility.hopWorld()) {
      Logger.error("Failed to hop worlds.");
      return -1;
    }
    return 1000;
  }
}
