package de.godcipher.setbonus.listener;

import com.jeff_media.armorequipevent.ArmorEquipEvent;
import de.godcipher.setbonus.set.SetBonusMapper;
import de.godcipher.setbonus.set.SetBonusStats;
import de.godcipher.setbonus.set.SetType;
import de.godcipher.setbonus.set.StatType;
import de.godcipher.setbonus.util.ItemStackUtil;
import de.godcipher.setbonus.util.PlayerEquipmentChecker;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.ItemStack;

public class SetBonusListener implements Listener {

  private final SetBonusMapper setBonusMapper;

  public SetBonusListener(SetBonusMapper setBonusMapper) {
    this.setBonusMapper = setBonusMapper;
  }

  @EventHandler
  public void onDamage(EntityDamageEvent event) {
    if (event.getEntity() instanceof Player) {
      Player player = (Player) event.getEntity();
      double baseDamage = event.getDamage();
      double reducedDamage = baseDamage;

      SetType setType = PlayerEquipmentChecker.getSetType(player);
      if (setType == null) {
        return;
      }

      SetBonusStats setBonusStats = setBonusMapper.getSetBonusStatsMap().get(setType);
      SetBonusStats playerStats =
          setBonusStats.getPercentageOfStats(
              PlayerEquipmentChecker.getPlayerSetPercentage(player, setType));

      reducedDamage -= baseDamage * playerStats.getStats().get(StatType.DAMAGE_REDUCTION) / 100.0;
      if (event.getCause() == EntityDamageEvent.DamageCause.FIRE) {
        reducedDamage -=
            baseDamage * playerStats.getStats().get(StatType.FIRE_DAMAGE_REDUCTION) / 100.0;
      }

      if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
        reducedDamage -=
            baseDamage * playerStats.getStats().get(StatType.PROJECTILE_DAMAGE_REDUCTION) / 100.0;
      }

      if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
        reducedDamage -=
            baseDamage * playerStats.getStats().get(StatType.EXPLOSION_DAMAGE_REDUCTION) / 100.0;
      }

      reducedDamage = Math.max(0, reducedDamage);
      event.setDamage(reducedDamage);
    }
  }

  @EventHandler
  public void onFoodSaturation(FoodLevelChangeEvent event) {
    if (event.getEntity() instanceof Player) {
      Player player = (Player) event.getEntity();
      SetType setType = PlayerEquipmentChecker.getSetType(player);
      if (setType == null) {
        return;
      }

      SetBonusStats setBonusStats = setBonusMapper.getSetBonusStatsMap().get(setType);
      SetBonusStats playerStats =
          setBonusStats.getPercentageOfStats(
              PlayerEquipmentChecker.getPlayerSetPercentage(player, setType));

      if (event.getFoodLevel() > player.getFoodLevel()) {
        event.setFoodLevel(
            (int)
                (event.getFoodLevel()
                    * (1 + playerStats.getStats().get(StatType.FOOD_SATURATION) / 100.0)));
      }
    }
  }

  @EventHandler
  public void onDamageDealing(EntityDamageByEntityEvent event) {
    if (event.getDamager() instanceof Player) {
      Player player = (Player) event.getDamager();
      SetType setType = PlayerEquipmentChecker.getSetType(player);
      if (setType == null) {
        return;
      }

      SetBonusStats setBonusStats = setBonusMapper.getSetBonusStatsMap().get(setType);
      SetBonusStats playerStats =
          setBonusStats.getPercentageOfStats(
              PlayerEquipmentChecker.getPlayerSetPercentage(player, setType));

      double damage = event.getDamage();
      event.setDamage(damage * (1 + playerStats.getStats().get(StatType.MELEE_DAMAGE) / 100.0));
    }
  }

  @EventHandler
  public void onExperienceGain(PlayerExpChangeEvent event) {
    Player player = event.getPlayer();
    SetType setType = PlayerEquipmentChecker.getSetType(player);
    if (setType == null) {
      return;
    }

    SetBonusStats setBonusStats = setBonusMapper.getSetBonusStatsMap().get(setType);
    SetBonusStats playerStats =
        setBonusStats.getPercentageOfStats(
            PlayerEquipmentChecker.getPlayerSetPercentage(player, setType));

    event.setAmount(
        (int)
            (event.getAmount()
                * (1 + playerStats.getStats().get(StatType.EXPERIENCE_GAIN) / 100.0)));
  }

  @EventHandler
  public void onRegeneration(EntityRegainHealthEvent event) {
    if (event.getEntity() instanceof Player) {
      Player player = (Player) event.getEntity();
      SetType setType = PlayerEquipmentChecker.getSetType(player);
      if (setType == null) {
        return;
      }

      SetBonusStats setBonusStats = setBonusMapper.getSetBonusStatsMap().get(setType);
      SetBonusStats playerStats =
          setBonusStats.getPercentageOfStats(
              PlayerEquipmentChecker.getPlayerSetPercentage(player, setType));

      event.setAmount(
          event.getAmount() * (1 + playerStats.getStats().get(StatType.REGENERATION) / 100.0));
    }
  }

  @EventHandler
  public void onKill(EntityDeathEvent event) {
    if (!(event.getEntity() instanceof Player) && event.getEntity().getKiller() != null) {
      Player player = event.getEntity().getKiller();
      SetType setType = PlayerEquipmentChecker.getSetType(player);
      if (setType == null) {
        return;
      }

      SetBonusStats setBonusStats = setBonusMapper.getSetBonusStatsMap().get(setType);
      SetBonusStats playerStats =
          setBonusStats.getPercentageOfStats(
              PlayerEquipmentChecker.getPlayerSetPercentage(player, setType));

      if (playerStats.getStats().get(StatType.DROP_CHANCE) > 0) {
        if (Math.random() < playerStats.getStats().get(StatType.DROP_CHANCE) / 100.0) {
          event.getDrops().forEach(item -> item.setAmount(item.getAmount() * 2));
        }
      }
    }
  }

  @EventHandler
  public void onArmorUnequip(ArmorEquipEvent armorEquipEvent) {
    ItemStack oldArmorPiece = armorEquipEvent.getOldArmorPiece();
    if (oldArmorPiece == null || oldArmorPiece.getType() == Material.AIR) {
      return;
    }

    if (!EnchantmentTarget.ARMOR.includes(oldArmorPiece)) {
      return;
    }

    ItemStackUtil.applyLoreToItem(
        oldArmorPiece,
        setBonusMapper.getSetBonusStatsMap().get(ItemStackUtil.determineSetType(oldArmorPiece)),
        false,
        0);
  }
}
