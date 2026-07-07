package noppes.npcs.shared.client.gui.components;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.chat.Component;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.shared.client.gui.listeners.IComponentGui;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.util.Util;
import noppes.npcs.util.ValueUtil;

import java.util.ArrayList;
import java.util.List;

public class GuiLineNop extends Gui implements IComponentGui {

    protected final IGuiInterface listener;
    protected final boolean isVertical;
    protected final float thickness;
    protected final int id;

    protected float scale = 1.0f;
    protected int color;

    protected List<Component> hoverText = new ArrayList<>();

    protected int x, y, width, height;
    protected boolean visible = true;
    protected boolean isHovered = false;

    public GuiLineNop(IGuiInterface listenerIn, int idIn, int xIn, int yIn, int length, float thicknessIn, boolean isVerticalIn, int colorIn) {
        x = xIn;
        y = yIn;
        id = idIn;
        listener = listenerIn;
        isVertical = isVerticalIn;
        color = colorIn;
        thickness = thicknessIn;
        width = isVerticalIn ? (int) thicknessIn : length;
        height = isVerticalIn ? length : (int) thicknessIn;
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
        x += addX;
        y += addY;
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

    public int getWidth() { return isVertical ? (int) thickness : width; }

    public int getHeight() { return 1 + (isVertical ? height : (int) thickness); }

    @Override
    public GuiComponentType getElementType() { return GuiComponentType.EXTRA; }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + (isVertical ? (int) thickness : width)
                    && mouseY < getY() + (isVertical ? height : (int) thickness);
            if (listener != null && isHovered && !hoverText.isEmpty()) { listener.setHoverText(hoverText); }

            GuiScreen.drawRect(
                    (int) (getX() / scale),
                    (int) (getY() / scale),
                    (int) ((getX() + (isVertical ? thickness : width)) / scale),
                    (int) ((getY() + (isVertical ? height : thickness)) / scale),
                    color
            );
        }
    }

    @Override
    public boolean isFocused() { return false; }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrolled) { return false; }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) { return false; }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy) { return false; }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) { return false; }

    @Override
    public boolean keyPressed(char typedChar, int keyCode) { return false; }

    @Override
    public void tick() { }

    @Override
    public boolean isHovered() { return isHovered; }

    public GuiLineNop setColor(int colorIn) {
        color = colorIn;
        return this;
    }

    public GuiLineNop setScale(float scaleIn) {
        scale = ValueUtil.correctFloat(ValueUtil.onlyPositiveFloat(scaleIn, 5.0f), 0.05f, 5.0f);
        return this;
    }

    public int getX() { return x; }

    public int getY() { return y; }

}