package noppes.npcs.mixin.client.gui.screens.recipebook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import noppes.npcs.controllers.data.RecipeCarpentry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = RecipeButton.class, priority = 498)
public class RecipeButtonMixin {

    @Shadow private static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
    @Shadow private RecipeBookMenu<?> menu;
    @Shadow private RecipeBook book;
    @Shadow private RecipeCollection collection;
    @Shadow private float time;
    @Shadow private float animationTime;
    @Shadow private int currentIndex;

    /** npc recipe */
    @Inject(at = {@At("HEAD")}, method = {"renderWidget"}, cancellable = true)
    private void npcs$renderWidget(GuiGraphics graphics, int x, int y, float partialTicks, CallbackInfo ci) {
        IRecipeButtonMixin mixin = (IRecipeButtonMixin) this;
        Recipe<?> recipe = mixin.invokeGetRecipe();
        if (recipe instanceof RecipeCarpentry npcRecipe) {
            ci.cancel();
            RecipeButton parent = (RecipeButton) (Object) this;
            if (!Screen.hasControlDown()) {
                time += partialTicks;
            }

            int u = 29;
            if (!collection.hasCraftable() || !npcRecipe.availability.isAvailable(Minecraft.getInstance().player)) { u += 25; }
            int v = 206;
            if (collection.getRecipes(book.isFiltering(menu)).size() > 1) { v += 25; }

            boolean isAnimated = animationTime > 0.0F;
            if (isAnimated) {
                float f = 1.0F + 0.1F * (float)Math.sin(animationTime / 15.0F * (float)Math.PI);
                graphics.pose().pushPose();
                graphics.pose().translate((float)(parent.getX() + 8), (float)(parent.getY() + 12), 0.0F);
                graphics.pose().scale(f, f, 1.0F);
                graphics.pose().translate((float)(-(parent.getX() + 8)), (float)(-(parent.getY() + 12)), 0.0F);
                animationTime -= partialTicks;
            }

            graphics.blit(RECIPE_BOOK_LOCATION, parent.getX(), parent.getY(), u, v, parent.getWidth(), parent.getHeight());

            List<Recipe<?>> list = mixin.invokeGetOrderedRecipes();
            currentIndex = Mth.floor(time / 30.0F) % list.size();
            ItemStack itemstack = list.get(currentIndex).getResultItem(collection.registryAccess());
            int k = 4;
            if (collection.hasSingleResultItem() && list.size() > 1) {
                graphics.renderItem(itemstack, parent.getX() + k + 1, parent.getY() + k + 1, 0, 10);
                --k;
            }

            graphics.renderFakeItem(itemstack, parent.getX() + k, parent.getY() + k);
            if (isAnimated) { graphics.pose().popPose(); }
        }

    }

    /** add availability info from npc recipe */
    @Inject(at = {@At("RETURN")}, method = {"getTooltipText"}, cancellable = true)
    public void npcs$getTooltipText(CallbackInfoReturnable<List<Component>> cir) {
        IRecipeButtonMixin mixin = (IRecipeButtonMixin) this;
        Recipe<?> recipe = mixin.invokeGetRecipe();
        if (recipe instanceof RecipeCarpentry npcRecipe && npcRecipe.availability.hasOptions()) {
            List<Component> list = cir.getReturnValue();
            list.addAll(npcRecipe.availability.getAvailability(Minecraft.getInstance().player, Component.translatable("recipe.hover.availability.type")));
            cir.setReturnValue(list);
        }
    }

}
