package me.hsgamer.nick.utils.signgui;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.logging.Level;
import java.util.stream.IntStream;
import me.hsgamer.nick.Nick;
import me.hsgamer.nick.utils.Utils;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class SignMenuFactory {

  private static final int ACTION_INDEX = 9;
  private static final int SIGN_LINES = 4;

  private static final String NBT_FORMAT = "{\"text\":\"%s\"}";
  private static final String NBT_BLOCK_ID = "minecraft:sign";

  private final Plugin plugin;

  private final Map<Player, Menu> inputReceivers;

  public SignMenuFactory(Plugin plugin) {
    this.plugin = plugin;
    this.inputReceivers = new HashMap<>();
    this.listen();
  }

  private static WrappedChatComponent[] wrap(String[] lines) {
    List<WrappedChatComponent> wrappedChatComponents = new ArrayList<>();

    for (String line : lines) {
      line = line == null ? "" : line;
      wrappedChatComponents.add(WrappedChatComponent.fromText(Utils.colornize(line)));
    }

    return wrappedChatComponents.toArray(new WrappedChatComponent[0]);
  }

  private static String[] unwrap(WrappedChatComponent[] components) {
    List<String> stringList = new ArrayList<>();

    for (WrappedChatComponent component : components) {
      String text = IChatBaseComponent.ChatSerializer.a(component.getJson()).getText();
      stringList.add(text);
    }

    return stringList.toArray(new String[0]);
  }

  public Menu newMenu(List<String> text) {
    Objects.requireNonNull(text, "text");
    return new Menu(text);
  }

  private void listen() {
    ProtocolLibrary.getProtocolManager()
        .addPacketListener(new PacketAdapter(this.plugin, PacketType.Play.Client.UPDATE_SIGN) {
          @Override
          public void onPacketReceiving(PacketEvent event) {
            PacketContainer packet = event.getPacket();
            Player player = event.getPlayer();

            String[] input;
            if (Nick.IS_LEGACY) {
              input = unwrap(packet.getChatComponentArrays().read(0));
            } else {
              input = packet.getStringArrays().read(0);
            }

            Menu menu = inputReceivers.remove(player);

            if (menu == null) {
              return;
            }
            event.setCancelled(true);

            boolean success = menu.response.test(player, input);

            if (!success && menu.opensOnFail()) {
              Bukkit.getScheduler().runTaskLater(plugin, () -> menu.open(player), 2L);
            }

            PacketContainer remove = ProtocolLibrary.getProtocolManager()
                .createPacket(PacketType.Play.Server.BLOCK_CHANGE);
            remove.getBlockPositionModifier().write(0, menu.position);
            remove.getBlockData().write(0, WrappedBlockData.createData(Material.AIR));
            try {
              ProtocolLibrary.getProtocolManager().sendServerPacket(player, remove);
            } catch (InvocationTargetException e) {
              Nick.getInstance().getLogger().log(Level.WARNING, "Error when sending the packet", e);
            }
          }
        });
  }

  public class Menu {

    private final List<String> text;

    private BiPredicate<Player, String[]> response;
    private boolean reopenIfFail;

    private BlockPosition position;

    Menu(List<String> text) {
      this.text = text;
    }

    protected BlockPosition getPosition() {
      return this.position;
    }

    public boolean opensOnFail() {
      return this.reopenIfFail;
    }

    public Menu reopenIfFail() {
      this.reopenIfFail = true;
      return this;
    }

    public Menu response(BiPredicate<Player, String[]> response) {
      this.response = response;
      return this;
    }

    @SuppressWarnings("deprecated")
    public void open(Player player) {
      Objects.requireNonNull(player, "player");
      Location location = player.getLocation();
      this.position = new BlockPosition(location.getBlockX(), 0, location.getBlockZ());

      PacketContainer block = ProtocolLibrary.getProtocolManager()
          .createPacket(PacketType.Play.Server.BLOCK_CHANGE);
      block.getBlockPositionModifier().write(0, position);
      block.getBlockData().write(0, WrappedBlockData.createData(SignMaterial.getSign()));

      PacketContainer openSign = ProtocolLibrary.getProtocolManager()
          .createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);
      openSign.getBlockPositionModifier().write(0, this.position);

      PacketContainer signData;
      if (Nick.IS_LEGACY) {
        signData = ProtocolLibrary.getProtocolManager()
            .createPacket(PacketType.Play.Server.UPDATE_SIGN);
        signData.getBlockPositionModifier().write(0, position);
        signData.getChatComponentArrays().write(0, wrap(text.toArray(new String[0])));
      } else {
        signData = ProtocolLibrary.getProtocolManager()
            .createPacket(PacketType.Play.Server.TILE_ENTITY_DATA);

        NbtCompound signNBT = (NbtCompound) signData.getNbtModifier().read(0);

        IntStream.range(0, SIGN_LINES).forEach(line -> signNBT.put("Text" + (line + 1),
            text.size() > line ? String.format(NBT_FORMAT, Utils.colornize(text.get(line))) : " "));

        signNBT.put("x", this.position.getX());
        signNBT.put("y", this.position.getY());
        signNBT.put("z", this.position.getZ());
        signNBT.put("id", NBT_BLOCK_ID);

        signData.getBlockPositionModifier().write(0, this.position);
        signData.getIntegers().write(0, ACTION_INDEX);
        signData.getNbtModifier().write(0, signNBT);
      }

      try {
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, block);
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, signData);
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, openSign);
      } catch (InvocationTargetException exception) {
        Nick.getInstance().getLogger().log(Level.WARNING, "Error when opening the sign", exception);
      }
      inputReceivers.put(player, this);
    }
  }
}