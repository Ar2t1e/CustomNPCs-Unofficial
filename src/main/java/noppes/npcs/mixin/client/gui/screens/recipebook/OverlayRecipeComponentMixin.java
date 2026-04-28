package noppes.npcs.mixin.client.gui.screens.recipebook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import noppes.npcs.CustomNpcs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(value = OverlayRecipeComponent.class, priority = 498)
public class OverlayRecipeComponentMixin {

    @Final @Shadow private static int MAX_ROW;
    @Final @Shadow private static int MAX_ROW_LARGE;
    @Final @Shadow private static float ITEM_RENDER_SCALE;
    @Final @Shadow public static int BUTTON_SIZE;
    @Final @Shadow private List<Object> recipeButtons;
    @Shadow private boolean isVisible;
    @Shadow private int x;
    @Shadow private int y;
    @Shadow private Minecraft minecraft;
    @Shadow private RecipeCollection collection;
    @Shadow @Nullable private Recipe<?> lastRecipeClicked;
    @Shadow float time;
    @Shadow boolean isFurnaceMenu;

    @Unique
    private static final ResourceLocation NPCS$RECIPE_BOOK_LOCATION = new ResourceLocation(CustomNpcs.MODID, "textures/gui/recipe_book.png");

    /** npc recipe */
    @Inject(at = {@At("HEAD")}, method = {"render"}, cancellable = true)
    private void npcs$renderWidget(GuiGraphics graphics, int x, int y, float partialTicks, CallbackInfo ci) {

    }

}
