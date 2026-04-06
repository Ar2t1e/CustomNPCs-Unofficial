package noppes.npcs.shared.client.gui.components;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.client.gui.GuiBoundarySetting;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.listeners.*;
import noppes.npcs.util.Util;
import noppes.npcs.util.ValueUtil;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiCustomWindowNop extends GuiBasic
        implements IComponentGui, ICustomScrollListener, ISliderListener, ITextfieldListener, ITextChangeListener {

    @SideOnly(Side.CLIENT)
    public interface OnClose {
        void onClose(GuiCustomWindowNop window);
    }

    public final int id;

    protected boolean isHovered;
    protected boolean focused = false;
    public boolean active = false;
    public boolean enabled = true;
    public boolean visible = true;
    public final IGuiInterface listener;

    protected IComponentGui point;
    protected OnClose onClose = null;
    protected @Nonnull GuiButtonNop exit;
    protected int colorLine = 0x6C00FF;
    protected int mousePressX;
    protected int mousePressY;
    public boolean isMoving = false;
    public Object[] objs = null;

    public GuiCustomWindowNop(IGuiInterface gui, int idIn, int x, int y, int width, int height, Component titleIn) {
        super();
        id = idIn;
        title = titleIn;
        guiLeft = x;
        guiTop = y;
        setSize(width, height);
        setBackground("bgfilled.png");
        listener = gui;
        exit = addButton(2500, 0, 0, "X")
                .setSize(8, 8)
                .setTexture(ANIMATION_BUTTONS)
                .setDefBack(false)
                .setIsAnim(true)
                .setUV(232, 0, 24, 24)
                .setColor(new Color(0xFF404040).getRGB());
        exit.layerColor = new Color(0xFFFF0000).getRGB();
    }

    @Override
    public void buttonEvent(GuiButtonNop button) {
        if (isHovered && visible) {
            if (button == exit) { visible = false; }
            else { listener.buttonEvent(button); }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        isHovered = visible && isMouseHover(mouseX, mouseY, guiLeft, guiTop, imageWidth, imageHeight);
        if (visible) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) guiLeft, (float) guiTop, (float) id);
            wrapper.mouseX = mouseX;
            wrapper.mouseY = mouseY;
            int x = mouseX;
            int y = mouseY;
            if (wrapper.subgui != null) { x = 0; y = 0; }
            if (drawDefaultBackground && background != null) {
                GlStateManager.pushMatrix();
                GlStateManager.scale(bgScale, bgScale, bgScale);
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(background);
                if (widthTexture != 0 && heightTexture != 0) {
                    int maxRow = ValueUtil.correctInt((int) Math.ceil((float) imageHeight / (float) (heightTexture - 2 * borderTexture)), 2, 10);
                    int maxCol = ValueUtil.correctInt((int) Math.ceil((float) imageWidth / (float) (widthTexture - 2 * borderTexture)), 2, 10);
                    int tileWidth = imageWidth / maxCol;
                    int tileHeight = imageHeight / maxRow;
                    int lastTileWidth = imageWidth - tileWidth * (maxCol - 1);
                    int lastTileHeight = imageHeight - tileHeight * (maxRow - 1);

                    int uOffset = (widthTexture - 2 * borderTexture - tileWidth) / 2;
                    int uMax = widthTexture - lastTileWidth;
                    int vOffset = (heightTexture - 2 * borderTexture - tileHeight) / 2;
                    int vMax = heightTexture - lastTileHeight;

                    for (int col = 0; col < maxCol; ++col) {
                        for (int row = 0; row < maxRow; ++row) {
                            drawTexturedModalRect(col * tileWidth,
                                    row * tileHeight,
                                    col == 0 ? 0 : col == maxCol - 1 ? uMax : uOffset,
                                    row == 0 ? 0 : row == maxRow - 1 ? vMax : vOffset,
                                    col == maxCol - 1 ? lastTileWidth : tileWidth,
                                    row == maxRow - 1 ? lastTileHeight : tileHeight);
                        }
                    }
                }
                else if (imageWidth > 256) {
                    drawTexturedModalRect(0, 0, 0, 0, 250, imageHeight);
                    drawTexturedModalRect(250, 0, 256 - (imageWidth - 250), 0, imageWidth - 250, imageHeight);
                }
                else { drawTexturedModalRect(0, 0, 0, 0, imageWidth, imageHeight); }
                GlStateManager.popMatrix();
            }
            drawTopRect(guiLeft + 3, guiTop + 3, guiLeft + imageWidth - 3, guiTop + 11, zLevel, colorLine + 0xF0000000, colorLine + 0x40000000);
            if (title != null && !title.getString().isEmpty()) {
                GuiButtonNop.renderString(title, guiLeft + 4, guiTop + 1, guiLeft + imageWidth - 20, guiTop + 11,
                        CustomNpcs.MainColor.getRGB() | 255 << 24, false, false);
            }
            for (IComponentGui component : new ArrayList<>(wrapper.components)) { component.render(x, y, partialTicks); }
            try { super.drawScreen(x, y, partialTicks); } catch (Exception ignored) { }
            if (wrapper.subgui != null) {
                GlStateManager.translate(0.0F, 0.0F, 60.0F);
                wrapper.subgui.drawScreen(mouseX, mouseY, partialTicks);
                GlStateManager.translate(0.0F, 0.0F, -60.0F);
            }
            else {
                if (point != null) {
                    double xc = (double)guiLeft + (double) imageWidth / 2.0d;
                    double yc = (double)guiTop + (double) imageHeight / 2.0d;
                    double dist = Math.sqrt((mouseY - yc) * (mouseY - yc) + (mouseX - xc) * (mouseX - xc));
                    double base = Math.sqrt(Math.pow(imageWidth, 2.0d) + Math.pow(imageHeight, 2.0d)) / 2.0d;
                    if (dist <= base * 2.0d) {
                        double a = -1.0d / (2.0d * base - base);
                        double b = -2.0d * a  * base;
                        float alpha = (float) (a * dist + b);
                        if (alpha < 0.0f) { alpha = 0.0f; } else if (alpha > 1.0f) { alpha = 1.0f; }
                        int[] cr = point.getCenter();
                        int color = colorLine + ((int) (alpha * 255.0f) << 24);
                        GuiBoundarySetting.drawLine(cr[0], cr[1], xc, yc, color, 2);
                    }
                }
                if (isMoving && eventButton == 0) {
                    moveTo(mouseX - mousePressX, mouseY - mousePressY);
                    mousePressX = mouseX;
                    mousePressY = mouseY;
                }
                else { isMoving = false; }
                if (!hoverText.isEmpty() && (hoverIsGame || (CustomNpcs.ShowDescriptions && GuiBasic.showHoverText))) {
                    if (!hoverIsGame) { hoverText.add(Component.translatable("hover.alt.h")); }
                    drawHoveringText(toHoverText(), mouseX, mouseY, fontRenderer);
                    hoverText.clear();
                }
            }
            GlStateManager.popMatrix();
        }
    }

    @Override
    public int[] getCenter() { return new int[] { guiLeft + width / 2, guiTop + height / 2}; }

    @Override
    public List<Component> getHoversText() { return hoverText; }

    @Override
    public int getId() { return id; }

    @Override
    public boolean isEnabled() { return enabled; }

    @Override
    public boolean isVisible() { return visible; }

    @Override
    public void moveTo(int addX, int addY) {
        guiLeft += addX;
        guiTop += addY;
        for (IComponentGui component : new ArrayList<>(wrapper.components)) { component.moveTo(addX, addY); }
    }

    @Override
    public GuiCustomWindowNop setHoverTexts(Object... components) {
        hoverText.clear();
        if (components == null) { return this; }
        Util.instance.putHovers(hoverText, components);
        return this;
    }

    @Override
    public GuiCustomWindowNop setIsEnabled(boolean isEnabled) {
        active = isEnabled;
        for (IComponentGui component : new ArrayList<>(wrapper.components)) { component.setIsEnabled(isEnabled); }
        return this;
    }

    @Override
    public GuiCustomWindowNop setIsVisible(boolean isVisible) {
        visible = isVisible;
        for (IComponentGui component : new ArrayList<>(wrapper.components)) { component.setIsVisible(isVisible); }
        return this;
    }

    @Override
    public GuiCustomWindowNop setIsFocused(boolean isFocused) {
        focused = isFocused;
        for (IComponentGui component : new ArrayList<>(wrapper.components)) { component.setIsFocused(false); }
        return this;
    }

    @Override
    public GuiCustomWindowNop setSize(int width, int height) {
        imageWidth = width;
        imageHeight = height;
        return this;
    }

    @Override
    public GuiComponentType getElementType() { return GuiComponentType.EXTRA; }

    @Override
    public void tick() { wrapper.tick(); }

    @Override
    public boolean keyPressed(char typedChar, int keyCode) {
        if (enabled && visible && isHovered) {
            boolean bo = wrapper.keyPressed(typedChar, keyCode);
            if (!bo) {
                switch (keyCode) {
                    case Keyboard.KEY_TAB:
                    case Keyboard.KEY_DOWN: {
                        focusedNextComponent();
                        return true;
                    } // focused next component
                    case Keyboard.KEY_UP: {
                        focusedPrevComponent();
                        return true;
                    } // focused prev component
                }
            }
            return bo;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (enabled && visible && isHovered) {
            boolean bo = wrapper.mouseClicked(mouseX, mouseY, mouseButton);
            if (!bo && mouseButton == 0 && isMouseHover(mouseX, mouseY, guiLeft + 3, guiTop + 3, imageWidth - 3, 8)) {
                mousePressX = (int) mouseX;
                mousePressY = (int) mouseY;
                isMoving = true;
                bo = true;
            }
            return bo;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double mouseScrolled) {
        if (enabled && visible && isHovered) {
            boolean bo = wrapper.mouseScrolled(mouseX, mouseY, mouseScrolled);
            if (!bo) {
                if (mouseScrolled > 0.0d) { focusedNextComponent(); bo = true; }
                else if (mouseScrolled < 0.0d) { focusedPrevComponent(); bo = true; }
            }
            return bo;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy) {
        if (enabled && visible && isHovered) { return wrapper.mouseDragged(mouseX, mouseY, mouseButton, dx, dy); }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (enabled && visible && isHovered) { return wrapper.mouseReleased(mouseX, mouseY, mouseButton); }
        return false;
    }

    @Override
    public void scrollClicked(GuiCustomScrollNop scroll) {
        if (listener instanceof ICustomScrollListener) { ((ICustomScrollListener) listener).scrollClicked(scroll); }
    }

    @Override
    public void scrollDoubleClicked(GuiCustomScrollNop scroll) {
        if (listener instanceof ICustomScrollListener) { ((ICustomScrollListener) listener).scrollDoubleClicked(scroll); }
    }

    @Override
    public void unFocused(GuiTextFieldNop textField) {
        if (listener instanceof ITextfieldListener) { ((ITextfieldListener) listener).unFocused(textField); }
    }

    @Override
    public void mouseDragged(GuiSliderNop slider) {
        if (listener instanceof ISliderListener) { ((ISliderListener) listener).mouseDragged(slider); }
    }

    @Override
    public void mousePressed(GuiSliderNop slider) {
        if (listener instanceof ISliderListener) { ((ISliderListener) listener).mousePressed(slider); }
    }

    @Override
    public void mouseReleased(GuiSliderNop slider) {
        if (listener instanceof ISliderListener) { ((ISliderListener) listener).mouseReleased(slider); }
    }

    @Override
    public void textUpdate(String text) {
        if (listener instanceof ITextChangeListener) { ((ITextChangeListener) listener).textUpdate(text); }
    }

    @Override
    public void onClose() {
        if (onClose != null) { onClose.onClose(this); }
        super.onClose();
    }

    public static void drawTopRect(int left, int top, int right, int bottom, float zLevel, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);

        buffer.pos(left, top, zLevel).color(f1, f2, f3, f).endVertex();
        buffer.pos(left, bottom, zLevel).color(f1, f2, f3, f).endVertex();
        buffer.pos(right, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        buffer.pos(right, top, zLevel).color(f5, f6, f7, f4).endVertex();

        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public void setPoint(IComponentGui component) { point = component; }

    public void setColorLine(int color) { colorLine = color & 0x00FFFFFF; }

    public int getColorLine() { return colorLine; }

    public void resetButtons() {
        exit.setSize(8, 8)
                .setTexture(ANIMATION_BUTTONS)
                .setDefBack(false)
                .setIsAnim(true)
                .setUV(232, 0, 24, 24)
                .setColor(new Color(0xFF404040).getRGB());
        exit.setX(guiLeft + imageWidth - 12);
        exit.setY(guiTop + 3);
        exit.layerColor = new Color(0xFFFF0000).getRGB();
    }

    public boolean isHovered() { return isHovered; }

    public GuiCustomWindowNop addClose(OnClose onCloseIn) {
        onClose = onCloseIn;
        return this;
    }

}
