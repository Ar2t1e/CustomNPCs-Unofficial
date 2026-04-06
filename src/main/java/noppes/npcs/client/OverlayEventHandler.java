package noppes.npcs.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent.Post;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.controllers.OverlayController;
import noppes.npcs.client.overlay.Overlay;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
   value = {Dist.CLIENT},
   modid = CustomNpcs.MODID
)
public class OverlayEventHandler {

   @SubscribeEvent
   public static void onRenderOverlay(Post event) {
      CustomNpcs.debugData.start("Mod");
      if (event.getOverlay().id() == VanillaGuiOverlay.FROSTBITE.id()) {
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(770, 771, 1, 0);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         for (Overlay overlay : OverlayController.getInstance().getOverlays()) { overlay.render(event.getGuiGraphics()); }
         RenderSystem.disableBlend();
      }
      CustomNpcs.debugData.end("Mod");
   }

}
