package me.hsgamer.nick.utils.signgui.legacy;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.*;

public class SignLocator implements Listener {
    private static final SignLocator instance = new SignLocator();

    private Map<Chunk, Iterator<Location>> iterators = new HashMap<>();

    public static SignLocator get() {
        return instance;
    }

    @EventHandler(ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        iterators.remove(event.getChunk());
    }

    public Location next(Chunk chunk) {
        Iterator<Location> iterator = iterators.get(chunk);

        if (iterator == null || !iterator.hasNext()) {
            iterator = create(chunk);
            iterators.put(chunk, iterator);
        }

        return iterator.next();
    }

    private Iterator<Location> create(Chunk chunk) {
        int minX = chunk.getX() << 4;
        int minZ = chunk.getZ() << 4;
        int maxX = minX + 16;
        int maxZ = minZ + 16;

        List<Location> locationList = new ArrayList<>();

        for (int y = 0; y <= chunk.getWorld().getMaxHeight(); y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    locationList.add(new Location(chunk.getWorld(), x, y, z));
                }
            }
        }

        return locationList.iterator();
    }
}
