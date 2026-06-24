package noppes.npcs.api.wrapper.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.util.text.*;
import noppes.npcs.api.gui.IComponent;
import noppes.npcs.api.gui.IComponentStyle;
import noppes.npcs.util.Util;

import java.util.List;

public class ComponentWrapper implements IComponent {

    public final Component component;

    public ComponentWrapper(Component componentIn) { component = componentIn; }

    public static ComponentWrapper of(String text) { return new ComponentWrapper(Component.jsonToComponent(text)); }

    @Override
    public ITextComponent getMCComponent() { return component.getParent(); }

    @Override
    public String getKey() {
        List<ITextComponent> list = component.getSiblings();
        if (list.isEmpty()) { list.add(new TextComponentString("")); }
        ITextComponent content = list.get(0);
        if (content instanceof TextComponentTranslation) { return ((TextComponentTranslation) content).getKey(); }
        else if (content instanceof TextComponentKeybind) { return ((TextComponentKeybind) content).getKeybind(); }
        else if (content instanceof TextComponentScore) { return ((TextComponentScore) content).getName(); }
        else if (content instanceof TextComponentSelector) { return ((TextComponentSelector) content).getSelector(); }
        return getString();
    }

    @Override
    public IComponentStyle getStyle() { return ComponentStyleWrapper.of(component); }

    @Override
    public Object[] getTranslatableObjects() {
        List<ITextComponent> list = component.getSiblings();
        if (list.isEmpty()) { list.add(new TextComponentString("")); }
        ITextComponent content = list.get(0);
        if (content instanceof TextComponentTranslation) { return ((TextComponentTranslation) content).getFormatArgs(); }
        return new Object[0];
    }

    @Override
    public ComponentWrapper append(String addText) {
        component.appendText(addText);
        return this;
    }

    @Override
    public ComponentWrapper append(IComponent addComponent) {
        component.appendSibling(addComponent.getMCComponent());
        return this;
    }

    @Override
    public String getString() { return Util.instance.deleteColor(component.getFormattedText()); }

    @Override
    public String getFormattedText() { return component.getFormattedText(); }

    @Override
    public String toJson() { return ITextComponent.Serializer.componentToJson(component.getParent()); }

}
