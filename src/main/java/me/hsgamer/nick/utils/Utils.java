package me.hsgamer.nick.utils;

import me.hsgamer.nick.Nick;
import me.hsgamer.nick.enums.ConfigEnum;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Utils {
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(colornize(message));
    }

    public static void sendMessage(CommandSender sender, ConfigEnum configEnum) {
        String prefix = (String) getValueFromConfig(ConfigEnum.PREFIX);
        String message = (String) getValueFromConfig(configEnum);

        sendMessage(sender, prefix + message);
    }

    public static Object getValueFromConfig(ConfigEnum configEnum) {
        return Nick.getInstance().getConfigFile().getConfig().get(configEnum.getPath(), configEnum.getDef());
    }

    public static String colornize(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
