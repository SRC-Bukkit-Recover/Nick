package me.hsgamer.nick.enums;

import java.util.Arrays;

public enum ConfigEnum {
    // MESSAGES
    PREFIX("messages.prefix", "&f[&e&lNick&f] "),
    PLAYER_ONLY("messages.player-only", "&cYou must be a player to do this"),
    SIGN_LINES("messages.sign-lines", Arrays.asList("^^^^^^^^^^^^^", "&aType the nickname that", "&ayou want to change")),
    NO_PERMISSION("messages.no-permission", "&cYou don't have permission to do this"),
    SUCCESSFUL("messages.successful", "&aSuccessfully changed"),
    NOT_BLANK("messages.not-blank", "&cYou should not leave your nickname blank"),
    // SETTINGS
    PERMISSION("settings.permission", "nick.use"),
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
