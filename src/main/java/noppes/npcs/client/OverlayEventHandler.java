package noppes.npcs.client;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.controllers.OverlayController;
import noppes.npcs.client.overlay.Overlay;

public class OverlayEventHandler {

    @SubscribeEvent
    public void onRenderOverlay(Post event) {
        CustomNpcs.debugData.start("Mod");
        if (event.getType() == RenderGameOverlayEvent.ElementType.VIGNETTE) {
            List<Overlay> overlays = OverlayController.getInstance().getOverlays();
            if (!overlays.isEmpty()) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                        GlStateManager.SourceFactor.ONE,
                        GlStateManager.DestFactor.ZERO);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                for (Overlay overlay : overlays) { overlay.render(); }
                GlStateManager.popMatrix();
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableLighting();
                GlStateManager.disableRescaleNormal();
                GlStateManager.enableAlpha();
                GlStateManager.enableDepth();
                GlStateManager.depthMask(true);
                GlStateManager.enableTexture2D();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                        GlStateManager.SourceFactor.ONE,
                        GlStateManager.DestFactor.ZERO);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
        CustomNpcs.debugData.end("Mod");
    }

}
