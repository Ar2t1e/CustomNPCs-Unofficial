package noppes.npcs.mixin.client.gui.components;

import net.minecraft.SharedConstants;
import net.minecraft.client.gui.components.EditBox;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EditBox.class, priority = 498)
public class MixinEditBox {

   @Redirect(
      method = {"insertText"},
      at = @At(
              value = "INVOKE",
              target = "Lnet/minecraft/SharedConstants;filterText(Ljava/lang/String;)Ljava/lang/String;"
      )
   )
   public String filterTextProxy(String insertText) {
      return (Object) this instanceof GuiTextFieldNop ? insertText : SharedConstants.filterText(insertText);
   }

}
