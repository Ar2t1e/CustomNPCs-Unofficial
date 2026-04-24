package noppes.npcs.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.client.gui.util.GuiTooltipUtils;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

import javax.annotation.Nonnull;
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
    public void buttonEvent(@Nonnull GuiButtonNop button) { if (button.id == 66) { onClose(); } }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        hover = -1;
        PoseStack matrixStack = graphics.pose();
        graphics.blit(background, guiLeft, guiTop + imageHeight, 0, 219, imageWidth, 3);

        matrixStack.pushPose();
        matrixStack.translate(guiLeft + 7.0f, guiTop + 16.0f, 0.0f);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 9; j++) { graphics.blit(RESOURCE_SLOT, j * 18, i * 18, 0, 0, 18, 18); }
        }
        matrixStack.popPose();

        for (int i = 0 ; i < stacks.length; i++) {
            if (stacks[i] == null || stacks[i].isEmpty()) { continue; }
            matrixStack.pushPose();
            int x = (int) (guiLeft + 8.0d + (i % 9) * 18.0d);
            int y = (int) (guiTop + 17.0d + Math.floor(i / 9.0d) * 18.0d);
            matrixStack.translate(x, y, 0.0f);
            graphics.renderItem(stacks[i], 0, 0);
            graphics.renderItemDecorations(font, stacks[i], 0, 0);
            if (isMouseHover(mouseX, mouseY, x, y, 18, 18)) {
                matrixStack.translate(-x, -y + 32.0f, 0.0f);
                GuiTooltipUtils.renderTooltip(graphics, font, stacks[i], mouseX, mouseY);
                hover = i;
            }
            matrixStack.popPose();
        }
    }

    @Override
    public void init() {
        super.init();
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
