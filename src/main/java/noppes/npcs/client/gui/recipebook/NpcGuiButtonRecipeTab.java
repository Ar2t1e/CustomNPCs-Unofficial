package noppes.npcs.client.gui.recipebook;

import java.util.ArrayList;
import java.util.Iterator;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.GuiButtonRecipeTab;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.stats.RecipeBook;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomTabs;
import noppes.npcs.util.Util;

// Displaying a tab button on the left edge of the GUI recipes window
@SideOnly(Side.CLIENT)
public class NpcGuiButtonRecipeTab extends GuiButtonRecipeTab {

    private float animationTime;
    private final CreativeTabs category;
    private final boolean isGlobal;

    public NpcGuiButtonRecipeTab(int buttonId, CreativeTabs tab, boolean globalRecipes) {
        super(buttonId, tab);
        category = tab;
        initTextureValues(153, 2, 35, 0, Util.RECIPE_BOOK);
        isGlobal = globalRecipes;
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!visible) { return; }
        if (animationTime > 0.0F) {
            float f = 1.0F + 0.1F * (float) Math.sin(animationTime / 15.0F * (float) Math.PI);
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) (x + 8), (float) (y + 12), 0.0F);
            GlStateManager.scale(1.0F, f, 1.0F);
            GlStateManager.translate((float) (-(x + 8)), (float) (-(y + 12)), 0.0F);
        }
        hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
        mc.getTextureManager().bindTexture(resourceLocation);
        GlStateManager.disableDepth();
        int k = xTexStart;
        int i = yTexStart;
        if (stateTriggered) { k += xDiffTex; }
        if (hovered) { i += yDiffTex; }
        int j = x;
        if (stateTriggered) { j -= 2; }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(j, y, k, i, width, height);
        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        renderIcon(mc.getRenderItem());
        GlStateManager.enableLighting();
        RenderHelper.disableStandardItemLighting();
        if (animationTime > 0.0F) {
            GlStateManager.popMatrix();
            animationTime -= partialTicks;
        }
    }

    public @Nonnull CreativeTabs getCategory() { return category; }

    private void renderIcon(RenderItem render) {
        ItemStack itemstack = category.getIconItemStack();
        if (category == CreativeTabs.TOOLS) {
            render.renderItemAndEffectIntoGUI(itemstack, x + 3, y + 5);
            render.renderItemAndEffectIntoGUI(CreativeTabs.COMBAT.getIconItemStack(), x + 14, y + 5);
        } else if (category == CreativeTabs.MISC) {
            render.renderItemAndEffectIntoGUI(itemstack, x + 3, y + 5);
            render.renderItemAndEffectIntoGUI(CreativeTabs.FOOD.getIconItemStack(), x + 14, y + 5);
        }
        else if (category == CustomTabs.TOOLS || category == CustomTabs.ITEMS) { // Custom
            if (isGlobal) {
                render.renderItemAndEffectIntoGUI(itemstack, x + 3, y + 5);
                render.renderItemAndEffectIntoGUI(new ItemStack(CustomItems.cloner), x + 14, y + 5);
            } else {
                render.renderItemAndEffectIntoGUI(new ItemStack(CustomBlocks.carpentyBench), x + 9, y + 5);
            }
        } else {
            render.renderItemAndEffectIntoGUI(itemstack, x + 9, y + 5);
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void startAnimation(Minecraft mc) {
        RecipeBook recipebook = mc.player.getRecipeBook();
        if (!RecipeBookClient.RECIPES_BY_TAB.containsKey(category)) {
            RecipeList recipelist = new RecipeList();
            RecipeBookClient.ALL_RECIPES.add(recipelist);
            (RecipeBookClient.RECIPES_BY_TAB.computeIfAbsent(category, (hasRecipeList) -> new ArrayList<>())).add(recipelist);
            (RecipeBookClient.RECIPES_BY_TAB.computeIfAbsent(CreativeTabs.SEARCH, (hasRecipeList) -> new ArrayList<>())).add(recipelist);
        }
        label21: for (RecipeList recipelist : RecipeBookClient.RECIPES_BY_TAB.get(category)) {
            Iterator<IRecipe> iterator = recipelist.getRecipes(recipebook.isFilteringCraftable()).iterator();
            while (true) {
                if (!iterator.hasNext()) {
                    continue label21;
                }
                IRecipe irecipe = iterator.next();
                if (recipebook.isNew(irecipe)) {
                    break;
                }
            }
            animationTime = 15.0F;
            return;
        }
    }

    public boolean updateVisibility() {
        if (category == CustomTabs.TOOLS || category == CustomTabs.ITEMS) { visible = true; }
        else { visible = super.updateVisibility(); }
        return visible;
    }

}