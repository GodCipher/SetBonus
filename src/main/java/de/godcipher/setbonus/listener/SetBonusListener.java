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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.ItemStack;

public class SetBonusListener implements Listener {

  private final SetBonusMapper setBonusMapper;

  public SetBonusListener(SetBonusMapper setBonusMapper) {
    this.setBonusMapper = setBonusMapper;
  }

  /** Handles damage reduction based on the player's equipment set bonus. */
  @EventHandler
  public void onDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getEntity();
    double baseDamage = event.getDamage();

    SetType setType = PlayerEquipmentChecker.getSetType(player);
    if (setType == null) {
      return;
    }

    SetBonusStats setBonusStats = setBonusMapper.getSetBonusStatsMap().get(setType);
    SetBonusStats playerStats =
        setBonusStats.getPercentageOfStats(
            PlayerEquipmentChecker.getPlayerSetPercentage(player, setType));

    double reducedDamage = calculateDamageReduction(baseDamage, playerStats, event.getCause());
    event.setDamage(Math.max(0, reducedDamage));
  }

  /** Calculates the damage reduction based on various damage causes. */
  private double calculateDamageReduction(
      double baseDamage, SetBonusStats playerStats, EntityDamageEvent.DamageCause cause) {
    double reducedDamage = baseDamage;
    reducedDamage -= baseDamage * playerStats.getStats().get(StatType.DAMAGE_REDUCTION) / 100.0;

    switch (cause) {
      case FIRE:
        reducedDamage -=
            baseDamage * playerStats.getStats().get(StatType.FIRE_DAMAGE_REDUCTION) / 100.0;
        break;
      case PROJECTILE:
        reducedDamage -=
            baseDamage * playerStats.getStats().get(StatType.PROJECTILE_DAMAGE_REDUCTION) / 100.0;
        break;
      case ENTITY_EXPLOSION:
        reducedDamage -=
            baseDamage * playerStats.getStats().get(StatType.EXPLOSION_DAMAGE_REDUCTION) / 100.0;
        break;
      case FALL:
        reducedDamage -=
            baseDamage * playerStats.getStats().get(StatType.FALL_DAMAGE_REDUCTION) / 100.0;
        break;
      default:
        break;
    }
    return reducedDamage;
  }

  /** Adjusts food saturation level based on player's equipment set bonus. */
  @EventHandler
  public void onFoodSaturation(FoodLevelChangeEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

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
      int newFoodLevel =
          (int)
              (event.getFoodLevel()
                  * (1 + playerStats.getStats().get(StatType.FOOD_SATURATION) / 100.0));
      event.setFoodLevel(newFoodLevel);
    }
  }

  /** Handles damage dealing and reflection based on player's equipment set bonus. */
  @EventHandler
  public void onDamageDealing(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getDamager();
    SetType setType = PlayerEquipmentChecker.getSetType(player);
    if (setType == null) {
      return;
    }

    SetBonusStats setBonusStats = setBonusMapper.getSetBonusStatsMap().get(setType);
    SetBonusStats playerStats =
        setBonusStats.getPercentageOfStats(
            PlayerEquipmentChecker.getPlayerSetPercentage(player, setType));

    double baseDamage = event.getDamage();
    double increasedDamage =
        baseDamage * (1 + playerStats.getStats().get(StatType.MELEE_DAMAGE) / 100.0);
    event.setDamage(increasedDamage);

    if (playerStats.getStats().get(StatType.DAMAGE_REFLECTION) > 0
        && event.getEntity() instanceof LivingEntity) {
      double reflectedDamage =
          increasedDamage * playerStats.getStats().get(StatType.DAMAGE_REFLECTION) / 100.0;
      ((LivingEntity) event.getEntity()).damage(reflectedDamage, player);
    }
  }

  /** Adjusts experience gain based on player's equipment set bonus. */
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

    int newExpAmount =
        (int)
            (event.getAmount()
                * (1 + playerStats.getStats().get(StatType.EXPERIENCE_GAIN) / 100.0));
    event.setAmount(newExpAmount);
  }

  /** Handles monster invisibility based on player's equipment set bonus. */
  @EventHandler
  public void onTarget(EntityTargetLivingEntityEvent event) {
    if (!(event.getTarget() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getTarget();
    SetType setType = PlayerEquipmentChecker.getSetType(player);
    if (setType == null) {
      return;
    }

    SetBonusStats setBonusStats = setBonusMapper.getSetBonusStatsMap().get(setType);
    SetBonusStats playerStats =
        setBonusStats.getPercentageOfStats(
            PlayerEquipmentChecker.getPlayerSetPercentage(player, setType));

    if (Math.random() < playerStats.getStats().get(StatType.MONSTER_INVISIBILITY) / 100.0) {
      event.setCancelled(true);
    }
  }

  /** Adjusts regeneration amount based on player's equipment set bonus. */
  @EventHandler
  public void onRegeneration(EntityRegainHealthEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    Player player = (Player) event.getEntity();
    SetType setType = PlayerEquipmentChecker.getSetType(player);
    if (setType == null) {
      return;
    }

    SetBonusStats setBonusStats = setBonusMapper.getSetBonusStatsMap().get(setType);
    SetBonusStats playerStats =
        setBonusStats.getPercentageOfStats(
            PlayerEquipmentChecker.getPlayerSetPercentage(player, setType));

    double increasedRegen =
        event.getAmount() * (1 + playerStats.getStats().get(StatType.REGENERATION) / 100.0);
    event.setAmount(increasedRegen);
  }

  /** Handles increased drop chance based on player's equipment set bonus. */
  @EventHandler
  public void onKill(EntityDeathEvent event) {
    if (event.getEntity() instanceof Player || event.getEntity().getKiller() == null) {
      return;
    }

    Player player = event.getEntity().getKiller();
    SetType setType = PlayerEquipmentChecker.getSetType(player);
    if (setType == null) {
      return;
    }

    SetBonusStats setBonusStats = setBonusMapper.getSetBonusStatsMap().get(setType);
    SetBonusStats playerStats =
        setBonusStats.getPercentageOfStats(
            PlayerEquipmentChecker.getPlayerSetPercentage(player, setType));

    double dropChance = playerStats.getStats().get(StatType.DROP_CHANCE);
    if (dropChance > 0 && Math.random() < dropChance / 100.0) {
      event.getDrops().forEach(item -> item.setAmount(item.getAmount() * 2));
    }
  }

  /** Applies lore to armor when unequipped. */
  @EventHandler
  public void onArmorUnequip(ArmorEquipEvent armorEquipEvent) {
    ItemStack oldArmorPiece = armorEquipEvent.getOldArmorPiece();
    if (oldArmorPiece == null
        || oldArmorPiece.getType() == Material.AIR
        || !EnchantmentTarget.ARMOR.includes(oldArmorPiece)) {
      return;
    }

    SetType setType = SetType.determineSetType(oldArmorPiece);
    SetBonusStats setBonusStats = setBonusMapper.getSetBonusStatsMap().get(setType);

    ItemStackUtil.applyLoreToItem(oldArmorPiece, setBonusStats, false, 0);
  }
}
