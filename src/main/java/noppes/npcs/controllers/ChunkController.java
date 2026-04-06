package noppes.npcs.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;

public class ChunkController {

   public static final ChunkController instance = new ChunkController();
   private final HashMap<Long, List<UUID>> loaded = new HashMap<>();

   public void clear() { loaded.clear(); }

   public void unload(ServerLevel level, UUID id, int xChunk, int zChunk) {
      long i = ChunkPos.asLong(xChunk, zChunk);
      List<UUID> list = loaded.get(i);
      if (list != null) {
         list.remove(id);
         if (list.isEmpty()) {
            level.setChunkForced(xChunk, zChunk, false);
            loaded.remove(i);
         }
      }
   }

   public void load(ServerLevel world, UUID id, int xChunk, int zChunk) {
      if (size() < CustomNpcs.ChuckLoaders) {
         long i = ChunkPos.asLong(xChunk, zChunk);
         List<UUID> list = loaded.computeIfAbsent(i, k -> new ArrayList<>());
         list.add(id);
         if (list.size() == 1) { world.setChunkForced(xChunk, zChunk, true); }
      }
   }

   public int size() { return loaded.size(); }

   public boolean hasToNpc(EntityNPCInterface npc) {
      if (npc != null) {
         UUID uuid = npc.getUUID();
         for (List<UUID> list : new ArrayList<>(loaded.values())) {
            if (list.contains(uuid)) { return true; }
         }
      }
      return false;
   }

}
