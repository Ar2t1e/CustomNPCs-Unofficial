package noppes.npcs.mixin.client.gui;

import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiCrafting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;

@Mixin(value = GuiCrafting.class, priority = 498)
public class GuiCraftingMixin {

    @Shadow private GuiButtonImage recipeButton;

    /**
     * @author BetaZavr
     * @reason Remove extra duplicate button
     */
    @Inject(method = "initGui", at = @At("TAIL"))
    public void npcs$initGui(CallbackInfo ci) {
        GuiCrafting parent = (GuiCrafting) (Object) this;
        if (parent.buttonList.size() > 1) { parent.buttonList = Collections.singletonList(recipeButton); }
    }

}
