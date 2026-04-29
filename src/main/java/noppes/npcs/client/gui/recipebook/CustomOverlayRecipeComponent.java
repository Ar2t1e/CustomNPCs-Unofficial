package noppes.npcs.client.gui.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.RecipeCarpentry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CustomOverlayRecipeComponent extends OverlayRecipeComponent {

    protected static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
    protected static final int MAX_ROW = 4;
    protected static final int MAX_ROW_LARGE = 5;
    protected static final float ITEM_RENDER_SCALE = 0.375F;
    public static final int BUTTON_SIZE = 25;

    protected final List<OverlayRecipeButton> recipeButtons = Lists.newArrayList();
    protected boolean isVisible;
    protected int x;
    protected int y;
    protected RecipeCollection collection;
    @Nullable protected Recipe<?> lastRecipeClicked;
    protected float time;
    protected boolean isFurnaceMenu;

    // New from Unofficial (BetaZavr)
    protected static final ResourceLocation CUSTOM_RECIPE_BOOK_LOCATION = new ResourceLocation(CustomNpcs.MODID, "textures/gui/recipe_book.png");
    protected int recipeSize = 25;

    @Override
    public void init(@Nonnull Minecraft minecraftIn, @Nonnull RecipeCollection collectionIn,
                     int xIn, int yIn, int viewportWidth, int viewportHeight, float uiScalingFactor) {
        collection = collectionIn;
        if (minecraftIn.player != null && minecraftIn.player.containerMenu instanceof AbstractFurnaceMenu) {
            isFurnaceMenu = true;
        }
        boolean isFiltering = minecraftIn.player != null && minecraftIn.player.getRecipeBook().isFiltering((RecipeBookMenu<?>) minecraftIn.player.containerMenu);
        List<Recipe<?>> listCraftable = collection.getDisplayRecipes(true);
        List<Recipe<?>> listNotCraftable = isFiltering ? Collections.emptyList() : collection.getDisplayRecipes(false);
        Recipe<?> recipe = null;
        if (!listCraftable.isEmpty()) { recipe = listCraftable.get(0); }
        else if (!listNotCraftable.isEmpty()) { recipe = listNotCraftable.get(0); }
        recipeSize = 25;
        if (recipe instanceof RecipeCarpentry npcRecipe && !npcRecipe.isGlobal) { recipeSize = 32; }

        int craftableSize = listCraftable.size();
        int buttonAmount = craftableSize + listNotCraftable.size();
        int borderSize = buttonAmount <= 16 ? MAX_ROW : MAX_ROW_LARGE;
        int l = (int) Math.ceil((float) buttonAmount / (float) borderSize);
        x = xIn;
        y = yIn;
        float f = (float)(x + Math.min(buttonAmount, borderSize) * recipeSize);
        float f1 = (float) (viewportWidth + recipeSize * 2);
        if (f > f1) {
            x = (int)((float)x - uiScalingFactor * (float)((int)((f - f1) / uiScalingFactor)));
        }
        float f2 = (float)(y + l * recipeSize);
        float f3 = (float)(viewportHeight + recipeSize * 2);
        if (f2 > f3) {
            y = (int)((float)y - uiScalingFactor * (float) Mth.ceil((f2 - f3) / uiScalingFactor));
        }
        float f4 = (float)y;
        float f5 = (float)(viewportHeight - recipeSize * 4);
        if (f4 < f5) {
            y = (int)((float)y - uiScalingFactor * (float)Mth.ceil((f4 - f5) / uiScalingFactor));
        }
        isVisible = true;
        recipeButtons.clear();
        for(int buttonId = 0; buttonId < buttonAmount; ++buttonId) {
            boolean isCraftable = buttonId < craftableSize;
            recipe = isCraftable ? listCraftable.get(buttonId) : listNotCraftable.get(buttonId - craftableSize);
            int buttonX = x + MAX_ROW + recipeSize * (buttonId % borderSize);
            int buttonY = y + MAX_ROW_LARGE + recipeSize * (buttonId / borderSize);
            if (isFurnaceMenu) { recipeButtons.add(new OverlaySmeltingRecipeButton(buttonX, buttonY, recipe, isCraftable)); }
            else { recipeButtons.add(new OverlayRecipeButton(buttonX, buttonY, recipe, isCraftable)); }
        }
        lastRecipeClicked = null;
    }

    @Override
    public @Nonnull RecipeCollection getRecipeCollection() { return collection; }

    @Override
    @Nullable
    public Recipe<?> getLastRecipeClicked() { return lastRecipeClicked; }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 0) {
            for(OverlayRecipeButton recipeButton : recipeButtons) {
                if (recipeButton.mouseClicked(mouseX, mouseY, mouseButton)) {
                    lastRecipeClicked = recipeButton.recipe;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (isVisible) {
            time += partialTicks;
            // background
            PoseStack matrixStack = graphics.pose();
            RenderSystem.enableBlend();
            matrixStack.pushPose();
            matrixStack.translate(0.0F, 0.0F, 1000.0F);
            int i = recipeButtons.size() <= 16 ? MAX_ROW : MAX_ROW_LARGE;
            int j = Math.min(recipeButtons.size(), i);
            int k = Mth.ceil((float) recipeButtons.size() / (float)i);
            graphics.blitNineSliced(RECIPE_BOOK_LOCATION, x, y, j * recipeSize + 8, k * recipeSize + 8,
                    4, 32, 32, 82, 208);
            RenderSystem.disableBlend();
            // recipes
            for(OverlayRecipeButton recipeButton : recipeButtons) { recipeButton.render(graphics, mouseX, mouseY, partialTicks); }
            matrixStack.popPose();
        }
    }

    @Override
    public void setVisible(boolean isVisibleIn) { isVisible = isVisibleIn; }

    @Override
    public boolean isVisible() { return isVisible; }

    @OnlyIn(Dist.CLIENT)
    public class OverlayRecipeButton extends AbstractWidget implements PlaceRecipe<Ingredient> {

        protected final Recipe<?> recipe;
        protected final boolean isCraftable;
        protected final List<OverlayRecipeButton.Pos> ingredientPos = Lists.newArrayList();

        public OverlayRecipeButton(int x, int y, Recipe<?> recipeIn, boolean isCraftableIn) {
            super(x, y, 200, 20, CommonComponents.EMPTY);
            recipe = recipeIn;
            int size = 24;
            if (recipe instanceof RecipeCarpentry npcRecipe && !npcRecipe.isGlobal) { size = 31; }
            width = size;
            height = size;
            isCraftable = isCraftableIn;
            calculateIngredientsPositions(recipe);
        }

        protected void calculateIngredientsPositions(Recipe<?> recipe) {
            int recipeWidth = 3;
            int recipeHeight = 3;
            if (recipe instanceof RecipeCarpentry npcRecipe) {
                recipeWidth = npcRecipe.getWidth();
                recipeHeight = npcRecipe.getHeight();
            }
            placeRecipe(recipeWidth, recipeHeight, -1, recipe, recipe.getIngredients().iterator(), 0);
        }

        @Override
        public void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) { defaultButtonNarrationText(narrationElementOutput); }

        @Override
        public void addItemToSlot(Iterator<Ingredient> iterator, int slotId, int amount, int row, int column) {
            ItemStack[] aitemstack = iterator.next().getItems();
            if (aitemstack.length != 0) {
                ingredientPos.add(new Pos(3 + column * 7, 3 + row * 7, aitemstack));
            }
        }

        @Override
        public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            boolean isNcpModRecipe = recipeSize == 32;
            int u = isNcpModRecipe ? 0 : 152;
            int v = isNcpModRecipe ? 0 : isFurnaceMenu ? 130 : 78;
            if (!isCraftable) { u += isNcpModRecipe ? 33 : 26; }
            if (isHoveredOrFocused()) { v += isNcpModRecipe ? 33 : 26; }
            graphics.blit(isNcpModRecipe ? CUSTOM_RECIPE_BOOK_LOCATION : RECIPE_BOOK_LOCATION, getX(), getY(), u, v, width, height);

            PoseStack matrixStack = graphics.pose();
            matrixStack.pushPose();
            matrixStack.translate(getX() + 2, getY() + 2, 150.0D);
            for(OverlayRecipeButton.Pos recipeButtonPos : ingredientPos) {
                matrixStack.pushPose();
                matrixStack.translate(recipeButtonPos.x, recipeButtonPos.y, 0.0D);
                matrixStack.scale(ITEM_RENDER_SCALE, ITEM_RENDER_SCALE, 1.0F);
                matrixStack.translate(-8.0D, -8.0D, 0.0D);
                if (recipeButtonPos.ingredients.length > 0) {
                    graphics.renderItem(recipeButtonPos.ingredients[Mth.floor(time / 30.0F) % recipeButtonPos.ingredients.length], 0, 0);
                }
                matrixStack.popPose();
            }
            matrixStack.popPose();
        }

        @OnlyIn(Dist.CLIENT)
        protected static class Pos {
            public final ItemStack[] ingredients;
            public final int x;
            public final int y;

            public Pos(int xIn, int yIn, ItemStack[] ingredientsIn) {
                x = xIn;
                y = yIn;
                ingredients = ingredientsIn;
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    public class OverlaySmeltingRecipeButton extends OverlayRecipeButton {

        public OverlaySmeltingRecipeButton(int x, int y, Recipe<?> recipe, boolean isCraftable) {
            super(x, y, recipe, isCraftable);
        }

        @Override
        protected void calculateIngredientsPositions(Recipe<?> recipe) {
            ItemStack[] aitemstack = recipe.getIngredients().get(0).getItems();
            ingredientPos.add(new Pos(10, 10, aitemstack));
        }
    }

}
