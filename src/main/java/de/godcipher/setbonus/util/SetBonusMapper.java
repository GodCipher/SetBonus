package de.godcipher.setbonus.util;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class SetBonusMapper {

  private final Map<SetType, SetBonusStats> setBonusStatsMap = new HashMap<>();

  public void fromConfig(FileConfiguration configuration) {
    for (SetType setType : SetType.values()) {
      ConfigurationSection section = configuration.getConfigurationSection(setType.getArmorType());
      if (section != null) {
        Map<EffectType, Integer> effectStats = new EnumMap<>(EffectType.class);

        for (EffectType effectType : EffectType.values()) {
          int value = section.getInt(effectType.getConfigName(), 0);
          effectStats.put(effectType, value);
        }

        SetBonusStats bonusStats = new SetBonusStats(effectStats);
        setBonusStatsMap.put(setType, bonusStats);
      }
    }
  }

  public Map<SetType, SetBonusStats> getSetBonusStatsMap() {
    return setBonusStatsMap;
  }
}
