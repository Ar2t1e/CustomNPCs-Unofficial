package noppes.npcs.client.gui.recipebook;

import java.util.*;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.recipebook.GuiRecipeOverlay;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.recipebook.PlaceRecipe;

// Displaying variations of one recipe in the GUI recipe window
@SideOnly(Side.CLIENT)
public class CustomGuiRecipeOverlay extends GuiRecipeOverlay {

    protected static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
    protected final List<OverlayRecipeButton> recipeButtons = new ArrayList<>();
    protected IRecipe lastRecipeClicked;
    protected Minecraft minecraft;
    protected RecipeList collection;
    protected float time;
    protected boolean visible;
    protected int x;
    protected int y;

    // New from Unofficial (BetaZavr)
    protected static final ResourceLocation CUSTOM_RECIPE_BOOK_LOCATION = new ResourceLocation(CustomNpcs.MODID, "textures/gui/recipe_book.png");
    protected static final int MAX_ROW = 4;
    protected static final int MAX_ROW_LARGE = 5;
    protected static final float ITEM_RENDER_SCALE = 0.42F;
    public static int BUTTON_SIZE = 25;
    protected boolean isFurnaceMenu;

    @Override
    public void init(@Nonnull Minecraft minecraftIn, @Nonnull RecipeList collectionIn, int xIn, int yIn,
                     int viewportWidth, int viewportHeight, float uiScalingFactor, @Nonnull RecipeBook book) {
        minecraft = minecraftIn;
        collection = collectionIn;
        if (minecraft.player != null && minecraft.player.openContainer instanceof ContainerFurnace) {
            isFurnaceMenu = true;
        }
        boolean isFiltering = book.isFilteringCraftable();
        List<IRecipe> listCraftable = collection.getDisplayRecipes(true);
        List<IRecipe> listNotCraftable = isFiltering ? Collections.emptyList() : collection.getDisplayRecipes(false);

        IRecipe recipe = null;
        if (!listCraftable.isEmpty()) { recipe = listCraftable.get(0); }
        else if (!listNotCraftable.isEmpty()) { recipe = listNotCraftable.get(0); }
        BUTTON_SIZE = 25;
        if (recipe instanceof RecipeCarpentry && !((RecipeCarpentry) recipe).isGlobal) { BUTTON_SIZE = 32; }

        int craftableSize = listCraftable.size();
        int buttonAmount = craftableSize + listNotCraftable.size();
        int borderSize = buttonAmount <= 16 ? MAX_ROW : MAX_ROW_LARGE;
        int l = (int) Math.ceil((float) buttonAmount / (float) borderSize);
        x = xIn;
        y = yIn;

        float f = (float) (x + Math.min(buttonAmount, borderSize) * BUTTON_SIZE);
        float f1 = (float) (viewportWidth + BUTTON_SIZE * 2);
        if (f > f1) {
            x = (int) ((float) x - uiScalingFactor * (float) ((int) ((f - f1) / uiScalingFactor)));
        }
        float f2 = (float) (y + l * BUTTON_SIZE);
        float f3 = (float) (viewportHeight + BUTTON_SIZE * 2);
        if (f2 > f3) {
            y = (int) ((float) y - uiScalingFactor * (float) MathHelper.ceil((f2 - f3) / uiScalingFactor));
        }
        float f4 = (float) y;
        float f5 = (float) (viewportHeight - BUTTON_SIZE * 4);
        if (f4 < f5) {
            y = (int) ((float) y - uiScalingFactor * (float) MathHelper.ceil((f4 - f5) / uiScalingFactor));
        }
        visible = true;
        recipeButtons.clear();
        for (int buttonId = 0; buttonId < buttonAmount; ++buttonId) {
            boolean isCraftable = buttonId < craftableSize;
            recipe = isCraftable ? listCraftable.get(buttonId) : listNotCraftable.get(buttonId - craftableSize);
            int buttonX = x + MAX_ROW + BUTTON_SIZE * (buttonId % borderSize);
            int buttonY = y + MAX_ROW_LARGE + BUTTON_SIZE * (buttonId / borderSize);
            if (isFurnaceMenu) { recipeButtons.add(new OverlaySmeltingRecipeButton(buttonX, buttonY, recipe, isCraftable)); }
            else { recipeButtons.add(new OverlayRecipeButton(buttonX, buttonY, recipe, isCraftable)); }
        }
        lastRecipeClicked = null;
    }

