package de.godcipher.setbonus.scheduler;

import de.godcipher.setbonus.util.ItemStackUtil;
import de.godcipher.setbonus.util.PlayerEquipmentChecker;
import de.godcipher.setbonus.set.SetBonusMapper;
import de.godcipher.setbonus.set.SetType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EquipmentUpdateScheduler implements Runnable {

  private static final double SET_COMPLETE_THRESHOLD = 0.5;

  private final SetBonusMapper setBonusMapper;

  public EquipmentUpdateScheduler(SetBonusMapper setBonusMapper) {
    this.setBonusMapper = setBonusMapper;
  }

  @Override
  public void run() {
    Bukkit.getOnlinePlayers().forEach(this::processPlayer);
  }

  private void processPlayer(Player player) {
    applyBonuses(player);
  }

  private void applyBonuses(Player player) {
    SetType setType = PlayerEquipmentChecker.getSetType(player);
    double setPercentage =
        setType != null ? PlayerEquipmentChecker.getPlayerSetPercentage(player, setType) : 0.0;
    boolean setComplete = setPercentage >= SET_COMPLETE_THRESHOLD;
    applyLoreToEquipment(player.getInventory().getArmorContents(), setComplete, setPercentage);
  }

  private void applyLoreToEquipment(ItemStack[] equipment, boolean active, double setPercentage) {
    if (equipment == null) return;
    int itemsProcessed = 0;
    for (ItemStack itemStack : equipment) {
      if (itemStack == null || !ItemStackUtil.isArmor(itemStack)) continue;
      itemsProcessed++;
      boolean isActive = itemsProcessed / 4.0 <= setPercentage && active;
      SetType setType = SetType.determineSetType(itemStack);
      ItemStackUtil.applyLoreToItem(
          itemStack, setBonusMapper.getSetBonusStatsMap().get(setType), isActive, setPercentage);
    }
  }
}
