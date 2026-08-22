package noppes.npcs.mixin.client.gui.recipebook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.GhostRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import noppes.npcs.controllers.data.RecipeCarpentry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;
import java.util.List;

@Mixin(value = GhostRecipe.class, priority = 498)
public class GhostRecipeMixin {

    @Shadow private IRecipe recipe;
    @Final @Shadow private List<GhostRecipe.GhostIngredient> ingredients;

    @Inject(at = {@At("TAIL")}, method = {"render"})
    public void render(@Nonnull  Minecraft mc, int leftPos, int topPos, boolean hasRedMark, float partialTicks, CallbackInfo ci) {
        if (recipe instanceof RecipeCarpentry) {
            for(int i = 1; i < ingredients.size(); ++i) {
                GhostRecipe.GhostIngredient ingredient = ingredients.get(i);
                ItemStack itemstack = ingredient.getItem();
                if (!itemstack.isEmpty()) {
                    mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemstack,
                            ingredient.getX() + leftPos, ingredient.getY() + topPos,
                            String.valueOf(itemstack.getCount()));
                }
            }
        }
    }

}
