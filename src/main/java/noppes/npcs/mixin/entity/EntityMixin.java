package noppes.npcs.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.mixin.entity.IEntityIMixin;
import noppes.npcs.api.wrapper.data.Data;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class, priority = 498)
public class EntityMixin implements IEntityIMixin {

    @Shadow public int timeUntilPortal;

    @Unique protected final Data npcs$storeddata = new Data();

    @Override
    public void npcs$copyDataFromOld(Entity entity) {
        NBTTagCompound nbttagcompound = entity.writeToNBT(new NBTTagCompound());
        nbttagcompound.removeTag("Dimension");
        ((Entity) (Object) this).readFromNBT(nbttagcompound);
        timeUntilPortal = entity.timeUntilPortal;
        IEntityMixin parent = (IEntityMixin) this;
        parent.setLastPortalPos(((IEntityMixin) entity).getLastPortalPos());
        parent.setLastPortalVec(((IEntityMixin) entity).getLastPortalVec());
        parent.setTeleportDirection(((IEntityMixin) entity).getTeleportDirection());
    }

    @Inject(method = "writeToNBT", at = @At("RETURN"), cancellable = true)
    public void npcs$writeToNBT(CallbackInfoReturnable<NBTTagCompound> cir) {
        NBTTagCompound compound = cir.getReturnValue();
        compound.setTag("CustomStoredData", npcs$storeddata.getNbt().getMCNBT());
        cir.setReturnValue(compound);
    }

    @Inject(method = "readFromNBT", at = @At("RETURN"))
    public void npcs$readFromNBT(NBTTagCompound compound, CallbackInfo ci) {
        npcs$storeddata.setNbt(compound.getCompoundTag("CustomStoredData"));
    }

    @Inject(method = "applyEntityCollision", at = @At("RETURN"), cancellable = true)
    public void npcs$applyEntityCollision(Entity entityIn, CallbackInfo ci) {
        if (entityIn instanceof EntityNPCInterface && ((EntityNPCInterface) entityIn).display.getHitboxState() == 2) {
            Entity parent = (Entity) (Object) this;
            if (!(parent instanceof EntityNPCInterface) || ((EntityNPCInterface) parent).display.getHitboxState() != 2) { ci.cancel(); }
        }
    }
    @Inject(method = "isRidingSameEntity", at = @At("HEAD"), cancellable = true)
    public void npcs$isRidingSameEntity(Entity entityIn, CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        if (self instanceof EntityNPCInterface && ((EntityNPCInterface) self).hitboxRiding.containsKey(entityIn)) {
            EntityNPCInterface npc = (EntityNPCInterface) self;
            if (!npc.getNavigator().noPath() && npc.hitboxRiding.containsKey(entityIn)) {
                cir.setReturnValue(true);
            }
        }
        if (entityIn instanceof EntityNPCInterface && ((EntityNPCInterface) entityIn).hitboxRiding.containsKey(self)) {
            EntityNPCInterface npc = (EntityNPCInterface) entityIn;
            if (!npc.getNavigator().noPath() && npc.hitboxRiding.containsKey(self)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Override
    public Data npcs$getStoredData() { return npcs$storeddata; }

}
