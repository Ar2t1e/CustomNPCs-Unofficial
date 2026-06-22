package net.minecraft.network.chat;

import net.minecraft.util.text.*;
import noppes.npcs.api.interfaces.IgnoreForAPI;
import noppes.npcs.api.mixin.util.text.IStyleMixin;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

@IgnoreForAPI
public class Component implements ICustomTextComponent {

    public static Component from(ITextComponent component) { return new Component(component); }

    public static Component empty() { return new Component("", true); }

    public static Component literal(String message) { return new Component(message, true); }

    public static Component translatable(String localisationKey) { return new Component(localisationKey, false); }

    public static Component translatable(String localisationKey, Object... objects) { return new Component(localisationKey, objects); }

    public Component copy() {
        return new Component(ITextComponent.Serializer.jsonToComponent(ITextComponent.Serializer.componentToJson(parent)));
    }

    private final ITextComponent parent;

    public Component(String message, boolean isSimple) {
        if (isSimple) { parent = new TextComponentString(message); }
        else { parent = new TextComponentTranslation(message); }
    }

    public Component(String message, Object... objects) { parent = new TextComponentTranslation(message, objects); }

    public Component(ITextComponent label) { parent = label; }

    @Override
    public @Nonnull ITextComponent setStyle(@Nonnull Style style) { return parent.setStyle(style); }

    @Override
    public @Nonnull Style getStyle() { return parent.getStyle(); }

    @Nonnull
    @Override
    public Component withColor(int color) {
        ((IStyleMixin) parent.getStyle()).npcs$setColor(color);
        return this;
    }

    @Override
    public @Nonnull ITextComponent appendText(@Nonnull String text) { return parent.appendText(text); }

    @Override
    public @Nonnull ITextComponent appendSibling(@Nonnull ITextComponent component) { return parent.appendSibling(component); }

    @Override
    public @Nonnull String getUnformattedComponentText() { return parent.getUnformattedComponentText(); }

    @Override
    public @Nonnull String getUnformattedText() { return parent.getUnformattedText(); }

    @Override
    public @Nonnull String getFormattedText() { return parent.getFormattedText(); }

    @Override
    public @Nonnull List<ITextComponent> getSiblings() { return parent.getSiblings(); }

    @Override
    public @Nonnull ITextComponent createCopy() { return parent.createCopy(); }

    @Override
    public @Nonnull Iterator<ITextComponent> iterator() { return parent.iterator(); }

    @Override
    public @Nonnull Component append(@Nonnull String text) {
        parent.appendText(text);
        return this;
    }

    @Override
    public @Nonnull Component append(@Nonnull ITextComponent component) {
        parent.appendSibling(component instanceof Component ? ((Component) component).parent : component);
        return this;
    }

    @Nonnull
    @Override
    public String getString() { return Util.instance.deleteColor(parent.getFormattedText()); }

    public Component withStyle(TextFormatting ... textFormats) {
        Style style = parent.getStyle();
        for (TextFormatting textFormatting : textFormats) {
            style.setColor(textFormatting);
            if (textFormatting == TextFormatting.OBFUSCATED) { style.setObfuscated(true); }
            else if (textFormatting == TextFormatting.BOLD) { style.setBold(true); }
            else if (textFormatting == TextFormatting.STRIKETHROUGH) { style.setStrikethrough(true); }
            else if (textFormatting == TextFormatting.UNDERLINE) { style.setUnderlined(true); }
            else if (textFormatting == TextFormatting.ITALIC) { style.setItalic(true); }
        }
        parent.setStyle(style);
        return this;
    }

    public ITextComponent getContents() {
        List<ITextComponent> list = parent.getSiblings();
        if (list.isEmpty()) { return new TextComponentString(""); }
        return list.get(0);
    }

    public ITextComponent getParent() { return parent; }

    public static class Serializer {

        public static String componentToJson(Component component) {
            return component.getString().isEmpty() ? "" : componentToJson(component.parent);
        }

        public static String componentToJson(ITextComponent component) { return ITextComponent.Serializer.componentToJson(component); }

        public static @Nonnull Component jsonToComponent(String content) {
            if (content == null || content.isEmpty()) { return Component.empty(); }
            try {
                ITextComponent iText = ITextComponent.Serializer.jsonToComponent(content);
                return iText != null ? new Component(iText) : Component.empty();
            }
            catch (Exception e) {
                //LogWriter.warn("Not json to component \""+content+"\"");
                return new Component(content);
            }
        }

    }

}
