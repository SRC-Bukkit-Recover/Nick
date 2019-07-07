package me.hsgamer.nick.enums;

import java.util.Arrays;

public enum ConfigEnum {
  // MESSAGES
  PREFIX("messages.prefix", "&f[&e&lNick&f] "),
  PLAYER_ONLY("messages.player-only", "&cYou must be a player to do this"),
  SIGN_LINES("messages.sign-lines",
      Arrays.asList("^^^^^^^^^^^^^", "&aType the nickname that", "&ayou want to change")),
  NO_PERMISSION("messages.no-permission", "&cYou don't have permission to do this"),
  SUCCESSFUL("messages.successful", "&aSuccessfully changed to %name%"),
  NOT_BLANK("messages.not-blank", "&cYou should not leave your nickname blank"),
  SAME_NAME("messages.same-name",
      "&cYou have the same name as your nickname, You don't need to do this"),
  // SETTINGS
  PERMISSION_NICK("settings.permissions.nick", "nick.nick"),
  PERMISSION_UNNICK("settings.permissions.unnick", "nick.unnick"),
  PERMISSION_COLOR("settings.permissions.color", "nick.color"),
  PERMISSION_TABLIST_NAME("settings.permissions.tablist-name", "nick.tablist"),
  SET_PLAYER_LIST_NAME("settings.change-tablist-name", false),
  SET_NAME_TAG("settings.change-name-tag", false);
  String path;
  Object def;

  ConfigEnum(String path, Object def) {
    this.path = path;
    this.def = def;
  }

  public String getPath() {
    return path;
  }

  public Object getDef() {
    return def;
  }
}
