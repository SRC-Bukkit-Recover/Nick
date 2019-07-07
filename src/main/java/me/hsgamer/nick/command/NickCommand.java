package me.hsgamer.nick.command;

import java.util.ArrayList;
import java.util.List;
import me.hsgamer.nick.Nick;
import me.hsgamer.nick.enums.ConfigEnum;
import me.hsgamer.nick.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label,
      String[] args) {
    if (sender instanceof Player) {
      if (sender.hasPermission((String) Utils.getValueFromConfig(ConfigEnum.PERMISSION_NICK))) {
        openGUI((Player) sender);
      } else {
        Utils.sendMessage(sender, ConfigEnum.NO_PERMISSION);
      }
    } else {
      Utils.sendMessage(sender, ConfigEnum.PLAYER_ONLY);
    }
    return true;
  }

  private void openGUI(Player sender) {
    List<String> lines = new ArrayList<>();
    lines.add("");
    lines.addAll((List<String>) Utils.getValueFromConfig(ConfigEnum.SIGN_LINES));
    Nick.getInstance().getSignMenuFactory()
        .newMenu(sender, lines)
        .reopenIfFail()
        .response(((player, strings) -> {
          String nick = StringUtils.strip(strings[0]);
          if (nick.isEmpty()) {
            Utils.sendMessage(player, ConfigEnum.NOT_BLANK);
          } else {
            Utils.setDisplayName(player, nick, true);
            Utils.setPlayerListName(player, nick, true);
            Utils.refreshPlayer(player);
            Utils.sendMessage(player,
                String.valueOf(Utils.getValueFromConfig(ConfigEnum.SUCCESSFUL))
                    .replace("%name%", nick), true);
          }
          return true;
        }))
        .open();
  }
}
