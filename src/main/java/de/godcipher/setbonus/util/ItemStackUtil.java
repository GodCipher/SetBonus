package de.godcipher.setbonus.util;

import java.util.*;
import java.util.stream.Collectors;

import de.godcipher.setbonus.set.EffectType;
import de.godcipher.setbonus.set.SetBonusStats;
import de.godcipher.setbonus.set.SetType;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackUtil {

  private static final String BONUS_PREFIX = "§e§lSet Bonus:";

  public static void applyLoreToItem(
          ItemStack itemStack, SetBonusStats setBonusStats, boolean active, double setPercentage) {
    ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta == null) return;

    List<String> lore = new ArrayList<>(Arrays.asList(" ", BONUS_PREFIX));

    if (setBonusStats != null) {
      List<Map.Entry<EffectType, Integer>> positiveBonuses = getPositiveBonuses(setBonusStats);
      int bonusesToHighlight = (int) Math.round(positiveBonuses.size() * setPercentage);

      for (int i = 0; i < positiveBonuses.size(); i++) {
        boolean isBonusActive = active && i < bonusesToHighlight;
        lore.add(formatLoreLine(positiveBonuses.get(i), isBonusActive));
      }
    }

    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);
  }

  public static boolean isArmor(ItemStack itemStack) {
    return EnchantmentTarget.ARMOR.includes(itemStack);
  }

  public static SetType determineSetType(ItemStack itemStack) {
    return Arrays.stream(SetType.values())
        .filter(setType -> itemStack.getType().name().contains(setType.getArmorType()))
        .findFirst()
        .orElse(null);
  }

  private static List<Map.Entry<EffectType, Integer>> getPositiveBonuses(
      SetBonusStats setBonusStats) {
    return setBonusStats.getStats().entrySet().stream()
        .filter(entry -> entry.getValue() > 0)
        .collect(Collectors.toList());
  }

  private static String formatLoreLine(Map.Entry<EffectType, Integer> entry, boolean active) {
    String colorCode = active ? "§a" : "§7";
    return colorCode + "[" + entry.getKey().getDisplayName() + " " + entry.getValue() + "%]";
  }
}
