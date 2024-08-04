package de.godcipher.setbonus;

import com.jeff_media.armorequipevent.ArmorEquipEvent;
import de.godcipher.setbonus.listener.SetBonusListener;
import de.godcipher.setbonus.scheduler.EquipmentUpdateScheduler;
import de.godcipher.setbonus.scheduler.PassiveStatsScheduler;
import de.godcipher.setbonus.set.StatType;
import de.godcipher.setbonus.set.SetBonusMapper;
import de.godcipher.setbonus.set.SetType;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class SetBonusPlugin extends JavaPlugin {

  private final SetBonusMapper setBonusMapper = new SetBonusMapper();

  @Override
  public void onEnable() {
    setupBStats();
    loadConfig();
    generateAndMergeConfig();
    loadFromConfig();
    startScheduler();
    registerListener();
  }

  @Override
  public void onDisable() {}

  private void loadConfig() {
    getConfig().options().copyDefaults(true);
    saveDefaultConfig();
  }

  private void loadFromConfig() {
    updateEffectTypeDisplayNames();
    setBonusMapper.fromConfig(getConfig());
  }

  private void startScheduler() {
    getServer()
            .getScheduler()
            .runTaskTimer(this, new EquipmentUpdateScheduler(setBonusMapper), 0, 20);
    getServer().getScheduler().runTaskTimer(this, new PassiveStatsScheduler(setBonusMapper), 0, 20);
  }

  private void registerListener() {
    ArmorEquipEvent.registerListener(this);
    Bukkit.getPluginManager().registerEvents(new SetBonusListener(setBonusMapper), this);
  }

  private void setupBStats() {
    new Metrics(this, 22885);
  }

  private void generateAndMergeConfig() {
    File configFile = new File(getDataFolder(), "config.yml");
    FileConfiguration existingConfig = YamlConfiguration.loadConfiguration(configFile);

    try (PrintWriter writer = new PrintWriter(configFile)) {
      writer.println("# ALL VALUES ARE PERCENTAGES\n");
      writeDisplayNames(writer, existingConfig);
      writeSetBonusValues(writer, existingConfig);
    } catch (IOException e) {
      getLogger().log(Level.SEVERE, "Could not create config.yml", e);
    }
  }

  private void writeDisplayNames(PrintWriter writer, FileConfiguration existingConfig) {
    writer.println("# Display Names");
    for (StatType statType : StatType.values()) {
      String key = statType.getConfigName() + "-display-name";
      String displayName = existingConfig.contains(key) ? existingConfig.getString(key) : statType.getDisplayName();
      writer.printf("%s-display-name: %s%n", statType.getConfigName(), displayName);
    }
    writer.println();
  }

  private void writeSetBonusValues(PrintWriter writer, FileConfiguration existingConfig) {
    writer.println("# Set Bonus Stats");
    for (SetType setType : SetType.values()) {
      writer.println(setType.name() + ":");
      for (StatType statType : StatType.values()) {
        String effectKey = statType.getConfigName();
        String fullKey = setType.name() + "." + effectKey;
        int value = existingConfig.contains(fullKey) ? existingConfig.getInt(fullKey) : 0;
        writer.printf("  %s: %d%n", effectKey, value);
      }
      writer.println();
    }
  }

  private void updateEffectTypeDisplayNames() {
    for (StatType statType : StatType.values()) {
      String configName = statType.getConfigName();
      statType.setDisplayName(getConfig().getString(configName + "-display-name"));
    }
  }
}
