package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.controllers.ChunkController;
import noppes.npcs.entity.EntityNPCInterface;

public class JobChunkLoader extends JobInterface {

   protected List<ChunkPos> chunks = new ArrayList<>();
   protected int ticks = 20;
   protected long playerLastSeen = -1L;

   public JobChunkLoader(EntityNPCInterface npc) {
      super(npc);
      type = JobType.CHUNK_LOADER;
   }

   @Override
   public void load(CompoundTag compound) {
      super.load(compound);
      type = JobType.CHUNK_LOADER;
      playerLastSeen = compound.getLong("ChunkPlayerLastSeen");
   }

   @Override
   public CompoundTag save(CompoundTag compound) {
      super.save(compound);
      compound.putLong("ChunkPlayerLastSeen", playerLastSeen);
      return compound;
   }

   @Override
   public boolean aiContinueExecute() { return false; }

   @Override
   public boolean aiShouldExecute() {
      --ticks;
      if (ticks <= 0 && npc != null) {
         ticks = 20;
         List<Player> players = npc.level().getEntitiesOfClass(Player.class, npc.getBoundingBox().inflate(48.0D, 48.0D, 48.0D));
         if (!players.isEmpty()) { playerLastSeen = System.currentTimeMillis(); }

         if (playerLastSeen > -1L) {
            if (System.currentTimeMillis() > playerLastSeen + 600000L) {
               ChunkController.instance.unload((ServerLevel) npc.level(), npc.getUUID(), npc.chunkPosition().x, npc.chunkPosition().z);
               chunks.clear();
               playerLastSeen = -1L;
               return false;
            }
            List<ChunkPos> list = new ArrayList<>();
            int x = Mth.floor(npc.getX() / 16.0D);
            int z = Mth.floor(npc.getZ() / 16.0D);
            // New from Unofficial (BetaZavr) 3x3
            for (int u = -1; u < 2; u++) {
               for (int v = -1; v < 2; v++) {
                  list.add(new ChunkPos(x + u, z + v));
               }
            }
            for (ChunkPos chunk : list) {
               if (!chunks.contains(chunk)) { ChunkController.instance.load((ServerLevel)npc.level(), npc.getUUID(), chunk.x, chunk.z); }
            }
            for (ChunkPos chunk : chunks) { ChunkController.instance.unload((ServerLevel)npc.level(), npc.getUUID(), chunk.x, chunk.z); }
            chunks = list;
         }
      }
      return false;
   }

   @Override
   public void reset() {
      if (npc != null && !npc.isClientSide()) {
         ChunkController.instance.unload((ServerLevel)npc.level(), npc.getUUID(), npc.chunkPosition().x, npc.chunkPosition().z);
         chunks.clear();
         playerLastSeen = 0L;
      }
   }

   // New from Unofficial (BetaZavr)
   @Override
   public boolean isWorking() { return !chunks.isEmpty() || ChunkController.instance.hasToNpc(npc); }

}
