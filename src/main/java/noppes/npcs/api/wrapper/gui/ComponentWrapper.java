package noppes.npcs.api.wrapper.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.*;
import noppes.npcs.api.gui.IComponent;
import noppes.npcs.api.gui.IComponentStyle;
import noppes.npcs.util.Util;

public class ComponentWrapper implements IComponent {

    public final MutableComponent component;

    public ComponentWrapper(MutableComponent componentIn) { component = componentIn; }

    public static ComponentWrapper of(String text) {
        MutableComponent component;
        if (text == null || text.isEmpty()) { component = Component.empty(); }
        else {
            try { component = Component.Serializer.fromJson(text); }
            catch (Exception e) {
                component = Component.translatable(text);
                if (component.getString().equals(text)) { component = Component.literal(text); }
            }
        }
        return new ComponentWrapper(component);
    }

    @Override
    public Component getMCComponent() { return component; }

    @Override
    public String getKey() {
        if (component.getContents() instanceof TranslatableContents tr) { return tr.getKey(); }
        else if (component.getContents() instanceof LiteralContents lc) { return lc.text(); }
        else if (component.getContents() instanceof KeybindContents kc) { return kc.getName(); }
        else if (component.getContents() instanceof NbtContents nc) { return nc.getNbtPath(); }
        else if (component.getContents() instanceof ScoreContents sc) { return sc.getName(); }
        else if (component.getContents() instanceof SelectorContents pc) { return pc.getPattern(); }
        return getString();
    }

    @Override
    public IComponentStyle getStyle() { return ComponentStyleWrapper.of(component); }

    @Override
    public Object[] getTranslatableObjects() {
        if (component.getContents() instanceof TranslatableContents tr) { return tr.getArgs(); }
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
    public String getFormattedText() { return Util.instance.getOldFormattedText(component); }

    @Override
    public String toJson() { return Component.Serializer.toJson(component); }

}