    @Override
    public @Nonnull IRecipe getLastRecipeClicked() { return lastRecipeClicked; }

    @Override
    public @Nonnull RecipeList getRecipeList() { return collection; }

    @Override
    public boolean buttonClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            for (OverlayRecipeButton recipeButton : recipeButtons) {
                if (recipeButton.mousePressed(minecraft, mouseX, mouseY)) {
                    lastRecipeClicked = recipeButton.recipe;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (visible && minecraft != null) {
            time += partialTicks;
            // background
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            minecraft.getTextureManager().bindTexture(RECIPE_BOOK_LOCATION);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 170.0F);
            int i = recipeButtons.size() <= 16 ? MAX_ROW : MAX_ROW_LARGE;
            int u = Math.min(recipeButtons.size(), i);
            int v = MathHelper.ceil((float)recipeButtons.size() / (float)i);
            nineInchSprite(u, v);
            GlStateManager.disableBlend();
            RenderHelper.disableStandardItemLighting();
            // recipes
            for (OverlayRecipeButton button : recipeButtons) { button.drawButton(minecraft, mouseX, mouseY, partialTicks); }
            GlStateManager.popMatrix();
        }
    }

    private void nineInchSprite(int u, int v) {
        int buttonSize = BUTTON_SIZE - 1;
        int border = MAX_ROW;
        int widthIn = 82;
        int heightIn = 208;
        drawTexturedModalRect(x, y, widthIn, heightIn, border, border);
        drawTexturedModalRect(x + border * 2 + u * buttonSize, y, widthIn + buttonSize + border, heightIn, border, border);
        drawTexturedModalRect(x, y + border * 2 + v * buttonSize, widthIn, heightIn + buttonSize + border, border, border);
        drawTexturedModalRect(x + border * 2 + u * buttonSize, y + border * 2 + v * buttonSize, widthIn + buttonSize + border, heightIn + buttonSize + border, border, border);
        for (int i = 0; i < u; ++i) {
            drawTexturedModalRect(x + border + i * buttonSize, y, widthIn + border, heightIn, buttonSize, border);
            drawTexturedModalRect(x + border + (i + 1) * buttonSize, y, widthIn + border, heightIn, border, border);
            for (int j = 0; j < v; ++j) {
                if (i == 0) {
                    drawTexturedModalRect(x, y + border + j * buttonSize, widthIn, heightIn + border, border, buttonSize);
                    drawTexturedModalRect(x, y + border + (j + 1) * buttonSize, widthIn, heightIn + border, border, border);
                }
                drawTexturedModalRect(x + border + i * buttonSize, y + border + j * buttonSize, widthIn + border, heightIn + border, buttonSize, buttonSize);
                drawTexturedModalRect(x + border + (i + 1) * buttonSize, y + border + j * buttonSize, widthIn + border, heightIn + border, border, buttonSize);
                drawTexturedModalRect(x + border + i * buttonSize, y + border + (j + 1) * buttonSize, widthIn + border, heightIn + border, buttonSize, border);
                drawTexturedModalRect(x + border + (i + 1) * buttonSize - 1, y + border + (j + 1) * buttonSize - 1, widthIn + border, heightIn + border, border + 1, border + 1);

                if (i == u - 1) {
                    drawTexturedModalRect(x + border * 2 + u * buttonSize, y + border + j * buttonSize, widthIn + buttonSize + border, heightIn + border, border, buttonSize);
                    drawTexturedModalRect(x + border * 2 + u * buttonSize, y + border + (j + 1) * buttonSize, widthIn + buttonSize + border, heightIn + border, border, border);
                }
            }
            drawTexturedModalRect(x + border + i * buttonSize, y + border * 2 + v * buttonSize, widthIn + border, heightIn + buttonSize + border, buttonSize, border);
            drawTexturedModalRect(x + border + (i + 1) * buttonSize, y + border * 2 + v * buttonSize, widthIn + border, heightIn + buttonSize + border, border, border);
        }
    }

