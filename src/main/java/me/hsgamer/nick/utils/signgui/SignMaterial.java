package me.hsgamer.nick.utils.signgui;

import me.hsgamer.nick.Nick;
import org.bukkit.Material;

public class SignMaterial {

  private static Material sign;

  public static Material getSign() {
    if (sign == null) {
      try {
        sign = Material.OAK_WALL_SIGN;
      } catch (NoSuchFieldError e) {
        if (Nick.IS_LEGACY) {
          sign = Material.matchMaterial("SIGN_POST");
        } else {
          sign = Material.matchMaterial("WALL_SIGN");
        }
      }
    }
    return sign;
  }
}
