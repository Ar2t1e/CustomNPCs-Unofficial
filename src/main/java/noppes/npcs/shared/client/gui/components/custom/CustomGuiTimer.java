package noppes.npcs.shared.client.gui.components.custom;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.chat.Component;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiTimerWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketHudTimerEnd;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;

public class CustomGuiTimer extends GuiLabel implements IComponentCustomGui {

    protected final CustomGuiTimerWrapper component;

    public CustomGuiTimer(GuiCustom parentIn, CustomGuiTimerWrapper componenIn) {
        super(parentIn, componenIn.getId(), componenIn.getText(), componenIn.getPosX(), componenIn.getPosY());
        component = componenIn;
        init();
    }

    @Override
    public void init() {
        id = component.getId();
        setX(component.getPosX());
        setY(component.getPosY());
        setWidth(component.getWidth());
        setHeight(component.getHeight());
        if (height <= 0) { setHeight(10); }
        enabled = component.getEnabled();
        visible = component.getVisible();
        if (component.hasHoverText()) { hoverText = component.getHoverTextList(); }
        textColor = component.getColor();
        showShadow = true;
        setMessage(Component.translatable(component.getText()));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (!enabled || !visible) { return; }
        super.render(mouseX, mouseY, partialTicks);
        if (isHovered && component.hasHoverText() && !hoverText.isEmpty() && listener != null) {
            listener.setHoverText(component.getHoverTextList());
        }
    }

    @Override
    public void renderWidget(int mouseX, int mouseY, float partialTicks) {
        isHovered = false;
        if (!visible) { return; }
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, (float)id);
        GlStateManager.scale(component.getScale(), component.getScale(), 0.0F);
        int x = (int) (getX() / component.getScale());
        int y = (int) (getY() / component.getScale());
        int r = (int) ((getX() + width) / component.getScale());
        int b = (int) ((getY() + height)  / component.getScale());
        isHovered = mouseX >= x && mouseY >= y && mouseX < r && mouseY < b;
        GuiButtonNop.renderString(getMessage(), x, y, r, b, textColor, showShadow, false, customFont);
        GlStateManager.popMatrix();
    }

    @Override
    public ICustomGuiComponent component() { return component; }

    @Override
    public @Nonnull Component getMessage() {
        long time = System.currentTimeMillis() - component.now;
        time /= 50L;
        if (component.reverse) { time = component.start - time; }
        if (time < 0 || (!component.reverse && time > component.end)) { Packets.sendServerDelayed(new SPacketHudTimerEnd(component.getOffsetType(), id), this, 250); }
        if (component.reverse) { time += 20; }
        return Component.literal(Util.instance.ticksToElapsedTime(time, false, false, false));
    }

    @Override
    public GuiComponentType getElementType() { return GuiComponentType.TIMER; }

}
