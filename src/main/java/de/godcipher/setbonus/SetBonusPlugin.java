package de.godcipher.setbonus;

import com.jeff_media.armorequipevent.ArmorEquipEvent;
import de.godcipher.setbonus.listener.SetBonusListener;
import de.godcipher.setbonus.scheduler.EquipmentUpdateScheduler;
import de.godcipher.setbonus.scheduler.PassiveStatsScheduler;
import de.godcipher.setbonus.set.EffectType;
import de.godcipher.setbonus.set.SetBonusMapper;
import de.godcipher.setbonus.set.SetType;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SetBonusPlugin extends JavaPlugin {

  private final SetBonusMapper setBonusMapper = new SetBonusMapper();

  @Override
  public void onEnable() {
    setupBStats();
    loadConfig();
    generateDefaultConfig();
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

  private void generateDefaultConfig() {
    File configFile = new File(getDataFolder(), "config.yml");

    if (isConfigEmpty()) {
      try (PrintWriter writer = new PrintWriter(configFile)) {
        writer.println("# ALL VALUES ARE PERCENTAGES\n");

        writer.println("# Display Names");
        for (EffectType effectType : EffectType.values()) {
          writer.println(
              effectType.getConfigName() + "-display-name: " + effectType.getDisplayName());
        }
        writer.println();

        writer.println("# Set Bonus Values");
        for (SetType setType : SetType.values()) {
          writer.println(setType.name() + ":");
          for (EffectType effectType : EffectType.values()) {
            String configName = effectType.getConfigName();
            writer.println("  " + configName + ": " + 0);
          }
          writer.println();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private boolean isConfigEmpty() {
    return getConfig().getKeys(false).isEmpty();
  }

  private void updateEffectTypeDisplayNames() {
    for (EffectType effectType : EffectType.values()) {
      String configName = effectType.getConfigName();
      effectType.setDisplayName(getConfig().getString(configName + "-display-name"));
    }
  }
}
