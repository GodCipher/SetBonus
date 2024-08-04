package de.godcipher.setbonus.set;

public enum SetType {
  LEATHER("LEATHER"),
  GOLD("GOLD"),
  CHAIN("CHAIN"),
  IRON("IRON"),
  DIAMOND("DIAMOND"),
  NETHERITE("NETHERITE");

  private final String armorType;

  SetType(String armorType) {
    this.armorType = armorType;
  }

  public String getArmorType() {
    return armorType;
  }
}
