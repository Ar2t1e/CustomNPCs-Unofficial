package noppes.npcs.mixin.client.gui.screens.recipebook;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import noppes.npcs.ScriptPlayerEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RecipeBookTabButton.class, priority = 498)
public class RecipeBookTabButtonMixin {



    @Inject(
            at = {@At("TAIL")},
            method = {"renderWidget"}
    )
    private void npcs$renderWidget(GuiGraphics graphics, int xMouse, int yMouse, float partialTicks, CallbackInfo ci) {
        ScriptPlayerEventHandler.test(graphics, xMouse, yMouse, partialTicks, ci);
    }

}
