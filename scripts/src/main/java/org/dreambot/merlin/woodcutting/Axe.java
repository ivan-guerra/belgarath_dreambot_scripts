package org.dreambot.merlin.woodcutting;

/**
 * Enum representing different types of axes available for woodcutting.
 */
public enum Axe {
  /** Dragon axe, requires level 61 woodcutting and level 60 attack. */
  DRAGON("Dragon axe", 61, 60, true),
  /** Rune axe, requires level 41 woodcutting and level 40 attack. */
  RUNE("Rune axe", 41, 40, false),
  /** Adamant axe, requires level 30 woodcutting and level 30 attack. */
  ADAMANT("Adamant axe", 30, 30, false),
  /** Mithril axe, requires level 20 woodcutting and level 20 attack. */
  MITHRIL("Mithril axe", 20, 20, false),
  /** Steel axe, requires level 6 woodcutting and level 5 attack. */
  STEEL("Steel axe", 6, 5, false),
  /** Iron axe, requires level 1 woodcutting and level 1 attack. */
  IRON("Iron axe", 1, 1, false),
  /** Bronze axe, requires level 1 woodcutting and level 1 attack. */
  BRONZE("Bronze axe", 1, 1, false);

  private final String name;
  private final int woodcutLvlReq;
  private final int attackLvlReq;
  private final boolean isMembers;

  Axe(String name, int woodcutLvlReq, int attackLvlReq, boolean isMembers) {
    this.name = name;
    this.woodcutLvlReq = woodcutLvlReq;
    this.attackLvlReq = attackLvlReq;
    this.isMembers = isMembers;
  }

  /**
   * Gets the name of the axe.
   *
   * @return the name of the axe
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the woodcutting level requirement for the axe.
   *
   * @return the woodcutting level requirement
   */
  public int getWoodcutLvlReq() {
    return woodcutLvlReq;
  }

  /**
   * Checks if the axe is members-only.
   *
   * @return true if the axe is members-only, false otherwise
   */
  public boolean isMembers() {
    return isMembers;
  }

  /**
   * Gets the attack level requirement for the axe.
   *
   * @return the attack level requirement
   */
  public int getAttackLvlReq() {
    return attackLvlReq;
  }
}
