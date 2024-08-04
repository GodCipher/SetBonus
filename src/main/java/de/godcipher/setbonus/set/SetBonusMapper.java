package de.godcipher.setbonus.set;

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
        Map<StatType, Integer> effectStats = new EnumMap<>(StatType.class);

        for (StatType statType : StatType.values()) {
          int value = section.getInt(statType.getConfigName(), 0);
          effectStats.put(statType, value);
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
