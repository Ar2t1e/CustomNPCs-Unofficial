package noppes.npcs.mixin.client.renderer;

import net.minecraft.client.renderer.CubeMap;
import net.minecraft.resources.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.util.LogWriter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = CubeMap.class, priority = 498)
public class CubeMapMixin {

    @Final @Shadow private ResourceLocation[] images;

    @Unique public int cnpc$variant = new Random().nextInt(CustomNpcs.PanoramaNumbers);

    @Inject(
            at = {@At("TAIL")},
            method = {"<init>"}
    )
    public void cnpc$init(ResourceLocation location, CallbackInfo ci) {
        if (CustomNpcs.ReplaceCustomBackground) {
            LogWriter.info("CustomNpcs: background variant #" + cnpc$variant);
            for(int i = 0; i < 6; ++i) {
                images[i] = new ResourceLocation(CustomNpcs.MODID, "textures/gui/title/background/" + cnpc$variant + "/panorama_" + i + ".png");
            }
        }
    }

}
