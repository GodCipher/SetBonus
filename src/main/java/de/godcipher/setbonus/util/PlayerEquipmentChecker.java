package de.godcipher.setbonus.util;

import java.util.EnumMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerEquipmentChecker {

  /**
   * Calculates the percentage of the specified armor set that the player is wearing.
   *
   * @param player The player whose armor is being checked.
   * @param setType The type of armor set to check.
   * @return The percentage of the armor set that the player is wearing (e.g., 0.5 for 50%).
   */
  public static double getPlayerSetPercentage(Player player, SetType setType) {
    ItemStack[] armorContents = player.getInventory().getArmorContents();
    int count = 0;

    for (ItemStack item : armorContents) {
      if (item == null) {
        continue;
      }

      if (item.getType().name().contains(setType.getArmorType())) {
        count++;
      }
    }

    return count / 4.0;
  }

  /**
   * Determines the type of armor set with the highest percentage that the player is wearing. Only
   * considers sets with at least 2 pieces.
   *
   * @param player The player whose armor is being checked.
   * @return The SetType that the player has the most pieces of, or null if no valid set is worn.
   */
  public static SetType getSetType(Player player) {
    ItemStack[] armorContents = player.getInventory().getArmorContents();

    Map<SetType, Long> armorCountMap = new EnumMap<>(SetType.class);

    for (SetType type : SetType.values()) {
      long count = countArmorPieces(armorContents, type);
      armorCountMap.put(type, count);
    }

    return armorCountMap.entrySet().stream()
        .filter(entry -> entry.getValue() >= 2) // Only consider sets with 2 or more pieces
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElse(null);
  }

  /**
   * Counts the number of armor pieces of a specific type that the player is wearing.
   *
   * @param armorContents The player's armor inventory.
   * @param type The type of armor to count.
   * @return The number of pieces of the specified type.
   */
  private static long countArmorPieces(ItemStack[] armorContents, SetType type) {
    return java.util.Arrays.stream(armorContents)
        .filter(item -> item != null && item.getType().name().contains(type.getArmorType()))
        .count();
  }
}
