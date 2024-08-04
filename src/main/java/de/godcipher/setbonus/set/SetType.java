package de.godcipher.setbonus.set;

import java.util.Arrays;
import org.bukkit.inventory.ItemStack;

public enum SetType {
  LEATHER("LEATHER"),
  GOLD("GOLD"),
  CHAIN("CHAIN"),
  IRON("IRON"),
  DIAMOND("DIAMOND"),
  NETHERITE("NETHERITE");

  public static boolean isSetType(ItemStack itemStack, SetType setType) {
    return determineSetType(itemStack) == setType;
  }

  public static SetType determineSetType(ItemStack itemStack) {
    return Arrays.stream(SetType.values())
        .filter(setType -> itemStack.getType().name().contains(setType.getArmorType()))
        .findFirst()
        .orElse(null);
  }

  private final String armorType;

  SetType(String armorType) {
    this.armorType = armorType;
  }

  public String getArmorType() {
    return armorType;
  }
}
