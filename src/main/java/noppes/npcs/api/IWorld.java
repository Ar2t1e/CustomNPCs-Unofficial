package noppes.npcs.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.item.IItemStack;

@SuppressWarnings("all")
public interface IWorld {

   @Deprecated
   IEntity<?>[] getNearbyEntities(@ParamName("x") int x, @ParamName("y") int y, @ParamName("z") int z,
                                  @ParamName("range") double range, @ParamName("type") int type);

   IEntity<?>[] getNearbyEntities(@ParamName("pos") IPos pos, @ParamName("range") double range, @ParamName("type") int type);

   @Deprecated
   IEntity<?> getClosestEntity(@ParamName("x") int x, @ParamName("y") int y, @ParamName("z") int z,
                               @ParamName("range") double range, @ParamName("type") int type);

   IEntity<?> getClosestEntity(@ParamName("pos") IPos pos, @ParamName("range") double range, @ParamName("type") int type);

   IEntity<?>[] getAllEntities(@ParamName("type") int type);

   long getTime();

   void setTime(@ParamName("ticks") long ticks);

   long getTotalTime();

   @Deprecated
   IBlock getBlock(@ParamName("x") int x, @ParamName("y") int y, @ParamName("z") int z);

   IBlock getBlock(@ParamName("pos") IPos pos);

   @Deprecated
   void setBlock(@ParamName("x") int x, @ParamName("y") int y, @ParamName("z") int z,
                 @ParamName("name") String name);

   IBlock setBlock(@ParamName("pos") IPos pos, @ParamName("name") String name);

   @Deprecated
   void removeBlock(@ParamName("x") int x, @ParamName("y") int y, @ParamName("z") int z);

   void removeBlock(@ParamName("pos") IPos pos);

   float getLightValue(@ParamName("x") int x, @ParamName("y") int y, @ParamName("z") int z);

   IPlayer<?> getPlayer(@ParamName("name") String name);

   boolean isDay();

   boolean isRaining();

   IDimension getDimension();

   void setRaining(@ParamName("bo") boolean bo);

   void thunderStrike(@ParamName("x") double x, @ParamName("y") double y, @ParamName("z") double z);

   void playSoundAt(@ParamName("pos") IPos pos, @ParamName("sound") String sound, @ParamName("volume") float volume, @ParamName("pitch") float pitch);

   void spawnParticle(@ParamName("particle") String particle, @ParamName("x") double x, @ParamName("y") double y, @ParamName("z") double z,
                      @ParamName("dx") double dx, @ParamName("dy") double dy, @ParamName("dz") double dz,
                      @ParamName("speed") double speed, @ParamName("count") int count);

   void broadcast(@ParamName("message") String message);

   IScoreboard getScoreboard();

   IData getTempdata();

   IData getStoreddata();

   IItemStack createItem(@ParamName("name") String name, @ParamName("size") int size);

   IItemStack createItemFromNbt(@ParamName("nbt") INbt nbt);

   void explode(@ParamName("x") double x, @ParamName("y") double y, @ParamName("z") double z,
                @ParamName("range") float range, @ParamName("fire") boolean fire, @ParamName("grief") boolean grief);

   IPlayer<?>[] getAllPlayers();

   String getBiomeName(@ParamName("x") int x, @ParamName("z") int z);

   void spawnEntity(@ParamName("entity") IEntity<?> entity);

   @Deprecated
   IEntity<?> spawnClone(@ParamName("x") double x, @ParamName("y") double y, @ParamName("z") double z,
                         @ParamName("tab") int tab, @ParamName("name") String name);

   @Deprecated
   IEntity<?> getClone(@ParamName("tab") int tab, @ParamName("name") String name);

   int getRedstonePower(@ParamName("x") int x, @ParamName("y") int y, @ParamName("z") int z);

   Level getMCLevel();

   BlockPos getMCBlockPos(@ParamName("x") int x, @ParamName("y") int y, @ParamName("z") int z);

   IEntity<?> getEntity(@ParamName("uuid") String uuid);

   IEntity<?> createEntityFromNBT(@ParamName("nbt") INbt nbt);

   IEntity<?> createEntity(@ParamName("id") String id);

   IBlock getSpawnPoint();

   void setSpawnPoint(@ParamName("block") IBlock block);

   String getName();

   void trigger(@ParamName("id") int id, @ParamName("arguments") Object... arguments);

}
