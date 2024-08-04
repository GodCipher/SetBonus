package de.godcipher.setbonus;

import com.jeff_media.armorequipevent.ArmorEquipEvent;
import de.godcipher.setbonus.listener.SetBonusListener;
import de.godcipher.setbonus.scheduler.EquipmentUpdateScheduler;
import de.godcipher.setbonus.scheduler.PassiveStatsScheduler;
import de.godcipher.setbonus.set.EffectType;
import de.godcipher.setbonus.set.SetBonusMapper;
import de.godcipher.setbonus.set.SetType;
import java.io.File;
import java.util.EnumMap;
import java.util.Map;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SetBonusPlugin extends JavaPlugin {

  private final SetBonusMapper setBonusMapper = new SetBonusMapper();

  @Override
  public void onEnable() {
    setupBStats();
    generateDefaultConfig();
    loadConfig();
    loadFromConfig();
    startScheduler();
    registerListener();
  }

  @Override
  public void onDisable() {}

  private void loadConfig() {
    getConfig().options().copyDefaults(true);
    saveConfig();
  }

  private void loadFromConfig() {
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

    if (!configFile.exists()) {
      getConfig().options().header("ALL VALUES ARE PERCENTAGES");

      for (SetType setType : SetType.values()) {
        Map<EffectType, Integer> defaultValues = new EnumMap<>(EffectType.class);

        for (EffectType effectType : EffectType.values()) {
          defaultValues.put(effectType, 0);
        }

        for (Map.Entry<EffectType, Integer> entry : defaultValues.entrySet()) {
          getConfig().set(setType.name() + "." + entry.getKey().getConfigName(), entry.getValue());
        }
      }

      saveConfig();
    }
  }
}
