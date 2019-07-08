package me.hsgamer.nick.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.Lists;
import java.util.List;
import me.hsgamer.nick.Nick;
import me.hsgamer.nick.enums.ConfigEnum;
import me.hsgamer.nick.utils.wrapper.WrapperPlayServerPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Utils {

  public static void sendMessage(CommandSender sender, String message, boolean enablePrefix) {
    if (enablePrefix) {
      String prefix = (String) getValueFromConfig(ConfigEnum.PREFIX);
      sender.sendMessage(colornize(prefix + message));
    } else {
      sender.sendMessage(colornize(message));
    }
  }

  public static void sendMessage(CommandSender sender, ConfigEnum configEnum) {
    String message = (String) getValueFromConfig(configEnum);

    sendMessage(sender, message, true);
  }

  public static Object getValueFromConfig(ConfigEnum configEnum) {
    return Nick.getInstance().getConfigFile().getConfig()
        .get(configEnum.getPath(), configEnum.getDef());
  }

  public static String colornize(String input) {
    return ChatColor.translateAlternateColorCodes('&', input);
  }

  public static void setDisplayName(Player player, String nick, boolean colornize) {
    if (colornize && player
        .hasPermission((String) getValueFromConfig(ConfigEnum.PERMISSION_COLOR))) {
      nick = colornize(nick);
    }
    if (Nick.getInstance().IS_ESSENTIALS_ENABLED) {
      Nick.getInstance().getEssentials().getUser(player).setNickname(nick);
    } else {
      player.setDisplayName(nick);
    }
  }

  public static String getNick(Player player) {
    if (Nick.getInstance().IS_ESSENTIALS_ENABLED) {
      return Nick.getInstance().getEssentials().getUser(player).getNickname();
    } else {
      return player.getDisplayName();
    }
  }

  public static void setPlayerListName(Player player, String nick, boolean colornize) {
    if ((boolean) getValueFromConfig(ConfigEnum.SET_PLAYER_LIST_NAME) && player
        .hasPermission((String) getValueFromConfig(ConfigEnum.PERMISSION_TABLIST_NAME))) {
      if (colornize && player
          .hasPermission((String) getValueFromConfig(ConfigEnum.PERMISSION_COLOR))) {
        nick = colornize(nick);
      }
      player.setPlayerListName(nick);
    }
  }

  public static void refreshPlayer(Player target) {
    if ((boolean) getValueFromConfig(ConfigEnum.SET_NAME_TAG)) {
      Bukkit.getScheduler().runTask(Nick.getInstance(), () -> {
        for (Player player : Bukkit.getOnlinePlayers()) {
          if (Nick.IS_LEGACY) {
            player.hidePlayer(target);
            player.showPlayer(target);
          } else {
            player.hidePlayer(Nick.getInstance(), target);
            player.showPlayer(Nick.getInstance(), target);
          }
        }
      });
    }
  }

  public static void addChangeNameTagListener() {
    ProtocolLibrary.getProtocolManager().addPacketListener(
        new PacketAdapter(Nick.getInstance(), PacketType.Play.Server.PLAYER_INFO) {

          @Override
          public void onPacketSending(PacketEvent event) {
            WrapperPlayServerPlayerInfo wrapper = new WrapperPlayServerPlayerInfo(
                event.getPacket());

            List<PlayerInfoData> playerInfoDataList = wrapper.getData();

            if (wrapper.getAction() != PlayerInfoAction.ADD_PLAYER) {
              return;
            }

            List<PlayerInfoData> newPlayerInfoDataList = Lists.newArrayList();

            for (PlayerInfoData playerInfoData : playerInfoDataList) {
              Player player;

              if (playerInfoData == null || playerInfoData.getProfile() == null
                  || (player = Bukkit.getPlayer(playerInfoData.getProfile().getUUID())) == null
                  || !player.isOnline()) {
                newPlayerInfoDataList.add(playerInfoData);
                continue;
              }

              WrappedGameProfile profile = playerInfoData.getProfile();

              String newNick = getNick(player);

              WrappedGameProfile newProfile = profile.withName(newNick);
              newProfile.getProperties().putAll(profile.getProperties());

              PlayerInfoData newPlayerInfoData = new PlayerInfoData(newProfile,
                  playerInfoData.getPing(), playerInfoData.getGameMode(),
                  playerInfoData.getDisplayName());
              newPlayerInfoDataList.add(newPlayerInfoData);
            }

            wrapper.setData(newPlayerInfoDataList);
          }
        });
  }
}
