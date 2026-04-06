package noppes.npcs.shared.client.gui.listeners;

import net.minecraft.network.chat.Component;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.shared.client.gui.components.GuiLabel;

import java.util.List;

// Change from Unofficial (BetaZavr)
public interface IComponentGui {

   int[] getCenter();

   List<Component> getHoversText();

   IComponentGui setCustomFont(ClientProxy.FontContainer font);

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

}
