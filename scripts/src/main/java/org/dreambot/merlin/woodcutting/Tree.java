package org.dreambot.merlin.woodcutting;

import org.dreambot.api.methods.map.Tile;

/**
 * Enum representing different types of trees available for woodcutting.
 */
public enum Tree {
  /** Lumbridge Castle Normal tree. */
  Normal("Normal tree", 1, new Tile(3196, 3245, 0), false),
  /** Lumbridge Castle Oak tree. */
  Oak("Oak tree", 15, new Tile(3204, 3245, 0), false),
  /** Port Sarim Willow tree. */
  Willow("Willow tree", 30, new Tile(3059, 3255, 0), false),
  /** Isle of Souls Teak tree. */
  Teak("Teak tree", 35, new Tile(2186, 2990, 0), true);

  private final String name;
  private final int levelRequirement;
  private final Tile location;
  private final boolean isP2P;

  /**
   * Constructs a new Tree enum value with the specified name, level requirement,
   * and location.
   *
   * @param name             The name of the tree type.
   * @param levelRequirement The level requirement for cutting this type of tree.
   * @param location         The location of the tree type.
   * @param isP2P            Whether the tree type is members-only (P2P) or
   *                         free-to-play (F2P).
   */
  Tree(String name, int levelRequirement, Tile location, boolean isP2P) {
    this.name = name;
    this.levelRequirement = levelRequirement;
    this.location = location;
    this.isP2P = isP2P;
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

  /**
   * Returns whether the tree type is members-only (P2P) or free-to-play (F2P).
   *
   * @return True if the tree type is members-only (P2P), false if it is
   *         free-to-play (F2P).
   */
  public boolean isP2P() {
    return isP2P;
  }

  @Override
  public String toString() {
    return name;
  }
}
