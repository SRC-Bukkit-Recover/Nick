package me.hsgamer.nick;

import com.earth2me.essentials.Essentials;
import me.hsgamer.nick.command.NickCommand;
import me.hsgamer.nick.command.UnNickCommand;
import me.hsgamer.nick.files.ConfigFile;
import me.hsgamer.nick.utils.SignMenuFactory;
import me.hsgamer.nick.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class Nick extends JavaPlugin {

    private static Nick instance;
    public boolean IS_ESSENTIALS_ENABLED;
    private SignMenuFactory signMenuFactory;
    private ConfigFile configFile;
    private Essentials essentials;

    public static Nick getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        Utils.sendMessage(getServer().getConsoleSender(), "&b&l===================================", false);
        Utils.sendMessage(getServer().getConsoleSender(), "&e&lPlugin: &f" + getDescription().getName(), false);
        Utils.sendMessage(getServer().getConsoleSender(), "&e&lVersion: &f" + getDescription().getVersion(), false);
        Utils.sendMessage(getServer().getConsoleSender(), "&e&lAuthors: &f" + getDescription().getAuthors(), false);
        Utils.sendMessage(getServer().getConsoleSender(), "&b&l===================================", false);

        this.configFile = new ConfigFile(this);
        this.signMenuFactory = new SignMenuFactory(this);

        if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            IS_ESSENTIALS_ENABLED = true;
            essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
        }

        getServer().getPluginCommand("nick").setExecutor(new NickCommand());
        getServer().getPluginCommand("unnick").setExecutor(new UnNickCommand());
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

    public Essentials getEssentials() {
        return essentials;
    }
}
