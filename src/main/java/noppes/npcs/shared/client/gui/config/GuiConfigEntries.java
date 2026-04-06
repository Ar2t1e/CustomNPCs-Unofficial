package noppes.npcs.shared.client.gui.config;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.gui.select.SubGuiColorSelector;
import noppes.npcs.client.gui.config.CustomNpcsConfigGui;
import noppes.npcs.config.ConfigElement;
import noppes.npcs.mixin.client.IMouseHandlerMixin;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.shared.client.gui.listeners.IComponentGui;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.Util;

import java.util.List;

public class GuiConfigEntries extends GuiBasic
        implements IComponentGui, ITextfieldListener {

    protected final CustomNpcsConfigGui owningScreen;
    protected final Minecraft mc;
    protected final int slotHeight;
    protected int guiBottom;
    protected int guiRight;
    protected int mouseX;
    protected int mouseY;
    protected int labelX;
    protected int valueX;
    protected int controlX;
    protected int resetX;
    protected int listSize;
    protected int listHeight;
    protected int scrollHeight = 0;
    protected int lastY = 0;
    protected int scrollY = 0;
    protected int maxScrollY = 0;
    protected boolean centerListVertically = true;
    protected boolean mouseInList;
    protected boolean isScrolling;

    public List<ConfigElement> listEntries;
    public int id;

    public GuiConfigEntries(CustomNpcsConfigGui parent, int idIn, int leftIn, int topIn, int rightIn, int bottomIn, int slotHeightIn) {
        super();
        mc = Minecraft.getInstance();
        owningScreen = parent;
        closeOnEsc = false;

        id = idIn;
        width = rightIn - leftIn;
        height = bottomIn - topIn;
        guiBottom = bottomIn;
        guiLeft = leftIn;
        guiTop = topIn;
        guiRight = rightIn;
        slotHeight = slotHeightIn;
    }

    public void resetList(List<ConfigElement> configElements) {
        listEntries = configElements;
        init();
    }

    @Override
    public void buttonEvent(GuiButtonNop button) {
        int id = (int) Math.floor((float) button.id / 4.0f);
        ConfigElement select = listEntries.get(id);
        if (select == null) { return; }
        int buttonID = button.id % 4;
        switch (buttonID) {
            case 0: {
                if (select.isArray()) {
                    owningScreen.entryList = new GuiConfigEntries(owningScreen, id, guiLeft, guiTop + 10, guiRight, guiBottom, slotHeight);
                    owningScreen.init();
                    return;
                }
                if (select.isColor()) {
                    SubGuiColorSelector subgui = new SubGuiColorSelector(select.getInt(), new SubGuiColorSelector.ColorCallback() {
                        @Override
                        public void color(int colorIn) {
                            select.setValue(colorIn);
                            if (button instanceof GuiColorButtonNop bc) { bc.setColor(colorIn); }
                            if (getButton(button.id + 1) != null) { getButton(button.id + 1).setMessage(select.getTextValue());}
                        }
                        @Override
                        public void preColor(int colorIn) {
                            select.setValue(colorIn);
                            if (button instanceof GuiColorButtonNop bc) { bc.setColor(colorIn); }
                            if (getButton(button.id + 1) != null) { getButton(button.id + 1).setMessage(select.getTextValue());}
                        }
                    });
                    subgui.setOffsetY(slotHeight).drawDefaultBackground = false;
                    subgui.object = id;
                    setSubGui(subgui);
                }
                else if (select.isBoolean()) { select.setValue(button.getValue() == 0); }
                if (getButton(button.id + 2) != null) { getButton(button.id + 2).setIsEnabled(!select.isDefault()); }
                if (getButton(button.id + 3) != null) { getButton(button.id + 3).setIsEnabled(!select.isChanged()); }
                break;
            } // change value
            case 1: {
                if (select.isColor()) {
                    SubGuiColorSelector subgui = new SubGuiColorSelector(select.getInt(), new SubGuiColorSelector.ColorCallback() {
                        @Override
                        public void color(int colorIn) {
                            select.setValue(colorIn);
                            if (button instanceof GuiColorButtonNop bc) { bc.setColor(colorIn); }
                            if (getButton(button.id - 1) != null) { getButton(button.id - 1).setMessage(select.getTextValue());}
                        }
                        @Override
                        public void preColor(int colorIn) {
                            select.setValue(colorIn);
                            if (button instanceof GuiColorButtonNop bc) { bc.setColor(colorIn); }
                            if (getButton(button.id - 1) != null) { getButton(button.id - 1).setMessage(select.getTextValue());}
                        }
                    });
                    subgui.setOffsetY(slotHeight).drawDefaultBackground = false;
                    subgui.object = id;
                    setSubGui(subgui);
                    return;
                }
                break;
            } // select color
            case 2: {
                select.setValue(select.defaultValue);
                // changed
                button.setIsEnabled(!select.isDefault());
                if (getButton(button.id + 1) != null) { getButton(button.id + 1).setIsEnabled(!select.isChanged()); }
                // Value
                if (getButton(button.id - 2) != null) {
                    if (select.isBoolean()) {
                        if ((boolean) select.value) { getButton(button.id - 2).setDisplayText(Component.literal("true").withStyle(ChatFormatting.GREEN)); }
                        else { getButton(button.id - 2).setDisplayText(Component.literal("false").withStyle(ChatFormatting.DARK_RED)); }
                    }
                    else { getButton(button.id - 2).setDisplayText(select.getTextValue()); }
                }
                else if (getTextField(button.id - 2) != null) { getTextField(button.id - 2).setValue(select.getTextValue().getString()); }
                if (getButton(button.id - 1) instanceof GuiColorButtonNop bc) { bc.setColor(select.getInt()); }
                break;
            } // default
            case 3: {
                select.setValue(select.firstValue);
                // changed
                if (getButton(button.id - 1) != null) { getButton(button.id - 1).setIsEnabled(!select.isDefault()); }
                button.setIsEnabled(select.isChanged());
                // Value
                if (getButton(button.id - 3) != null) { getButton(button.id - 3).setDisplayText(select.getTextValue()); }
                else if (getTextField(button.id - 3) != null) { getTextField(button.id - 3).setValue(select.getTextValue().getString()); }
                if (getButton(button.id - 2) instanceof GuiColorButtonNop bc) { bc.setColor(select.getInt()); }
                break;
            } // undo
        }
    }

    @Override
    public void init() {
        int l = guiLeft;
        int t = guiTop;
        super.init();
        guiLeft = l;
        guiTop = t;
        labelX = guiLeft + 4;
        resetX = guiRight - 36;
        controlX = resetX - 25;
        int viewWidth = (controlX - labelX - 10) / 2;
        int y = guiTop;
        int id = 0;
        int hL, hE, w;
        int hW = Math.max(20, slotHeight - 2);
        if (owningScreen.titleLine2 == null) { valueX = labelX + viewWidth + 5; }
        else {
            int totalWidth = viewWidth + 15 + 2 * hW;
            valueX = (width - totalWidth) / 2;
            controlX = valueX + viewWidth + 5;
            resetX = controlX + 5 + hW;
        }
        Object[] tfb = new Object[] { ChatFormatting.GREEN + "true", ChatFormatting.DARK_RED + "false" };
        listSize = listEntries.size();
        listHeight = slotHeight * listSize;
        if (listHeight > 0) { scrollHeight = (int)((double)(height - 2) / (double)listHeight * (double)(height - 2)); }
        else { scrollHeight = Integer.MAX_VALUE; }
        maxScrollY = listHeight - (height - 2) - 1;
        for (ConfigElement configElement : listEntries) {
            if (configElement == null) { continue; }
            hL = y + (slotHeight - mc.font.lineHeight) / 2;
            hE = y + (slotHeight - hW) / 2;
            MutableComponent hover = Component.empty()
                    .append(Component.literal(configElement.name).withStyle(ChatFormatting.GOLD))
                    .append("<br>").append(Component.literal("Side: "+configElement.prop.type()).withStyle(ChatFormatting.GRAY));
            String key = "property." + configElement.name.toLowerCase() + ".hover";
            MutableComponent tr = Component.translatable(key);
            if (!tr.getString().equals(key)) {
                hover.append("<br>").append(tr.withStyle(ChatFormatting.YELLOW));
                if (this.id != -1) {
                    try {
                        String ht = tr.getString();
                        if (ht.contains("[") && ht.contains("]")) {
                            ht = ht.substring(ht.indexOf("[") + 1, ht.indexOf("]"));
                            if (ht.contains(",")) {
                                hover.append("<br>").append(Component.literal("Part: " + ht.split(",")[id]));
                            }
                        }
                    } catch (Exception e) { LogWriter.error(e); }
                }
            }
            if (configElement.isArray()) {
                addButton(id * 4, valueX, hE, configElement.getTextValue())
                        .setSize(viewWidth, hW)
                        .setShowShadow(false)
                        .setHoverTexts(hover);
            }
            else if (configElement.isColor()) {
                w = viewWidth * 3 / 4 - 2;
                addButton(id * 4, valueX, hE, configElement.getTextValue())
                        .setSize(w, hW);
                add(new GuiColorButtonNop(this, id * 4 + 1, valueX + w + 2, hE + 1, (int) configElement.value)
                        .setSize(viewWidth / 4 - 1, hW - 2));
                if (configElement.defaultValue != null) {
                    hover.append("<br>")
                            .append(Component.translatable("fml.configgui.tooltip.default",
                            Integer.toHexString((int) configElement.defaultValue).toUpperCase()).withStyle(ChatFormatting.GRAY));
                }
                getButton(id * 4).setHoverTexts(hover);
                getButton(id * 4 + 1).setHoverTexts(hover);
            }
            else if (configElement.isBoolean()) {
                boolean bo = (boolean) configElement.value;
                addButton(id * 4, valueX, hE, false, bo ? 0 : 1, tfb)
                        .setSize(viewWidth, hW);
                if (configElement.defaultValue != null) {
                    hover.append("<br>")
                            .append(Component.translatable("fml.configgui.tooltip.default", configElement.defaultValue).withStyle(ChatFormatting.GRAY));
                }
                getButton(id * 4).setHoverTexts(hover);
            }
            else {
                addTextField(id * 4, valueX + 2, hE + 1, viewWidth - 4, hW - 2, configElement.getTextValue());
                if (configElement.min != null || configElement.max != null) {
                    if (configElement.isFloat() || configElement.isDouble()) {
                        double def = configElement.defaultValue != null ? (double) configElement.defaultValue : (double) configElement.firstValue;
                        double min = configElement.min != null ? (double) configElement.min : Float.MIN_VALUE;
                        double max = configElement.max != null ? (double) configElement.max : Float.MAX_VALUE;
                        getTextField(id * 4).setMinMaxDefault(min, max, (double) configElement.value);
                        hover.append("<br>")
                                .append(Component.translatable("fml.configgui.tooltip.defaultNumeric", min, max, def).withStyle(ChatFormatting.GRAY));
                    } else {
                        long def = configElement.defaultValue != null ? Long.parseLong("" + configElement.defaultValue) : Long.parseLong("" + configElement.firstValue);
                        long min = configElement.min != null ? Long.parseLong("" + configElement.min) : Integer.MIN_VALUE;
                        long max = configElement.max != null ? Long.parseLong("" + configElement.max) : Integer.MAX_VALUE;
                        getTextField(id * 4).setMinMaxDefault(min, max, Long.parseLong("" + configElement.value));
                        hover.append("<br>")
                                .append(Component.translatable("fml.configgui.tooltip.defaultNumeric", min, max, def).withStyle(ChatFormatting.GRAY));
                    }
                }
                else if (configElement.defaultValue != null) {
                    hover.append("<br>")
                            .append(Component.translatable("fml.configgui.tooltip.default", configElement.defaultValue).withStyle(ChatFormatting.GRAY));
                }
                getTextField(id * 4).setHoverTexts(hover);
            }
            if (owningScreen.titleLine2 == null) {
                addLabel(id, labelX, hL, configElement.getLabel())
                        .setSize(viewWidth, 12)
                        .setColor(configElement.prop.type().equalsIgnoreCase("client") ? 0xD0CF90 :
                                configElement.prop.type().equalsIgnoreCase("server") ? 0xB090d0 : 0x90D0CF)
                        .setHoverTexts(hover);            }
            addButton(id * 4 + 2, controlX, hE, "↶")
                    .setSize(hW, hW)
                    .setIsEnabled(!configElement.isDefault())
                    .setHoverTexts("fml.configgui.tooltip.resetToDefault");
            addButton(id * 4 + 3, resetX, hE, "☄")
                    .setSize(hW, hW)
                    .setIsEnabled(configElement.isChanged())
                    .setHoverTexts("fml.configgui.tooltip.undoChanges");
            id++;
            y += slotHeight;
        }
        if (lastY != 0) {
            y = lastY * slotHeight;
            for (GuiLabel label : wrapper.getComponents(GuiLabel.class)) { label.moveTo(0, y); }
            for (GuiTextFieldNop tf : wrapper.getComponents(GuiTextFieldNop.class)) { tf.moveTo(0, y); }
            for (GuiButtonNop button : wrapper.getComponents(GuiButtonNop.class)) { button.moveTo(0, y); }
        }
    }

    @Override
    public int[] getCenter() { return new int[] { guiLeft + width / 2, guiTop + height / 2}; }

    @Override
    public List<Component> getHoversText() { return hoverText; }

    @Override
    public int getId() { return 0; }

    @Override
    public boolean isEnabled() { return true; }

    @Override
    public boolean isVisible() { return true; }

    @Override
    public void moveTo(int addX, int addY) {
        guiLeft += addX;
        guiTop += addY;
    }

    @Override
    public GuiConfigEntries setHoverTexts(Object... components) {
        hoverText.clear();
        if (components == null) { return this; }
        Util.instance.putHovers(hoverText, components);
        return this;
    }

    @Override
    public GuiConfigEntries setIsEnabled(boolean isEnabled) { return this; }

    @Override
    public GuiConfigEntries setIsVisible(boolean isVisible) { return this; }

    @Override
    public GuiConfigEntries setIsFocused(boolean isFocused) { return this; }

    @Override
    public GuiConfigEntries setCustomFont(ClientProxy.FontContainer font) { return this; }

    @Override
    public GuiConfigEntries setSize(int widthIn, int heightIn) {
        width = widthIn;
        height = heightIn;
        return this;
    }

    @Override
    public GuiComponentType getElementType() { return GuiComponentType.EXTRA; }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.enableScissor(guiLeft, guiTop, guiRight, guiBottom);
        // background
        graphics.fillGradient(RenderType.guiOverlay(), guiLeft, guiTop, guiRight, guiTop + 4, 0xFF000000, 0x60000000, 0);
        graphics.fill(RenderType.guiOverlay(), guiLeft, guiTop + 4, guiRight, guiBottom - 4, 0x60000000);
        graphics.fillGradient(RenderType.guiOverlay(), guiLeft, guiBottom - 4, guiRight, guiBottom, 0x60000000, 0xFF000000, 0);
        // gui elements
        for (GuiLabel label : wrapper.getComponents(GuiLabel.class)) {
            label.setIsVisible(label.getY() + label.getHeight() <= guiBottom && label.getY() >= guiTop)
                    .render(graphics, mouseX, mouseY, partialTicks);
        }
        for (GuiTextFieldNop tf : wrapper.getComponents(GuiTextFieldNop.class)) {
            tf.setIsVisible(tf.getY() + tf.getHeight() <= guiBottom && tf.getY() >= guiTop)
                    .render(graphics, mouseX, mouseY, partialTicks);
        }
        for (GuiButtonNop button : wrapper.getComponents(GuiButtonNop.class)) {
            button.setIsVisible(button.getY() + button.getHeight() <= guiBottom && button.getY() >= guiTop)
                    .render(graphics, mouseX, mouseY, partialTicks);
        }
        // draw scrolling
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (scrollHeight < height - 2) {
            double x = mouseX - guiLeft;
            double y = mouseY - guiTop;
            float color = isScrolling ? 0.5f : x >= width - 10 && x < width - 1 && y >= 1 && y < height - 2 ? 0.75f : 1.0f;
            drawScrollBar(graphics, color);
        }
        graphics.disableScissor();

        if (hasSubGui()) {
            hoverText.clear();
            PoseStack matrixStack = graphics.pose();
            matrixStack.translate(0.0F, 0.0F, 60.0F);
            wrapper.subgui.render(graphics, mouseX, mouseY, partialTicks);
            matrixStack.translate(0.0F, 0.0F, -60.0F);
        } else {
            mouseInList = isMouseHover(mouseX, mouseY, guiLeft, guiTop, width, height);
            if (mouseInList) {
                // scrolling pos
                if (scrollHeight < height - 2) {
                    mouseY -= guiTop;
                    if (isScrolling) {
                        isScrolling = ((IMouseHandlerMixin) Minecraft.getInstance().mouseHandler).getActiveButton() == 0;
                        if (isScrolling) {
                            int moved = (int) ((float) mouseY / (float) height * (float) -listSize);
                            if (lastY != moved) { moveY(moved - lastY); }
                        }
                    }
                }
            }
        }
        if (!hoverText.isEmpty()) {
            owningScreen.setHoverText(hoverText);
            hoverText.clear();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (!hasSubGui() && scrollHeight < height - 2) {
            double x = mouseX - guiLeft;
            double y = mouseY - guiTop;
            isScrolling = x >= width - 10 && x < width - 1 && y >= 1 && y < height - 2;
            if (isScrolling) { return true; }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double mouseScrolled) {
        if (!hasSubGui() && mouseScrolled != 0.0D && mouseInList) {
            moveY(mouseScrolled > 0.0D ? 1 : -1);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int key, int key_1, int key_2) {
        if (!hasSubGui()) {
            if (listEntries.size() <= 1) { return false; }
            if (minecraft == null) { minecraft = Minecraft.getInstance(); }
            if (key == InputConstants.getKey("key.keyboard.up").getValue()  || key == minecraft.options.keyUp.getKey().getValue()) { // up
                moveY(1);
                return true;
            }
            else if (key == InputConstants.getKey("key.keyboard.down").getValue() || key == minecraft.options.keyDown.getKey().getValue()) { // down
                moveY(-1);
                return true;
            }
        }
        return super.keyPressed(key, key_1, key_2);
    }

    @Override
    public void subGuiClosed(Screen subgui) {
        if (subgui instanceof  SubGuiColorSelector gui) {
            int id = (int) gui.object;
            ConfigElement select = listEntries.get(id);
            id *= 4;
            if (getButton(id + 2) != null) { getButton(id + 2).setIsEnabled(!select.isDefault()); }
            if (getButton(id + 3) != null) { getButton(id + 3).setIsEnabled(select.isChanged()); }
        }
    }

    @Override
    public void unFocused(GuiTextFieldNop textField) {
        int id = (int) Math.floor((float) textField.id / 4.0f);
        ConfigElement select = listEntries.get(id);
        if (select == null) { return; }
        Object value;
        if (select.isFloat()) { value = textField.getFloat(); }
        else if (select.isFloat()) { value = textField.getDouble(); }
        else if (select.isLong()) { value = textField.getLong(); }
        else if (select.isInt()) { value = textField.getInteger(); }
        else if (select.isByte()) { value = (byte) textField.getInteger(); }
        else if (select.isShort()) { value = (short) textField.getInteger(); }
        else { value = textField.getValue(); }
        select.setValue(value);
        if (getButton(textField.id + 2) != null) { getButton(textField.id + 2).setIsEnabled(!select.isDefault()); }
        if (getButton(textField.id + 3) != null) { getButton(textField.id + 3).setIsEnabled(select.isChanged()); }
    }

    private void moveY(int step) {
        if (hasSubGui()) { return; }
        int y = step * slotHeight;
        int id = 2;
        if (getButton(id) != null && getButton(id).getY() + y - 1 > guiTop) { return; }
        id = (listSize - 1) * 4 + 2;
        if (getButton(id) != null && getButton(id).getY() + 2 * slotHeight + y < guiBottom) { return; }
        scrollY -= step * slotHeight;
        for (GuiLabel label : wrapper.getComponents(GuiLabel.class)) { label.moveTo(0, y); }
        for (GuiTextFieldNop tf : wrapper.getComponents(GuiTextFieldNop.class)) { tf.moveTo(0, y); }
        for (GuiButtonNop button : wrapper.getComponents(GuiButtonNop.class)) { button.moveTo(0, y); }
        if (scrollY < 0) { scrollY = 0; }
        if (scrollY > maxScrollY) { scrollY = maxScrollY; }
        lastY += step;
    }

    private void drawScrollBar(GuiGraphics graphics, float color) {
        RenderSystem.setShaderTexture(0, GuiCustomScrollNop.resource);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0F);
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(guiLeft + width - 10, guiTop, 0.0f);
        int h0 = height / 2;
        int h1 = height - h0;
        graphics.blit(GuiCustomScrollNop.resource, 0, 0, 0, 0, 10, h0);
        graphics.blit(GuiCustomScrollNop.resource, 0, h0, 0, 256 - h1, 10, h1);
        matrixStack.popPose();

        h0 = (scrollHeight - 1) / 2;
        h1 = scrollHeight - h0;
        matrixStack.pushPose();
        matrixStack.translate(guiLeft + width - 9.0f, guiTop + (int) ((float) scrollY / (float) listHeight * (float)(height - 2)) + 1.0f, 0.0f);
        RenderSystem.setShaderColor(color, color, color, 1.0F);
        graphics.blit(GuiCustomScrollNop.resource, 0, 0, 10, 0, 8, h0);
        graphics.blit(GuiCustomScrollNop.resource, 0, h0, 10, 256 - h1, 8, h1);
        matrixStack.popPose();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0F);
    }

}
