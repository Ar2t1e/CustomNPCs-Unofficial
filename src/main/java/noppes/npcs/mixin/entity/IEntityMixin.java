package noppes.npcs.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Entity.class, priority = 502)
public interface IEntityMixin {

    @Accessor void setWorld(World newWorld);

    @Accessor EntityDataManager getDataManager();

    @Accessor DataParameter<Byte> getFLAGS();

    @Accessor(remap = false) CapabilityDispatcher getCapabilities();

    @Accessor(remap = false) void setCapabilities(CapabilityDispatcher newCapabilityDispatcher);

    @Accessor BlockPos getLastPortalPos();

    @Accessor void setLastPortalPos(BlockPos newBlockPos);

    @Accessor Vec3d getLastPortalVec();

    @Accessor void setLastPortalVec(Vec3d newVec3d);

    @Accessor EnumFacing getTeleportDirection();

    @Accessor void setTeleportDirection(EnumFacing newEnumFacing);

}
