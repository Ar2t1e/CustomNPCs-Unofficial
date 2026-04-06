package noppes.npcs.roles;

import java.lang.ref.WeakReference;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.entity.data.role.IJobBard;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.entity.EntityNPCInterface;

public class JobBard extends JobInterface implements IJobBard {

   public boolean isStreamer = true;
   public boolean isLooping = false;
   public boolean hasOffRange = true;

   // New from Unofficial (BetaZavr)
   public ResourceLocation song = null;
   protected transient volatile WeakReference<Boolean> cachedInRange = new WeakReference<>(false);
   protected transient volatile long checkTime = 0L;
   public boolean isRange = true;
   public int[] range = new int[] { 2, 64 }; // min, max
   public int[] minPos = new int[] { 2, 2, 2 }; // x, y, z
   public int[] maxPos = new int[] { 64, 64, 64 }; // x, y, z

   public JobBard(EntityNPCInterface npc) {
      super(npc);
      type = JobType.BARD;
   }

   @Override
   public void load(CompoundTag compound) {
      super.load(compound);
      type = JobType.BARD;
      isStreamer = compound.getBoolean("BardStreamer");
      isLooping = compound.getBoolean("BardLoops");
      hasOffRange = compound.getBoolean("BardHasOff");

      // New from Unofficial (BetaZavr)
      song = !compound.contains("BardSong", 8) ? null : new ResourceLocation(NoppesUtilServer.validLocation(compound.getString("BardSong")));
      if (compound.contains("BardRangeData", Tag.TAG_INT_ARRAY) && compound.contains("BardIsRange", Tag.TAG_BYTE)) {
         isRange = compound.getBoolean("BardIsRange");
         int[] data = compound.getIntArray("BardRangeData");
         if (data.length > 1) { range = new int[] { data[0], data[1] }; }
         if (data.length > 4) { minPos = new int[] { data[2], data[3], data[4] }; }
         else { maxPos = new int[] { range[0], range[0], range[0] }; }
         if (data.length > 7) { maxPos = new int[] { data[5], data[6], data[7] }; }
         else {maxPos = new int[] { range[1], range[1], range[1] }; }
      }
      else if (compound.contains("BardMinRange", 3) && compound.contains("BardMaxRange", 3)) {
         isRange = true;
         range = new int[] { compound.getInt("BardMinRange"), compound.getInt("BardMaxRange") };
         minPos = new int[] { range[0], range[0], range[0] };
         maxPos = new int[] { range[1], range[1], range[1] };
      }
   }

   @Override
   public CompoundTag save(CompoundTag compound) {
      super.save(compound);
      compound.putBoolean("BardStreamer", isStreamer);
      compound.putBoolean("BardLoops", isLooping);
      compound.putBoolean("BardHasOff", hasOffRange);

      // New from Unofficial (BetaZavr)
      if (song != null) { compound.putString("BardSong", song.toString()); }
      compound.putBoolean("BardIsRange", isRange);
      compound.putIntArray("BardRangeData", new int[] { range[0], range[1], minPos[0], minPos[1], minPos[2], maxPos[0], maxPos[1], maxPos[2] });
      return compound;
   }

   @Override
   public void killed() { delete(); }

   @Override
   public void delete() {
      if (npc != null && npc.level().isClientSide() && hasOffRange && MusicController.Instance.isPlaying(song)) {
         MusicController.Instance.stopSound(song, isStreamer ? SoundSource.AMBIENT : SoundSource.MUSIC);
      }
   }

   @Override
   public String getSong() { return song == null ? "" : song.toString(); }

   @Override
   public void setSong(String resourceSound) {
      song = resourceSound == null ? null : new ResourceLocation(NoppesUtilServer.validLocation(resourceSound));
      if (npc != null) { npc.updateClient = true; }
   }

