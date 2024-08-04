package de.godcipher.setbonus.scheduler;

import de.godcipher.setbonus.set.EffectType;
import de.godcipher.setbonus.util.PlayerEquipmentChecker;
import de.godcipher.setbonus.set.SetBonusMapper;
import de.godcipher.setbonus.set.SetBonusStats;
import de.godcipher.setbonus.set.SetType;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class PassiveStatsScheduler implements Runnable {

  private static final double PLAYER_DEFAULT_SPEED = 0.2;
  private static final double PLAYER_DEFAULT_HEALTH = 20.0;

  private final SetBonusMapper setBonusMapper;

  public PassiveStatsScheduler(SetBonusMapper setBonusMapper) {
    this.setBonusMapper = setBonusMapper;
  }

  @Override
  public void run() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      SetType setType = PlayerEquipmentChecker.getSetType(player);
      if (setType == null) {
        player.setWalkSpeed((float) PLAYER_DEFAULT_SPEED);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(PLAYER_DEFAULT_HEALTH);
        continue;
      }

      SetBonusStats setBonusStats = setBonusMapper.getSetBonusStatsMap().get(setType);
      SetBonusStats playerStats =
          setBonusStats.getPercentageOfStats(
              PlayerEquipmentChecker.getPlayerSetPercentage(player, setType));
      Map<EffectType, Integer> stats = playerStats.getStats();

      player.setWalkSpeed(
          (float)
              (PLAYER_DEFAULT_SPEED
                  + PLAYER_DEFAULT_SPEED * ((double) stats.get(EffectType.SPEED) / 100)));
      player
          .getAttribute(Attribute.GENERIC_MAX_HEALTH)
          .setBaseValue(
              PLAYER_DEFAULT_HEALTH
                  + PLAYER_DEFAULT_HEALTH * ((double) stats.get(EffectType.MAX_HEALTH) / 100));
    }
  }
}
