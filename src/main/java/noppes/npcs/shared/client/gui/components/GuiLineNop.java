package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.shared.client.gui.listeners.IComponentGui;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.util.Util;
import noppes.npcs.util.ValueUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GuiLineNop extends AbstractWidget implements IComponentGui {

    protected final IGuiInterface listener;
    protected final boolean isVertical;
    protected final float thickness;
    protected final int id;

    protected float scale = 1.0f;
    protected int color;

    protected List<Component> hoverText = new ArrayList<>();

    public GuiLineNop(IGuiInterface listenerIn, int idIn, int x, int y, int length, float thicknessIn, boolean isVerticalIn, int colorIn) {
        super(x, y, isVerticalIn ? (int) thicknessIn : length, isVerticalIn ? length : (int) thicknessIn, Component.empty());
        id = idIn;
        listener = listenerIn;
        isVertical = isVerticalIn;
        color = colorIn;
        thickness = thicknessIn;
    }

    @Override
    public int[] getCenter() { return new int[] { getX() + (isVertical ? 0 : width / 2),
            getY() + (isVertical ? height / 2 : 0)}; }

    @Override
    public List<Component> getHoversText() { return hoverText; }

    @Override
    public GuiLineNop setCustomFont(ClientProxy.FontContainer font) { return this; }

    @Override
    public int getId() { return id; }

    @Override
    public boolean isEnabled() { return visible; }

    @Override
    public boolean isVisible() { return visible; }

    @Override
    public void moveTo(int addX, int addY) {
        setX(getX() + addX);
        setY(getY() + addY);
    }

    @Override
    public GuiLineNop setHoverTexts(Object... components) {
        hoverText.clear();
        if (components == null) { return this; }
        Util.instance.putHovers(hoverText, components);
        return this;
    }

    @Override
    public GuiLineNop setIsEnabled(boolean isEnabled) {
        visible = isEnabled;
        return this;
    }

    @Override
    public GuiLineNop setIsVisible(boolean isVisible) {
        visible = isVisible;
        return this;
    }

    @Override
    public GuiLineNop setIsFocused(boolean isFocused) { return this; }

    @Override
    public GuiLineNop setSize(int widthIn, int heightIn) {
        width = isVertical ? (int) thickness : widthIn;
        height = isVertical ? heightIn : (int) thickness;
        return this;
    }

    @Override
    public int getWidth() { return isVertical ? (int) thickness : width; }

    @Override
    public int getHeight() { return 1 + (isVertical ? height : (int) thickness); }

    @Override
    public GuiComponentType getElementType() { return GuiComponentType.EXTRA; }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            super.render(graphics, mouseX, mouseY, partialTicks);
            if (isHovered && !hoverText.isEmpty()) { listener.setHoverText(hoverText); }
        }
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        isHovered = false;
        if (visible) {
            isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + (isVertical ? (int) thickness : width)
                    && mouseY < getY() + (isVertical ? height : (int) thickness);
            PoseStack matrixStack = graphics.pose();
            matrixStack.pushPose();
            matrixStack.translate(getX(), getY(), 0.0f);
            matrixStack.scale(scale, scale, scale);
            graphics.fill(0, 0,
                    (int) ((isVertical ? thickness : width) / scale),
                    (int) ((isVertical ? height : thickness) / scale),
                    color);
            matrixStack.popPose();

        }
    }

    @Override
    protected void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) { }

    public GuiLineNop setColor(int colorIn) {
        color = colorIn;
        return this;
    }

    public GuiLineNop setScale(float scaleIn) {
        scale = ValueUtil.correctFloat(ValueUtil.onlyPositiveFloat(scaleIn, 5.0f), 0.05f, 5.0f);
        return this;
    }

}
