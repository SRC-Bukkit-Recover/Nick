package me.hsgamer.nick.command;

import me.hsgamer.nick.enums.ConfigEnum;
import me.hsgamer.nick.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnNickCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
      if (sender.hasPermission((String) Utils.getValueFromConfig(ConfigEnum.PERMISSION_UNNICK))) {
        if (!((Player) sender).getDisplayName().equals(sender.getName())) {
          Utils.setDisplayName((Player) sender, sender.getName(), false);
          Utils.sendMessage(sender, String.valueOf(Utils.getValueFromConfig(ConfigEnum.SUCCESSFUL))
              .replace("%name%", sender.getName()), true);
        } else {
          Utils.sendMessage(sender, ConfigEnum.SAME_NAME);
        }
      } else {
        Utils.sendMessage(sender, ConfigEnum.NO_PERMISSION);
      }
    } else {
      Utils.sendMessage(sender, ConfigEnum.PLAYER_ONLY);
    }
    return true;
  }
}
