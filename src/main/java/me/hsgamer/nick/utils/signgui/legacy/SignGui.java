package me.hsgamer.nick.utils.signgui.legacy;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import me.hsgamer.nick.Nick;
import me.hsgamer.nick.utils.signgui.SignMaterial;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SignGui {

  private BiConsumer<Player, String[]> listener;
  private String[] lines;
  private BlockPosition position;
  private boolean color;

  public void show(Player player) {
    ProtocolManager protocol = ProtocolLibrary.getProtocolManager();

    PacketContainer open = protocol.createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);
    PacketContainer remove = protocol.createPacket(PacketType.Play.Server.BLOCK_CHANGE);

    Location location = SignLocator.get().next(player.getLocation().getChunk());
    BlockPosition position = this.position = new BlockPosition(location.getBlockX(), 0,
        location.getBlockZ());

    open.getBlockPositionModifier().write(0, position);
    remove.getBlockPositionModifier().write(0, position);
    remove.getBlockData().write(0, WrappedBlockData.createData(Material.AIR));

    try {
      PacketContainer block = protocol.createPacket(PacketType.Play.Server.BLOCK_CHANGE);

      if (lines != null) {
        // TODO: Replaced in 1.9.4 with TILE_ENTITY_DATA packet.
        PacketContainer update = protocol.createPacket(PacketType.Play.Server.UPDATE_SIGN);

        block.getBlockPositionModifier().write(0, position);
        block.getBlockData().write(0, WrappedBlockData.createData(SignMaterial.SIGN));
        update.getBlockPositionModifier().write(0, position);
        update.getChatComponentArrays().write(0, wrap());

        protocol.sendServerPacket(player, block);
        protocol.sendServerPacket(player, update);
      }

      protocol.sendServerPacket(player, open);
      protocol.sendServerPacket(player, block);
      protocol.sendServerPacket(player, remove);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
      return;
    }

    if (listener != null) {
      protocol.addPacketListener(
          new PacketAdapter(Nick.getInstance(), PacketType.Play.Client.UPDATE_SIGN) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
              protocol.removePacketListener(this);

              PacketContainer packet = event.getPacket();
              BlockPosition position = packet.getBlockPositionModifier().read(0);
              WrappedChatComponent[] components = packet.getChatComponentArrays().read(0);

              if (SignGui.this.position == null) {
                throw new IllegalStateException("Sign update called but position not yet set.");
              }

              if (SignGui.this.position.equals(position)) {
                Bukkit.getScheduler()
                    .runTask(plugin, () -> listener.accept(event.getPlayer(), unwrap(components)));
              }
            }
          });
    }
  }

  private WrappedChatComponent[] wrap() {
    List<WrappedChatComponent> wrappedChatComponents = new ArrayList<>();

    for (String line : lines) {
      line = line == null ? "" : line;
      wrappedChatComponents.add(WrappedChatComponent.fromText(line));
    }

    return wrappedChatComponents.toArray(new WrappedChatComponent[0]);
  }

  private String[] unwrap(WrappedChatComponent[] components) {
    List<String> stringList = new ArrayList<>();

    for (WrappedChatComponent component : components) {
      String text = IChatBaseComponent.ChatSerializer.a(component.getJson()).getText();
      text = color ? ChatColor.translateAlternateColorCodes('&', String.valueOf(text)) : text;
      stringList.add(text);
    }

    return stringList.toArray(new String[0]);
  }

  public SignGui line(Line line, String text) {
    if (lines == null) {
      lines = new String[4];
    }

    lines[line.ordinal()] = text;
    return this;
  }

  public SignGui listener(BiConsumer<Player, String[]> listener) {
    this.listener = listener;
    return this;
  }

  public SignGui color() {
    this.color = true;
    return this;
  }
}
