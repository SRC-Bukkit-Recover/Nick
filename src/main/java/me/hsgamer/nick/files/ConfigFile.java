package me.hsgamer.nick.files;

import java.io.File;
import java.io.IOException;
import me.hsgamer.nick.enums.ConfigEnum;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigFile {

  private final JavaPlugin plugin;
  private final String filename = "config.yml";
  private FileConfiguration config;
  private File configFile;

  public ConfigFile(JavaPlugin plugin) {
    this.plugin = plugin;
    setUpConfig();
    setDefault();
  }

  private void setUpConfig() {
    configFile = new File(plugin.getDataFolder(), filename);
    if (!configFile.exists()) {
      configFile.getParentFile().mkdirs();
      config = YamlConfiguration.loadConfiguration(configFile);
      plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Created " + filename);
    } else {
      config = YamlConfiguration.loadConfiguration(configFile);
      plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Loaded " + filename);
    }
  }

  public void reloadConfig() {
    config = YamlConfiguration.loadConfiguration(configFile);
  }

  public void saveConfig() {
    try {
      config.save(configFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public FileConfiguration getConfig() {
    if (config == null) {
      setUpConfig();
    }
    return config;
  }

  private void setDefault() {
    for (ConfigEnum configEnum : ConfigEnum.class.getEnumConstants()) {
      getConfig().addDefault(configEnum.getPath(), configEnum.getDef());
    }
    getConfig().options().copyDefaults(true);
    saveConfig();
  }
}
