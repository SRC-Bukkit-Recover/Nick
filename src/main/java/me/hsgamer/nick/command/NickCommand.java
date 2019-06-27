package me.hsgamer.nick.command;

import me.hsgamer.nick.Nick;
import me.hsgamer.nick.enums.ConfigEnum;
import me.hsgamer.nick.utils.Utils;
import me.hsgamer.nick.utils.signgui.legacy.Line;
import me.hsgamer.nick.utils.signgui.legacy.SignGui;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NickCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
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
        if (Nick.IS_LEGACY) {
            String[] lines = ((List<String>) Utils.getValueFromConfig(ConfigEnum.SIGN_LINES)).toArray(new String[0]);
            new SignGui()
                    .line(Line.FIRST, "")
                    .line(Line.SECOND, Utils.colornize(lines[0]))
                    .line(Line.THIRD, Utils.colornize(lines[1]))
                    .line(Line.FOURTH, Utils.colornize(lines[2]))
                    .listener(((player, strings) -> {
                        String nick = StringUtils.strip(strings[0]);
                        if (nick.isEmpty()) {
                            Utils.sendMessage(player, ConfigEnum.NOT_BLANK);
                        } else {
                            Utils.setDisplayName(player, nick, true);
                            Utils.sendMessage(player, String.valueOf(Utils.getValueFromConfig(ConfigEnum.SUCCESSFUL)).replace("%name%", nick), true);
                        }
                    }))
                    .show(sender);
        } else {
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
                            Utils.sendMessage(player, String.valueOf(Utils.getValueFromConfig(ConfigEnum.SUCCESSFUL)).replace("%name%", nick), true);
                        }
                        return true;
                    }))
                    .open();
        }
    }
}