   // New from Unofficial (BetaZavr)
   @Override
   public boolean isWorking() {
      if (npc != null && npc.isClientSide() && song != null) {
         MusicController mData = MusicController.Instance;
         return npc.equals(mData.musicBard) || npc.equals(mData.songBard);
      }
      return song != null;
   }

   public void aiStep() {
      if (npc != null && npc.isClientSide() && song != null) {
         MusicController mData = MusicController.Instance;
         if (isStreamer ? mData.unloadSongBard : mData.unloadMusicBard) {
            Entity oldNPC = isStreamer ? mData.songBard : mData.musicBard;
            if (oldNPC == null) {
               if (isStreamer) { mData.unloadSongBard = false; }
               else { mData.unloadMusicBard = false; }
            }
            else if (oldNPC.getUUID().equals(npc.getUUID())) {
               if (isStreamer) {
                  mData.unloadSongBard = false;
                  mData.songBard = npc;
               }
               else {
                  mData.musicBard = npc;
                  mData.unloadMusicBard = false;
               }
            }
         } // music correct
         if (!mData.isBardPlaying(song, isStreamer)) {
            if (!getPlayerInRange()) { return; }
            mData.bardPlaySound(song, isStreamer, npc);
         } // not bard play song
         else if (npc.equals(isStreamer ? mData.songBard : mData.musicBard) && !song.equals(isStreamer ? mData.song : mData.music)) {
            if (mData.song != null && npc.equals(mData.songBard)) { mData.stopSound(mData.song, SoundSource.AMBIENT); }
            if (mData.music != null && npc.equals(mData.musicBard)) { mData.stopSound(mData.music, SoundSource.MUSIC); }
         } // change bard
         else if (!npc.equals(isStreamer ? mData.songBard : mData.musicBard)) {
            Player player = CustomNpcs.proxy.getPlayer();
            if (player != null) {
               Entity oldNPC = isStreamer ? mData.songBard : mData.musicBard;
               if (oldNPC == null || npc.distanceToSqr(player) < oldNPC.distanceToSqr(player)) {
                  if (getPlayerInRange()) {
                     ResourceLocation mSong = isStreamer ? mData.song : mData.music;
                     if (mSong != null && mSong.equals(song)) {
                        if (isStreamer) {
                           mData.songBard = npc;
                           mData.music = null;
                           mData.musicBard = null;
                        } else {
                           mData.musicBard = npc;
                           mData.song = null;
                           mData.songBard = null;
                        }
                        mData.setNewPosSong(mSong, npc.getX(), npc.getY(), npc.getZ());
                     }
                     else {
                        mData.stopSound(mSong, isStreamer ? SoundSource.AMBIENT : SoundSource.MUSIC);
                        mData.bardPlaySound(song, isStreamer, npc);
                     }
                  }
               }
            }
         } // check main NPC
         else if (hasOffRange && npc.equals(isStreamer ? mData.songBard : mData.musicBard)) {
            if (!getPlayerInRange()) { mData.stopSound(song, isStreamer ? SoundSource.AMBIENT : SoundSource.MUSIC); }
         } // check Distance
      }
   }

   private boolean getPlayerInRange() {
      Player player = CustomNpcs.proxy.getPlayer();
      if (player != null && npc != null) {
         long now = System.currentTimeMillis();
         if (now < checkTime) { return Boolean.TRUE.equals(cachedInRange.get()); }
         AABB aabb = npc.getBoundingBox();
         if (isRange) { aabb = aabb.inflate(range[0], range[0], range[0]); }
         else {
            aabb = new AABB(aabb.minX - minPos[0], aabb.minY - minPos[1], aabb.minZ - minPos[2],
                    aabb.maxX + minPos[0], aabb.maxY + minPos[1], aabb.maxZ + minPos[2]);
         }
         List<Player> list = npc.level().getEntitiesOfClass(Player.class, aabb);
         boolean result = list.contains(CustomNpcs.proxy.getPlayer());
         cachedInRange = new WeakReference<>(result);
         checkTime = now + 500L;
         return result;
      }
      return false;
   }


}
