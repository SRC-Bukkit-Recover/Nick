package me.hsgamer.nick;

import me.hsgamer.nick.files.ConfigFile;
import me.hsgamer.nick.utils.SignMenuFactory;
import me.hsgamer.nick.utils.Utils;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class Nick extends JavaPlugin {

    private static Nick instance;
    private SignMenuFactory signMenuFactory;
    private ConfigFile configFile;

    public static Nick getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        Utils.sendMessage(getServer().getConsoleSender(), "&b&l===================================");
        Utils.sendMessage(getServer().getConsoleSender(), "&e&lPlugin: &f" + getDescription().getName());
        Utils.sendMessage(getServer().getConsoleSender(), "&e&lVersion: &f" + getDescription().getVersion());
        Utils.sendMessage(getServer().getConsoleSender(), "&e&lAuthors: &f" + getDescription().getAuthors());
        Utils.sendMessage(getServer().getConsoleSender(), "&b&l===================================");

        this.configFile = new ConfigFile(this);
        this.signMenuFactory = new SignMenuFactory(this);

        getServer().getPluginCommand("nick").setExecutor(new Command());
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        instance = null;
        this.configFile = null;
        this.signMenuFactory = null;
    }

    public SignMenuFactory getSignMenuFactory() {
        return signMenuFactory;
    }

    public ConfigFile getConfigFile() {
        return configFile;
    }
}
