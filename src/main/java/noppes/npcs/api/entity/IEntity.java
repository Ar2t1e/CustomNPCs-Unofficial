package noppes.npcs.api.entity;

import net.minecraft.world.entity.Entity;
import noppes.npcs.api.*;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.util.IRayTraceResults;

@SuppressWarnings("all")
public interface IEntity<T extends Entity> {

   double getX();

   void setX(@ParamName("x") double x);

   double getY();

   void setY(@ParamName("y") double y);

   double getZ();

   void setZ(@ParamName("z") double z);

   int getBlockX();

   int getBlockY();

   int getBlockZ();

   IPos getPos();

   void setPos(@ParamName("pos") IPos pos);

   void setPosition(@ParamName("x") double x, @ParamName("y") double y, @ParamName("z") double z);

   void setRotation(@ParamName("rotation") float rotation);

   float getRotation();

   float getHeight();

   float getEyeHeight();

   float getWidth();

   void setPitch(@ParamName("pitch") float pitch);

   float getPitch();

   IEntity<?> getMount();

   void setMount(@ParamName("entity") IEntity<?> entity);

   IEntity<?>[] getRiders();

   IEntity<?>[] getAllRiders();

   void addRider(@ParamName("entity") IEntity<?> entity);

   void clearRiders();

   void knockback(@ParamName("power") int power, @ParamName("direction") float direction);

   boolean isSneaking();

   boolean isSprinting();

   IEntityItem<?> dropItem(@ParamName("item") IItemStack item);

   boolean inWater();

   boolean inFire();

   boolean inLava();

   IData getTempdata();

   IData getStoreddata();

   INbt getNbt();

   boolean isAlive();

   long getAge();

   void despawn();

   void spawn();

   void kill();

   boolean isBurning();

   void setBurning(@ParamName("ticks") int ticks);

   void extinguish();

   IWorld getWorld();

   String getTypeName();

   int getType();

   boolean typeOf(@ParamName("type") int type);

   T getMCEntity();

   String getUUID();

   String generateNewUUID();

   void storeAsClone(@ParamName("tab") int tab, @ParamName("name") String name);

   INbt getEntityNbt();

   void setEntityNbt(@ParamName("nbt") INbt nbt);

   IRayTraceResults rayTrace(@ParamName("distance") double distance);

   IRayTrace rayTraceBlock(@ParamName("distance") double distance, @ParamName("stopOnLiquid") boolean stopOnLiquid,
                           @ParamName("ignoreBlockWithoutBoundingBox") boolean ignoreBlockWithoutBoundingBox);

   IEntity<?>[] rayTraceEntities(@ParamName("distance") double distance, @ParamName("stopOnLiquid") boolean stopOnLiquid,
                                 @ParamName("ignoreBlockWithoutBoundingBox") boolean ignoreBlockWithoutBoundingBox);

   String[] getTags();

   void addTag(@ParamName("tag") String tag);

   boolean hasTag(@ParamName("tag") String tag);

   void removeTag(@ParamName("tag") String tag);

   void playAnimation(@ParamName("type") int type);

   void damage(@ParamName("amount") float amount);

   void damage(@ParamName("amount") float amount, @ParamName("source") IEntity<?> source);

   void damage(@ParamName("amount") float amount, @ParamName("damageSource") IEntityDamageSource damageSource);

   double getMotionX();

   double getMotionY();

   double getMotionZ();

   void setMotionX(@ParamName("motion") double motion);

   void setMotionY(@ParamName("motion") double motion);

   void setMotionZ(@ParamName("motion") double motion);

   String getName();

   void setName(@ParamName("name") String name);

   boolean hasCustomName();

   String getEntityName();

}
