package noppes.npcs.mixin.client.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import noppes.npcs.client.gui.custom.GuiCustom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Screen.class, priority = 498)
public class ScreenMixin {

   @Inject(
      at = {@At("TAIL")},
      method = {"init*"}
   )
   private void renderToBuffer(Minecraft mc, int width, int height, CallbackInfo callbackInfo) {
      if ((Object) this instanceof GuiCustom gui) {
         if (gui.subgui != null) {
            gui.subgui.init(mc, width, height);
         }
      }
   }

}
