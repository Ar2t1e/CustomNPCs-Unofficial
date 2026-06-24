package noppes.npcs.api.wrapper.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import noppes.npcs.api.gui.IComponentStyle;

public class ComponentStyleWrapper implements IComponentStyle {

    protected final Component component;
    protected Style style;

    public ComponentStyleWrapper(Component componentIn) {
        component = componentIn;
        style = componentIn.getStyle();
    }

    public static ComponentStyleWrapper of(Component component) {
        return new ComponentStyleWrapper(component);
    }

    @Override
    public int getColor() { return style.getColor() == null ? -1 : style.getColor().getColorIndex(); }

    @Override
    public String getInsertion() { return style.getInsertion(); }

    @Override
    public boolean isBold() { return style.getBold(); }

    @Override
    public boolean isItalic() { return style.getItalic(); }

    @Override
    public boolean isUnderlined() { return style.getUnderlined(); }

    @Override
    public boolean isStrikethrough() { return style.getStrikethrough(); }

    @Override
    public boolean isObfuscated() { return style.getObfuscated(); }

    @Override
    public void setColor(int color) {
        TextFormatting format;
        switch (color) {
            case 1: format = TextFormatting.DARK_BLUE; break;
            case 2: format = TextFormatting.DARK_GREEN; break;
            case 3: format = TextFormatting.DARK_AQUA; break;
            case 4: format = TextFormatting.DARK_RED; break;
            case 5: format = TextFormatting.DARK_PURPLE; break;
            case 6: format = TextFormatting.GOLD; break;
            case 7: format = TextFormatting.GRAY; break;
            case 8: format = TextFormatting.DARK_GRAY; break;
            case 9: format = TextFormatting.BLUE; break;
            case 10: format = TextFormatting.GREEN; break;
            case 11: format = TextFormatting.AQUA; break;
            case 12: format = TextFormatting.RED; break;
            case 13: format = TextFormatting.LIGHT_PURPLE; break;
            case 14: format = TextFormatting.YELLOW; break;
            case 15: format = TextFormatting.WHITE; break;
            default: format = TextFormatting.BLACK; break;
        }
        style = style.setColor(format);
        component.setStyle(style);
    }

    @Override
    public void setBold(boolean isBold) {
        style = style.setBold(isBold);
        component.setStyle(style);
    }

    @Override
    public void setItalic(boolean isItalic) {
        style = style.setItalic(isItalic);
        component.setStyle(style);
    }

    @Override
    public void setUnderlined(boolean isUnderlined) {
        style = style.setUnderlined(isUnderlined);
        component.setStyle(style);
    }

    @Override
    public void setStrikethrough(boolean isStrikethrough) {
        style = style.setStrikethrough(isStrikethrough);
        component.setStyle(style);
    }

    @Override
    public void setObfuscated(boolean isObfuscated) {
        style = style.setObfuscated(isObfuscated);
        component.setStyle(style);
    }

    @Override
    public void setInsertion(String insertion) {
        style = style.setInsertion(insertion);
        component.setStyle(style);
    }

}
