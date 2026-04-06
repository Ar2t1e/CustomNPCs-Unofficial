package noppes.npcs.mixin.minecraftforge.common.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import noppes.npcs.CustomNpcs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CapabilityDispatcher.class, priority = 498)
public class CapabilityDispatcherMixin {

   @Inject(
      method = {"serializeNBT()Lnet/minecraft/nbt/CompoundTag;"},
      at = {@At("RETURN")},
      cancellable = true,
      remap = false
   )
   public void serializeNBT(CallbackInfoReturnable<CompoundTag> cir) {
      CompoundTag tag = cir.getReturnValue();
      if (tag.contains(CustomNpcs.MODID + ":itemscripteddata") && tag.getCompound(CustomNpcs.MODID + ":itemscripteddata").isEmpty()) {
         tag.remove(CustomNpcs.MODID + ":itemscripteddata");
      }
      cir.setReturnValue(tag);
   }

}
