package org.dreambot.merlin.woodcutting;

/**
 * Enum representing different types of axes available for woodcutting.
 */
public enum Axe {
  /** Dragon axe, requires level 61 woodcutting and level 60 attack. */
  DRAGON("Dragon axe", 61, 60),
  /** Rune axe, requires level 41 woodcutting and level 40 attack. */
  RUNE("Rune axe", 41, 40),
  /** Adamant axe, requires level 30 woodcutting and level 30 attack. */
  ADAMANT("Adamant axe", 30, 30),
  /** Mithril axe, requires level 20 woodcutting and level 20 attack. */
  MITHRIL("Mithril axe", 20, 20),
  /** Steel axe, requires level 6 woodcutting and level 5 attack. */
  STEEL("Steel axe", 6, 5),
  /** Iron axe, requires level 1 woodcutting and level 1 attack. */
  IRON("Iron axe", 1, 1),
  /** Bronze axe, requires level 1 woodcutting and level 1 attack. */
  BRONZE("Bronze axe", 1, 1);

  private final String name;
  private final int woodcutLvlReq;
  private final int attackLvlReq;

  Axe(String name, int woodcutLvlReq, int attackLvlReq) {
    this.name = name;
    this.woodcutLvlReq = woodcutLvlReq;
    this.attackLvlReq = attackLvlReq;
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
   * Gets the attack level requirement for the axe.
   *
   * @return the attack level requirement
   */
  public int getAttackLvlReq() {
    return attackLvlReq;
  }
}
