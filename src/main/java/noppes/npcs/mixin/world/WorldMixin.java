package noppes.npcs.mixin.world;

import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import noppes.npcs.controllers.DimensionController;
import noppes.npcs.dimensions.CustomWorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = World.class, priority = 498)
public class WorldMixin {

    @Shadow protected WorldInfo worldInfo;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void npcs$init(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client, CallbackInfo ci) {
        CustomWorldInfo customInfo = (CustomWorldInfo) DimensionController.getInstance().getMCWorldInfo(providerIn.getDimension());
        if (customInfo != null) { worldInfo = customInfo; }
    }

}
