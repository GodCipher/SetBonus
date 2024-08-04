package de.godcipher.setbonus.set;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class SetBonusStats {

  public static final SetBonusStats EMPTY = new SetBonusStats(new EnumMap<>(StatType.class));

  private final Map<StatType, Integer> stats;

  SetBonusStats(Map<StatType, Integer> stats) {
    this.stats = new EnumMap<>(StatType.class);
    this.stats.putAll(stats);
  }

  /**
   * Creates a new SetBonusStats object with a percentage of total stats. Transfers the specified
   * percentage of non-zero stats into a new object.
   *
   * @param percentage The percentage of stats to include in the new object (e.g., 0.75 for 75%).
   * @return A new SetBonusStats object containing the selected stats.
   */
  public SetBonusStats getPercentageOfStats(double percentage) {
    if (percentage == 1.0) {
      return this;
    }

    List<StatType> nonZeroStats =
        stats.entrySet().stream()
            .filter(entry -> entry.getValue() > 0)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

    int numberOfStatsToInclude = (int) Math.round(nonZeroStats.size() * percentage);

    List<StatType> selectedStats = nonZeroStats.subList(0, numberOfStatsToInclude);

    Map<StatType, Integer> selectedStatValues = new EnumMap<>(StatType.class);
    for (StatType effect : StatType.values()) {
      selectedStatValues.put(effect, selectedStats.contains(effect) ? stats.get(effect) : 0);
    }

    return new SetBonusStats(selectedStatValues);
  }

  /**
   * Get the percentage value for a specific EffectType.
   *
   * @param statType The effect type to retrieve the percentage for.
   * @return The percentage value of the specified effect type.
   */
  public int getEffectPercentage(StatType statType) {
    return stats.getOrDefault(statType, 0);
  }
}
