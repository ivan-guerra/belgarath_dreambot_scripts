package org.dreambot.merlin.woodcutting;

import org.dreambot.api.methods.map.Tile;

/**
 * Enum representing different types of trees available for woodcutting.
 */
public enum Tree {
  /** Normal tree. */
  Normal("Tree", 1, new Tile(3204, 3243, 0)),
  /** Oak tree. */
  Oak("Oak tree", 15, new Tile(3204, 3243, 0)),
  /** Willow tree. */
  Willow("Willow tree", 30, new Tile(3204, 3243, 0)),
  /** Maple tree. */
  Maple("Maple tree", 45, new Tile(3204, 3243, 0)),
  /** Yew tree. */
  Redwood("Redwood tree", 90, new Tile(3204, 3243, 0));

  private final String name;
  private final int levelRequirement;
  private final Tile location;

  /**
   * Constructs a new Tree enum value with the specified name, level requirement,
   * and location.
   *
   * @param name             The name of the tree type.
   * @param levelRequirement The level requirement for cutting this type of tree.
   * @param location         The location of the tree type.
   */
  Tree(String name, int levelRequirement, Tile location) {
    this.name = name;
    this.levelRequirement = levelRequirement;
    this.location = location;
  }

  /**
   * Returns the name of the tree type.
   *
   * @return The name of the tree type.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the level requirement for cutting this type of tree.
   *
   * @return The level requirement for cutting this type of tree.
   */
  public int getLevelReq() {
    return levelRequirement;
  }

  /**
   * Returns the location of the tree type.
   *
   * @return The location of the tree type.
   */
  public Tile getLocation() {
    return location;
  }

  @Override
  public String toString() {
    return name;
  }
}
