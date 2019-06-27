package me.hsgamer.nick;

import com.earth2me.essentials.Essentials;
import me.hsgamer.nick.command.NickCommand;
import me.hsgamer.nick.command.UnNickCommand;
import me.hsgamer.nick.files.ConfigFile;
import me.hsgamer.nick.utils.Utils;
import me.hsgamer.nick.utils.signgui.current.SignMenuFactory;
import me.hsgamer.nick.utils.signgui.legacy.SignLocator;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class Nick extends JavaPlugin {

    public static boolean IS_LEGACY = false;
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

        IS_LEGACY = getServer().getVersion().contains("1.8");

        Utils.sendMessage(getServer().getConsoleSender(), "&b&l===================================", false);
        Utils.sendMessage(getServer().getConsoleSender(), "&e&lPlugin: &f" + getDescription().getName(), false);
        Utils.sendMessage(getServer().getConsoleSender(), "&e&lVersion: &f" + getDescription().getVersion(), false);
        Utils.sendMessage(getServer().getConsoleSender(), "&e&lAuthors: &f" + getDescription().getAuthors(), false);
        Utils.sendMessage(getServer().getConsoleSender(), "&b&l===================================", false);

        this.configFile = new ConfigFile(this);

        if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
            IS_ESSENTIALS_ENABLED = true;
            essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
        }

        if (IS_LEGACY) {
            Utils.sendMessage(getServer().getConsoleSender(), "&eYou are using an old version of your server. &bInitializing old listener...", true);

            getServer().getPluginManager().registerEvents(SignLocator.get(), this);
        } else {
            this.signMenuFactory = new SignMenuFactory(this);
        }

        getServer().getPluginCommand("nick").setExecutor(new NickCommand());
        getServer().getPluginCommand("unnick").setExecutor(new UnNickCommand());

        Utils.sendMessage(getServer().getConsoleSender(), "&aEnabled successfully. Enjoy ;)", true);
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
