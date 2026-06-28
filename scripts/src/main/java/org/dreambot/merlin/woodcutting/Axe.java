package org.dreambot.merlin.woodcutting;

/**
 * Enum representing different types of axes available for woodcutting.
 */
public enum Axe {
  DRAGON("Dragon axe", 61, 60),
  RUNE("Rune axe", 41, 40),
  ADAMANT("Adamant axe", 30, 30),
  MITHRIL("Mithril axe", 20, 20),
  STEEL("Steel axe", 6, 5),
  IRON("Iron axe", 1, 1),
  BRONZE("Bronze axe", 1, 1);

  private final String name;
  private final int woodcutLvlReq;
  private final int attackLvlReq;

  Axe(String name, int woodcutLvlReq, int attackLvlReq) {
    this.name = name;
    this.woodcutLvlReq = woodcutLvlReq;
    this.attackLvlReq = attackLvlReq;
  }

  public String getName() {
    return name;
  }

  public int getWoodcutLvlReq() {
    return woodcutLvlReq;
  }

  public int getAttackLvlReq() {
    return attackLvlReq;
  }
}
