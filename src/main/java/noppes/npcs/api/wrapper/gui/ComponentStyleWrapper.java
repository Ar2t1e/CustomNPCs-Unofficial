package noppes.npcs.api.wrapper.gui;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import noppes.npcs.api.gui.IComponentStyle;

public class ComponentStyleWrapper implements IComponentStyle {

    protected final MutableComponent component;
    protected Style style;

    public ComponentStyleWrapper(MutableComponent componentIn) {
        component = componentIn;
        style = componentIn.getStyle();
    }

    public static ComponentStyleWrapper of(MutableComponent component) {
        return new ComponentStyleWrapper(component);
    }

    @Override
    public int getColor() { return style.getColor() == null ? -1 : style.getColor().getValue(); }

    @Override
    public String getInsertion() { return style.getInsertion(); }

    @Override
    public boolean isBold() { return style.isBold(); }

    @Override
    public boolean isItalic() { return style.isItalic(); }

    @Override
    public boolean isUnderlined() { return style.isUnderlined(); }

    @Override
    public boolean isStrikethrough() { return style.isStrikethrough(); }

    @Override
    public boolean isObfuscated() { return style.isObfuscated(); }

    @Override
    public void setColor(int color) {
        style = style.withColor(color);
        component.setStyle(style);
    }

    @Override
    public void setBold(boolean isBold) {
        style = style.withBold(isBold);
        component.setStyle(style);
    }

    @Override
    public void setItalic(boolean isItalic) {
        style = style.withItalic(isItalic);
        component.setStyle(style);
    }

    @Override
    public void setUnderlined(boolean isUnderlined) {
        style = style.withUnderlined(isUnderlined);
        component.setStyle(style);
    }

    @Override
    public void setStrikethrough(boolean isStrikethrough) {
        style = style.withStrikethrough(isStrikethrough);
        component.setStyle(style);
    }

    @Override
    public void setObfuscated(boolean isObfuscated) {
        style = style.withObfuscated(isObfuscated);
        component.setStyle(style);
    }

    @Override
    public void setInsertion(String insertion) {
        style = style.withInsertion(insertion);
        component.setStyle(style);
    }

}
