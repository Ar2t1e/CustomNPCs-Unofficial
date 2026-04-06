package noppes.npcs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.controllers.OverlayController;
import noppes.npcs.client.overlay.Overlay;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

public class OverlayEventHandler {

    @SubscribeEvent
    public void onRenderOverlay(Post event) {
        CustomNpcs.debugData.start("Mod");
        if (event.getType() == RenderGameOverlayEvent.ElementType.VIGNETTE) {
            GlStateManager.enableBlend();
            GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft mc = Minecraft.getMinecraft();
            int mouseX = Mouse.getX();
            int mouseY = Mouse.getY();
            int mouseWheel = mc.currentScreen == null ? Mouse.getDWheel() : 0;
            for (Overlay overlay : OverlayController.getInstance().getOverlays()) { overlay.render(mc, mouseX, mouseY, mouseWheel, event.getPartialTicks()); }
            GlStateManager.disableBlend();
        }
        CustomNpcs.debugData.end("Mod");
    }

}
