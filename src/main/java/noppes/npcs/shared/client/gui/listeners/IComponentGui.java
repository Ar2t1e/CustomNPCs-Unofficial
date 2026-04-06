package noppes.npcs.shared.client.gui.listeners;

import net.minecraft.network.chat.Component;
import noppes.npcs.api.constants.GuiComponentType;

import java.util.List;

// Change from Unofficial (BetaZavr)
public interface IComponentGui {

    int[] getCenter();

    List<Component> getHoversText();

    int getId();

    boolean isEnabled();

    boolean isVisible();

    void moveTo(int addX, int addY);

    IComponentGui setHoverTexts(Object... components);

    IComponentGui setIsEnabled(boolean isEnabled);

    IComponentGui setIsVisible(boolean isVisible);

    IComponentGui setIsFocused(boolean isFocused);

    IComponentGui setSize(int width, int height);

    GuiComponentType getElementType();

    // for 1.12.2
    void render(int mouseX, int mouseY, float partialTicks);

    boolean isFocused();

    boolean mouseScrolled(double mouseX, double mouseY, double scrolled);

    boolean mouseClicked(double mouseX, double mouseY, int mouseButton);

    boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy);

    boolean mouseReleased(double mouseX, double mouseY, int mouseButton);

    boolean keyPressed(char typedChar, int keyCode);

    void tick();
}
