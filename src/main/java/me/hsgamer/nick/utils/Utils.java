package me.hsgamer.nick.utils;

import me.hsgamer.nick.Nick;
import me.hsgamer.nick.enums.ConfigEnum;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Utils {
    public static void sendMessage(CommandSender sender, String message, boolean enablePrefix) {
        String prefix = (String) getValueFromConfig(ConfigEnum.PREFIX);
        if (enablePrefix) {
            sender.sendMessage(colornize(prefix + message));
        } else {
            sender.sendMessage(colornize(message));
        }
    }

    public static void sendMessage(CommandSender sender, ConfigEnum configEnum) {
        String prefix = (String) getValueFromConfig(ConfigEnum.PREFIX);
        String message = (String) getValueFromConfig(configEnum);

        sendMessage(sender, prefix + message, true);
    }

    public static Object getValueFromConfig(ConfigEnum configEnum) {
        return Nick.getInstance().getConfigFile().getConfig().get(configEnum.getPath(), configEnum.getDef());
    }

    public static String colornize(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static void setDisplayName(Player player, String input) {
        if (Nick.getInstance().IS_ESSENTIALS_ENABLED) {
            Nick.getInstance().getEssentials().getUser(player).setNickname(input);
        } else {
            player.setDisplayName(input);
        }
    }
}
