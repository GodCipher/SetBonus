package de.godcipher.setbonus.set;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
public enum StatType {
  SPEED("speed", "Speed"),
  MELEE_DAMAGE("melee-damage", "Melee Damage"),
  PROJECTILE_DAMAGE_REDUCTION("projectile-damage-reduction", "Projectile Damage Reduction"),
  DAMAGE_REDUCTION("damage-reduction", "Damage Reduction"),
  FIRE_DAMAGE_REDUCTION("fire-damage-reduction", "Fire Damage Reduction"),
  DROP_CHANCE("drop-chance", "Drop Chance"),
  EXPERIENCE_GAIN("experience-gain", "Experience Gain"),
  REGENERATION("regeneration", "Regeneration"),
  FOOD_SATURATION("food-saturation", "Food Saturation"),
  EXPLOSION_DAMAGE_REDUCTION("explosion-damage-reduction", "Explosion Damage Reduction"),
  MAX_HEALTH("max-health", "Max Health");

  private final String configName;
  @Setter private String displayName;
  private final String defaultDisplayName;

  StatType(String configName, String displayName) {
    this.configName = configName;
    this.displayName = displayName;
    this.defaultDisplayName = displayName;
  }

  /** Resets the display name to its default value. */
  public void resetDisplayName() {
    this.displayName = this.defaultDisplayName;
  }

  /**
   * Finds an EffectType by its configuration name.
   *
   * @param configName The configuration name to look for.
   * @return The matching EffectType, or null if none found.
   */
  public static StatType fromConfigName(String configName) {
    for (StatType type : StatType.values()) {
      if (type.getConfigName().equalsIgnoreCase(configName)) {
        return type;
      }
    }
    return null;
  }

  /**
   * Loads display names from a configuration map.
   *
   * @param configMap A map containing configuration names and display names.
   */
  public static void loadDisplayNamesFromConfig(Map<String, String> configMap) {
    for (StatType type : StatType.values()) {
      String newDisplayName = configMap.get(type.getConfigName());
      if (newDisplayName != null) {
        type.setDisplayName(newDisplayName);
      }
    }
  }
}
