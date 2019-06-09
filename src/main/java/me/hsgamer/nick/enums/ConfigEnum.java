package me.hsgamer.nick.enums;

import java.util.Arrays;

public enum ConfigEnum {
    // MESSAGES
    PREFIX("messages.prefix", "&f[&e&lNick&f] "),
    PLAYER_ONLY("messages.player-only", "&cYou must be a player to do this"),
    SIGN_LINES("messages.sign-lines", Arrays.asList("^^^^^^^^^^^^^", "&aType the nickname that", "&ayou want to change")),
    NO_PERMISSION("messages.no-permission", "&cYou don't have permission to do this"),
    SUCCESSFUL("messages.successful", "&aSuccessfully changed to %name%"),
    NOT_BLANK("messages.not-blank", "&cYou should not leave your nickname blank"),
    // SETTINGS
    PERMISSION_NICK("settings.permissions.nick", "nick.nick"),
    PERMISSION_UNNICK("settings.permissions.unnick", "nick.unnick"),
    ;
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
