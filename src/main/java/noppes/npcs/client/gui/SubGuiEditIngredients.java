package noppes.npcs.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Arrays;

public class SubGuiEditIngredients extends GuiBasic {

    protected int hover;
    public final int id;
    public final ItemStack[] stacks;

    public SubGuiEditIngredients(int idIn, ItemStack[] itemStacks) {
        super();
        setBackground("smallbg.png");
        closeOnEsc = true;
        imageWidth = 176;
        imageHeight = 76;

        id = idIn;
        stacks = Arrays.copyOf(itemStacks, itemStacks.length);
    }

    @Override
    public void buttonEvent(@Nonnull GuiButtonNop button) {
        if (button.id == 66) { onClose(); }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        hover = -1;
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop + imageHeight, 0, 219, imageWidth, 3);

        GlStateManager.pushMatrix();
        mc.getTextureManager().bindTexture(GuiNPCInterface.RESOURCE_SLOT);
        GlStateManager.translate(guiLeft + 7.0f, guiTop + 16.0f, 0.0f);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 9; j++) { drawTexturedModalRect(j * 18, i * 18, 0, 0, 18, 18); }
        }
        GlStateManager.popMatrix();

        for (int i = 0 ; i < stacks.length; i++) {
            if (stacks[i] == null || stacks[i].isEmpty()) { continue; }
            GlStateManager.pushMatrix();
            int x = (int) (guiLeft + 8.0d + (i % 9) * 18.0d);
            int y = (int) (guiTop + 17.0d + Math.floor(i / 9.0d) * 18.0d);
            GlStateManager.translate(x, y, 0.0f);
            mc.getRenderItem().renderItemAndEffectIntoGUI(stacks[i], 0, 0);
            GlStateManager.translate(0.0f, 0.0f, 200.0f);
            drawString(mc.fontRenderer, "" + stacks[i].getCount(), 16 - mc.fontRenderer.getStringWidth("" + stacks[i].getCount()), 9, new Color(0xFFFFFFFF).getRGB());
            if (isMouseHover(mouseX, mouseY, x, y, 18, 18)) {
                GlStateManager.translate(-x, -y + 32.0f, 0.0f);
                drawHoveringText(stacks[i].getTooltip(player,
                        mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL), mouseX, mouseY, fontRenderer);
                hover = i;
            }
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        addLabel(0, guiLeft + 8, guiTop + 5, "gui.recipe.del");
        addButton(66, guiLeft + 57, guiTop + 54, "gui.done")
                .setSize(60, 20)
                .setHoverTexts("hover.back");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (!hasSubGui() && hover != -1 && stacks != null && hover < stacks.length) {
            stacks[hover] = ItemStack.EMPTY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
