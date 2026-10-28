package noppes.npcs.mixin.client.gui.recipebook;

import net.minecraft.client.gui.recipebook.GuiButtonRecipeTab;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import noppes.npcs.CustomItems;
import noppes.npcs.client.ClientRegisterEvents;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.shared.client.gui.GuiBasic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GuiButtonRecipeTab.class, priority = 498)
public class GuiButtonRecipeTabMixin {

    @Final @Shadow private CreativeTabs category;

    @Inject(method = "renderIcon", at = @At("HEAD"), cancellable = true)
    private void npcs$renderIcon(RenderItem renderItem, CallbackInfo ci) {
        GuiButtonRecipeTab parent = (GuiButtonRecipeTab) (Object) this;
        if (category == ClientRegisterEvents.CRAFTING_CUSTOM_GLOBAL_CATEGORY) {
            ci.cancel();
            GuiBasic.renderStackInGUI(category.getIconItemStack(), parent.x + 3, parent.y + 5, 0.0f, 1.0f, "");
            GuiBasic.renderStackInGUI(new ItemStack(CustomItems.cloner), parent.x + 14, parent.y + 5, 0.0f, 1.0f, "");
        }
        if (category == ClientRegisterEvents.CRAFTING_CUSTOM_ANVIL_CATEGORY) {
            ci.cancel();
            GuiBasic.renderStackInGUI(category.getIconItemStack(), parent.x + 9, parent.y + 5, 0.0f, 1.0f, "");
        }
    }

    @Inject(method = "updateVisibility", at = @At("HEAD"), cancellable = true)
    private void npcs$updateVisibility(CallbackInfoReturnable<Boolean> cir) {
        RecipeController rData = RecipeController.getInstance();
        if ((category == ClientRegisterEvents.CRAFTING_CUSTOM_GLOBAL_CATEGORY && !rData.getAllGlobalRecipes().isEmpty())||
                (category == ClientRegisterEvents.CRAFTING_CUSTOM_ANVIL_CATEGORY && !rData.getAllAnvilRecipes().isEmpty())) {
            GuiButtonRecipeTab parent = (GuiButtonRecipeTab) (Object) this;
            parent.visible = true;
            cir.setReturnValue(true);
        }
    }
}
