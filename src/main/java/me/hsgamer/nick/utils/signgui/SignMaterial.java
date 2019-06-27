package me.hsgamer.nick.utils.signgui;

import me.hsgamer.nick.Nick;
import org.bukkit.Material;

public class SignMaterial {
    public static Material SIGN;

    static {
        try {
            SIGN = Material.OAK_WALL_SIGN;
        } catch (NoSuchFieldError e) {
            if (Nick.IS_LEGACY) {
                SIGN = Material.matchMaterial("SIGN_POST");
            } else {
                SIGN = Material.matchMaterial("WALL_SIGN");
            }
        }
    }
}