    @Override
    public void setVisible(boolean isVisible) { visible = isVisible; }

    @Override
    public boolean isVisible() { return visible; }

    @SideOnly(Side.CLIENT)
    public class OverlayRecipeButton extends GuiButton implements PlaceRecipe<Ingredient> {

        protected final IRecipe recipe;
        protected final boolean isCraftable;
        protected final List<OverlayRecipeButton.Pos> ingredientPos = Lists.newArrayList();

        public OverlayRecipeButton(int x, int y, IRecipe recipeIn, boolean craftable) {
            super(0, x, y, "");
            recipe = recipeIn;
            int size = 24;
            if (recipe instanceof RecipeCarpentry && !((RecipeCarpentry) recipe).isGlobal) { size = 31; }
            width = size;
            height = size;
            isCraftable = craftable;
            calculateIngredientsPositions();
        }

        protected void calculateIngredientsPositions() {
            int recipeWidth = 3;
            int recipeHeight = 3;
            if (recipe instanceof RecipeCarpentry) {
                recipeWidth = ((RecipeCarpentry) recipe).getWidth();
                recipeHeight = ((RecipeCarpentry) recipe).getHeight();
            }
            placeRecipe(recipeWidth, recipeHeight, -1, recipe, recipe.getIngredients().iterator(), 0);
        }

        @Override
        public void addItemToSlot(Iterator<Ingredient> ingredient, int slotId, int amount, int row, int column) {
            ItemStack[] aitemstack = ingredient.next().getMatchingStacks();
            if (aitemstack.length != 0) {
                ingredientPos.add(new Pos(3 + column * 7, 3 + row * 7, aitemstack));
            }
        }

        @Override
        public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            // background
            boolean isNcpModRecipe = BUTTON_SIZE == 32;
            int u = isNcpModRecipe ? 0 : 152;
            int v = isNcpModRecipe ? 0 : isFurnaceMenu ? 130 : 78;
            if (!isCraftable) { u += isNcpModRecipe ? 33 : 26; }
            hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
            if (hovered) { v += isNcpModRecipe ? 33 : 26; }
            mc.getTextureManager().bindTexture(isNcpModRecipe ? CUSTOM_RECIPE_BOOK_LOCATION : RECIPE_BOOK_LOCATION);
            drawTexturedModalRect(x, y, u, v, width, height);
            // items
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 2, y + 2, 150.0D);
            for(OverlayRecipeButton.Pos recipeButtonPos : ingredientPos) {
                if (recipeButtonPos.ingredients.length > 0) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(recipeButtonPos.x, recipeButtonPos.y, 0.0D);
                    GlStateManager.scale(ITEM_RENDER_SCALE, ITEM_RENDER_SCALE, 1.0F);
                    GlStateManager.translate(-8.0D, -8.0D, 0.0D);
                    minecraft.getRenderItem().renderItemAndEffectIntoGUI(recipeButtonPos.ingredients[MathHelper.floor(time / 30.0F) % recipeButtonPos.ingredients.length], 0, 0);
                    GlStateManager.popMatrix();
                }
            }
            GlStateManager.popMatrix();
            if (!isFurnaceMenu && hovered) {
                drawCenteredString(minecraft.fontRenderer,
                        Component.translatable("item.craft.type." +
                                (recipe instanceof IShapedRecipe || recipe instanceof RecipeCarpentry && ((RecipeCarpentry) recipe).isShaped())).getString(),
                        x + width / 2, y - 12, 0xFFFFFFFF);
            }
        }

        @SideOnly(Side.CLIENT)
        protected class Pos {
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

    @SideOnly(Side.CLIENT)
    public class OverlaySmeltingRecipeButton extends OverlayRecipeButton {

        public OverlaySmeltingRecipeButton(int x, int y, IRecipe recipeIn, boolean craftable) {
            super(x, y, recipeIn, craftable);
        }

        @Override
        protected void calculateIngredientsPositions() {
            ItemStack[] aitemstack = recipe.getIngredients().get(0).getMatchingStacks();
            ingredientPos.add(new Pos(10, 10, aitemstack));
        }

    }

}
