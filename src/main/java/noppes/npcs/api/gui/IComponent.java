package noppes.npcs.api.gui;

import net.minecraft.network.chat.Component;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.wrapper.gui.ComponentWrapper;

public interface IComponent {

    Component getMCComponent();

    String getKey();

    IComponentStyle getStyle();

    Object[] getTranslatableObjects();

    ComponentWrapper append(@ParamName("text") String text);

    ComponentWrapper append(@ParamName("component") IComponent component);

    String getString();

    String getFormattedText();

    String toJson();

}
