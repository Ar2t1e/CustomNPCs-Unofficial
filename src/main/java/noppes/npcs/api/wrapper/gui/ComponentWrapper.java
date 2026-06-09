package noppes.npcs.api.wrapper.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.util.text.*;
import noppes.npcs.api.gui.IComponent;
import noppes.npcs.api.gui.IComponentStyle;

public class ComponentWrapper implements IComponent {

    public final Component component;

    public ComponentWrapper(Component componentIn) { component = componentIn; }

    public static ComponentWrapper of(String text) {
        Component component;
        if (text == null || text.isEmpty()) { component = Component.empty(); }
        else {
            try { component = new Component(ITextComponent.Serializer.jsonToComponent(text)); }
            catch (Exception e) {
                component = Component.translatable(text);
                if (component.getFormattedText().equals(text)) { component = Component.literal(text); }
            }
        }
        return new ComponentWrapper(component);
    }

    @Override
    public Component getMCComponent() { return component; }

    @Override
    public String getKey() {
        ITextComponent contents = component.getContents();
        if (contents instanceof TextComponentTranslation) { return ((TextComponentTranslation) contents).getKey(); }
        else if (contents instanceof TextComponentKeybind) { return ((TextComponentKeybind) contents).getKeybind(); }
        else if (contents instanceof TextComponentScore) { return ((TextComponentScore) contents).getName(); }
        else if (contents instanceof TextComponentSelector) { return ((TextComponentSelector) contents).getSelector(); }
        return getString();
    }

    @Override
    public IComponentStyle getStyle() { return ComponentStyleWrapper.of(component); }

    @Override
    public Object[] getTranslatableObjects() {
        ITextComponent contents = component.getContents();
        if (contents instanceof TextComponentTranslation) { return ((TextComponentTranslation) contents).getFormatArgs(); }
        return new Object[0];
    }

    @Override
    public ComponentWrapper append(String addText) {
        component.append(addText);
        return this;
    }

    @Override
    public ComponentWrapper append(IComponent addComponent) {
        component.append(addComponent.getMCComponent());
        return this;
    }

    @Override
    public String getString() { return component.getString(); }

    @Override
    public String getFormattedText() { return component.getFormattedText(); }

    @Override
    public String toJson() { return Component.Serializer.componentToJson(component); }

}
