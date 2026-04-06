package noppes.npcs.mixin.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Entity.class, priority = 499)
public interface IEntityMixin {

   @Accessor(value = "removalReason")
   RemovalReason removal();

   @Accessor("removalReason")
   void removal(RemovalReason newRemovalReason);

   @Accessor
   void setLevel(Level newLevel);

   @Accessor
   SynchedEntityData getEntityData();

   @Accessor
   EntityDataAccessor<Byte> getDATA_SHARED_FLAGS_ID();

   //@Accessor(remap = false)
   //CapabilityDispatcher getCapabilities(); // CapabilityProvider

   //@Accessor(remap = false)
   //void setCapabilities(CapabilityDispatcher newCapabilityDispatcher);  // CapabilityProvider

   @Accessor
   BlockPos getPortalEntrancePos();

   @Accessor
   void setPortalEntrancePos(BlockPos newBlockPos);

   @Accessor("removalReason")
   Entity.RemovalReason getRemoval();

   @Accessor("removalReason")
   void setRemoval(Entity.RemovalReason newRemoval);

   @Accessor
   EntityDimensions getDimensions();

   @Accessor
   void setDimensions(EntityDimensions newDimensions);

}
