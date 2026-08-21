package noppes.npcs.shared.client.gui.components;

import net.minecraft.network.chat.Component;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.gui.yellow_de.GuiYellowDialogEditor;
import noppes.npcs.client.gui.yellow_de.data.nodes.YDEArea;
import noppes.npcs.shared.client.gui.listeners.IComponentGui;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class YDEAreaNop implements IComponentGui {

    public final @Nonnull YDEArea area;
    public final @Nonnull GuiYellowDialogEditor listener;
    public boolean isLock = false;
    protected long lastClicked = 0L;
    protected @Nonnull ClientProxy.FontContainer font;
    protected List<Component> hoverText = new ArrayList<>();
    public boolean visible = true;
    public boolean isHovered = false;

    public YDEAreaNop(@Nonnull GuiYellowDialogEditor gui, @Nonnull YDEArea areaIn) {
        this.area = areaIn;
        this.listener = gui;
        this.font = new ClientProxy.FontContainer("JetBrainsMono", area.height / 6);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        isHovered = false;
        if (visible) {
            isHovered = mouseX >= area.x && mouseY >= area.y && mouseX < area.x + area.width && mouseY < area.y + area.height;
            if (isHovered && !hoverText.isEmpty()) {
                listener.setHoverText(hoverText);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (isHovered) {
            listener.setActive(area.id);
            if (lastClicked + 500L > System.currentTimeMillis()) {
                // double-click logic if needed
            }
            else { lastClicked = System.currentTimeMillis(); }
            return true;
        }
        return false;
    }

    @Override
    public int[] getCenter() {
        return new int[] { area.x + area.width / 2, area.y + area.height / 2 };
    }

    @Override
    public List<Component> getHoversText() { return hoverText; }

    @Override
    public YDEAreaNop setCustomFont(ClientProxy.FontContainer fontIn) {
        font = fontIn;
        return this;
    }

    @Override
    public int getId() { return area.id; }

    @Override
    public boolean isEnabled() { return isLock; }

    @Override
    public boolean isVisible() { return true; }

    @Override
    public void moveTo(int addX, int addY) {
        area.x += addX;
        area.y += addY;
    }

    @Override
    public YDEAreaNop setHoverTexts(Object... components) {
        hoverText.clear();
        if (components == null) { return this; }
        noppes.npcs.util.Util.instance.putHovers(hoverText, components);
        return this;
    }

    @Override
    public YDEAreaNop setIsEnabled(boolean isEnabled) {
        isLock = isEnabled;
        return this;
    }

    @Override
    public YDEAreaNop setIsVisible(boolean isVisible) {
        this.visible = isVisible;
        return this;
    }

    @Override
    public YDEAreaNop setIsFocused(boolean isFocused) { return this; }

    @Override
    public YDEAreaNop setSize(int width, int height) {
        area.width = width;
        area.height = height;
        font = new ClientProxy.FontContainer("JetBrainsMono", area.height / 6);
        return this;
    }

    @Override
    public GuiComponentType getElementType() { return GuiComponentType.EXTRA; }

    @Override
    public int getWidth() { return area.width; }

    @Override
    public int getHeight() { return area.height; }

    @Override
    public int getX() { return area.x; }

    @Override
    public int getY() { return area.y; }

    @Override
    public boolean isFocused() { return false; }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrolled) { return false; }

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

}