package noppes.npcs.shared.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.gui.yellow_de.GuiYellowDialogEditor;
import noppes.npcs.client.gui.yellow_de.data.nodes.YDEArea;
import noppes.npcs.shared.client.gui.listeners.IComponentGui;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class YDEAreaNop extends AbstractWidget implements IComponentGui {

    public final @Nonnull YDEArea area;
    public final @Nonnull GuiYellowDialogEditor listener;
    public boolean isLock = false;
    protected long lastClicked = 0L;
    protected @Nonnull ClientProxy.FontContainer font;
    protected List<Component> hoverText = new ArrayList<>();

    public YDEAreaNop(@Nonnull GuiYellowDialogEditor gui, @Nonnull YDEArea areaIn) {
        super(areaIn.x, areaIn.y, areaIn.width, areaIn.height, areaIn.title);
        listener = gui;
        area = areaIn;
        font = new ClientProxy.FontContainer("JetBrainsMono", area.height / 6);
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        isHovered = false;
        if (visible) {
            super.render(graphics, mouseX, mouseY, partialTicks);
            if (isHovered && !hoverText.isEmpty()) { listener.setHoverText(hoverText); }
        }
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (isHovered) {
            listener.setActive(area.id);
            if (lastClicked + 500L > System.currentTimeMillis()) {

            }
            else { lastClicked = System.currentTimeMillis(); }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void updateWidgetNarration(@Nonnull NarrationElementOutput narrationElementOutput) { }

    @Override
    public int[] getCenter() {
        return new int[] { area.x + area.width / 2, area.y + area.height /2 };
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
    public YDEAreaNop setIsVisible(boolean isVisible) { return this; }

    @Override
    public YDEAreaNop setIsFocused(boolean isFocused) { return this; }

    @Override
    public YDEAreaNop setSize(int width, int height) {
        area.width = width;
        area.height = height;
        font = new ClientProxy.FontContainer("JetBrainsMono", area.height / 6);
        return null;
    }

    @Override
    public GuiComponentType getElementType() { return GuiComponentType.EXTRA; }

    @Override
    public int getWidth() { return area.width; }

    @Override
    public int getHeight() { return area.height; }

}
