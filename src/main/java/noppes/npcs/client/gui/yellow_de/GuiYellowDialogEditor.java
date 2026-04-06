package noppes.npcs.client.gui.yellow_de;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.handler.data.IDialogCategory;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.controllers.YDEController;
import noppes.npcs.client.gui.util.GuiTooltipUtils;
import noppes.npcs.client.gui.yellow_de.data.YDEData;
import noppes.npcs.client.gui.yellow_de.data.UtilYDE;
import noppes.npcs.client.gui.yellow_de.data.YDELink;
import noppes.npcs.client.gui.yellow_de.data.YDENode;
import noppes.npcs.client.gui.yellow_de.data.nodes.YDECategory;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomWindowNop;
import noppes.npcs.shared.client.gui.components.YDEWindowNop;
import noppes.npcs.shared.client.gui.listeners.IComponentGui;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.CustomNPCsScheduler;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GuiYellowDialogEditor extends GuiBasic {

    public static YDEData YDE_DATA = YDEController.getInstance().getLevelData(ScriptController.getLevelKey());
    // window
    protected float xMouse;
    protected float yMouse;
    protected float w;
    protected float h;
    protected float centerU;
    protected float centerV;
    public float guiScale;
    // back
    public int color = 0xFFF0F0F0;
    // grid
    public YDEWindowNop hovered = null;
    protected boolean mouseOnGrid;
    protected double tempDx;
    protected double tempDy;
    // category
    public @Nonnull YDECategory category;
    // tabs
    protected final @Nonnull GuiCustomWindowNop leftTab;
    protected boolean hoverLeft = false;
    protected final @Nonnull GuiCustomWindowNop rightTab;
    protected boolean hoverRight = false;
    public int select = -2;

    public GuiYellowDialogEditor() {
        super();
        if (minecraft == null) { minecraft = Minecraft.getInstance(); }
        hoverIsGame = true;

        YDE_DATA = YDEController.getInstance().getLevelData(ScriptController.getLevelKey());

        String name = "";
        List<IDialogCategory> cats = DialogController.instance.categories();
        if (!cats.isEmpty()) { name = cats.get(0).getName(); }
        category = YDE_DATA.getCategory(name);

        int tabW = 160;
        leftTab = new GuiCustomWindowNop(this, 0, -tabW, 0, tabW, height, Component.translatable("yde.categories"));
        leftTab.setCustomFont(UtilYDE.FONT);
        leftTab.isLock = false;
        leftTab.lock.setIsEnabled(false);
        leftTab.lock.setUV(232, 0, 24, 24);
        leftTab.lock.setX(leftTab.lock.getX() + 1);
        leftTab.lock.setY(leftTab.lock.getY() - 1);
        leftTab.exit.setDisplayText(Component.literal("x"));
        leftTab.exit.setColor(color);
        leftTab.exit.setCustomFont(UtilYDE.FONT);
        leftTab.exit.setX(leftTab.exit.getX() + 1);
        leftTab.exit.setY(leftTab.exit.getY() - 1);

        rightTab = new GuiCustomWindowNop(this, 1, width, 0, tabW, height, Component.literal("TEST, test, next text"));
        rightTab.setCustomFont(UtilYDE.FONT);
        rightTab.isLock = false;
        rightTab.colorLine = 0x5A8A8C;
        rightTab.lock.setIsEnabled(false);
        rightTab.lock.setUV(232, 0, 24, 24);
        rightTab.lock.setX(rightTab.lock.getX() + 1);
        rightTab.lock.setY(rightTab.lock.getY() - 1);
        rightTab.exit.setDisplayText(Component.literal("x"));
        rightTab.exit.setColor(color);
        rightTab.exit.setCustomFont(UtilYDE.FONT);
        rightTab.exit.setX(rightTab.exit.getX() + 1);
        rightTab.exit.setY(rightTab.exit.getY() - 1);
    }

    @Override
    public void init() {
        // super init:
        if (minecraft == null) { minecraft = Minecraft.getInstance(); }
        guiScale = (float) minecraft.getWindow().getGuiScale();
        setFocused(null);
        renderables.clear();
        children().clear();
        wrapper.init(minecraft, width, height);
        // this init
        leftTab.imageHeight = (int) (height * guiScale);
        rightTab.imageHeight = (int) (height * guiScale);
        // category name
        addLabel(0, (width - UtilYDE.FONT_HEADLINE.width(category.title))/ 2, 0, category.title)
                .setCustomFont(UtilYDE.FONT_HEADLINE)
                .setColor(color);
        w = width * guiScale / 2.0f;
        h = height * guiScale / 2.0f;
        int sel = select;
        select = -2;
        LogWriter.info("TEST: "+category.category +"; "+YDE_DATA.nodes.size());
        for (YDENode node : YDE_DATA.nodes.values()) {
            //LogWriter.info("TEST: "+node.categoryId+" - "+node);
            if (!(node instanceof YDECategory) && node.category.equals(category.category)) {
                YDEWindowNop windowNop = new YDEWindowNop(this, node);
                add(windowNop);
                if (sel == node.id) { select = node.id; }
            }
        }
    }

    @Override
    public void mouseButtonEvent(GuiButtonNop button, int mouseButton) {
        //LogWriter.info("TEST: buttonID: "+button.id+"; mouseButton: "+mouseButton+"; "+active);
        switch (button.id) {
            case 0: {
                break;
            }
            case 2500: {
                if (mouseButton == 0) {
                    if (button.equals(leftTab.exit)) { leftTab.isYDEShow = !leftTab.isYDEShow; }
                    else { rightTab.isYDEShow = !rightTab.isYDEShow; }
                }
                break;
            }
        }
    }

    @Override
    public void renderBackground(@Nonnull GuiGraphics graphics) {
        if (minecraft == null) { minecraft = Minecraft.getInstance(); }
        PoseStack matrixStack = graphics.pose();
        RenderSystem.enableBlend();
        // Grid
        matrixStack.pushPose();
        matrixStack.translate(0.0f, 0.0f, -1.0f);
        // background
        graphics.fill(0, 0, (int) w, (int) h, YDEController.backColor);
        // center
        int gridColor = category.id < 0 ? YDEController.gridColorEmpty : YDEController.gridColor;
        UtilYDE.renderDot(graphics, new float[] {centerU, centerV}, 0.75f, false, gridColor);
        // lines
        float r = (float) FastColor.ARGB32.red(gridColor) / 255.0F;
        float g = (float) FastColor.ARGB32.green(gridColor) / 255.0F;
        float b = (float) FastColor.ARGB32.blue(gridColor) / 255.0F;
        centerU = (int) (category.x + w / 2.0f);
        centerV = (int) (category.y + h / 2.0f);
        float step = 20.0f * category.getScale();
        float t = 0.25f;
        int i = (int) Math.floor((centerV) / -step);
        int j = (int) Math.floor((centerU) / -step);
        float u1 = centerU + j * step;
        float v1 = centerV + i * step;
        // horizontal lines
        while (v1 < height * guiScale) {
            if (i % 10 == 0) {
                UtilYDE.fill(graphics, 0, v1 - t, width * guiScale, v1 + t, r, g, b, i == 0 ? 1.0f : 0.8f);
            }
            else {
                UtilYDE.fill(graphics, 0, v1 - t, width * guiScale, v1 + t, r, g, b, 0.35f);
            }
            v1 += step;
            i++;
        }
        // vertical lines
        while (u1 < width * guiScale) {
            if (j % 10 == 0) {
                UtilYDE.fill(graphics, u1 - t, 0, u1 + t, height * guiScale, r, g, b, j == 0 ? 1.0f : 0.8f);
            }
            else {
                UtilYDE.fill(graphics, u1 - t, 0, u1 + t, height * guiScale, r, g, b, 0.35f);
            }
            u1 += step;
            j++;
        }
        graphics.bufferSource().endBatch();
        matrixStack.popPose();

        // labels
        matrixStack.pushPose();
        matrixStack.translate(leftTab.getX() + leftTab.imageWidth + 8.0f, 0.0f, 200.0f);
        // grid offset
        String label = "U: " + -category.x + "; V: " + -category.y;
        UtilYDE.FONT.draw(graphics, label, 0.0f, 0.0f, color);
        // scale
        label = "x" + df2.format(category.getScale());
        UtilYDE.FONT.draw(graphics, label, 0.0f, UtilYDE.FONT.getHeight(), color);
        // mouse pos hover
        mouseOnGrid = hovered == null &&
                !leftTab.isHovered() && !hoverLeft &&
                !rightTab.isHovered()  && !hoverRight &&
                hoverText.isEmpty();
        matrixStack.popPose();
        if (mouseOnGrid) {
            matrixStack.pushPose();
            float xm = xMouse + 2.0f;
            float ym = yMouse + 11.0f;
            int u = (int) ((xMouse - w / 2.0f - category.x) / category.getScale());
            int v = (int) ((yMouse - h / 2.0f - category.y) / category.getScale());
            label = u + "; " + v;
            float f = 4 + UtilYDE.FONT.width(label);
            if (xm + f > rightTab.getX() - 5.0f) { xm -= xm - rightTab.getX() + 5.0f + f; }
            f = 4 + UtilYDE.FONT.getHeight();
            if (ym + f > h) { ym -= 19.0f; }
            matrixStack.translate(xm, ym, 1.0f);
            graphics.fill(-2, -1,
                    UtilYDE.FONT.width(label) + 2, UtilYDE.FONT.getHeight() + 1, YDEController.backColor);
            UtilYDE.FONT.draw(graphics, label, 0, 0, color);
            matrixStack.popPose();
        }
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (minecraft == null) { minecraft = Minecraft.getInstance(); }
        xMouse = mouseX * guiScale / 2.0f;
        yMouse = mouseY * guiScale / 2.0f;
        RenderSystem.enableBlend();
        // tabs
        PoseStack matrixStack = graphics.pose();
        int tabHeight = leftTab.imageHeight / 2;
        int arrowHeight = (int) (25.0f * guiScale);
        matrixStack.pushPose();
        matrixStack.scale(2.0f / guiScale, 2.0f / guiScale, 2.0f / guiScale);
        // main
        wrapper.graphics = graphics;
        wrapper.mouseX = (int) (((hasSubGui() ? 0.0f : xMouse) - centerU) / category.getScale());
        wrapper.mouseY = (int) (((hasSubGui() ? 0.0f : yMouse) - centerV) / category.getScale());
        if (drawDefaultBackground) { renderBackground(graphics); }
        // super

        hovered = null;
        matrixStack.pushPose();
        matrixStack.translate(centerU, centerV, 0.0f);
        matrixStack.scale(category.getScale(), category.getScale(), category.getScale());
        for (IComponentGui component : new ArrayList<>(wrapper.components)) {
            if (component instanceof Renderable renderable) { renderable.render(graphics, wrapper.mouseX, wrapper.mouseY, partialTicks); }
        }
        if (wrapper.subgui != null) {
            matrixStack.translate(0.0F, 0.0F, 60.0F);
            wrapper.subgui.render(graphics, mouseX, mouseY, partialTicks);
            matrixStack.translate(0.0F, 0.0F, -60.0F);
        }
        else if (hoverIsGame || (CustomNpcs.ShowDescriptions && GuiBasic.showHoverText) && !hoverText.isEmpty()) {
            if (!hoverIsGame) { hoverText.add(Component.translatable("hover.alt.h")); }
            RenderSystem.disableDepthTest();
            GuiTooltipUtils.renderTooltip(graphics, font, hoverText, Optional.empty(), mouseX, ValueUtil.correctInt(mouseY, 16, height));
            hoverText.clear();
        }
        matrixStack.popPose();

        matrixStack.pushPose();
        matrixStack.translate(0.0f, 0.0f, 500.0f);
        // left arrow
        if (leftTab.isYDEShow) {
            hoverLeft = xMouse >= leftTab.getX() + leftTab.imageWidth && xMouse < leftTab.getX() + leftTab.imageWidth + 7;
            leftTab.transferTo(0, 0);
        }
        else {
            hoverLeft = xMouse < 7.0f;
            leftTab.transferTo((int) (-leftTab.imageWidth + (hoverLeft ? -xMouse + 7.0f : 0.0f)), 0);
        }
        if (leftTab.visible) {
            matrixStack.pushPose();
            int c = hoverLeft ? YDEController.backHoverColor : YDEController.backColor;
            matrixStack.translate(leftTab.getX() + leftTab.getWidth(), 0.0f, 1.0f);
            graphics.fill(0, 0, 6, tabHeight, 0xF0000000 | (c & 0xFFFFFF));
            matrixStack.translate(0.0f, (tabHeight - arrowHeight / 2.0f) / 2.0f, 0.0f);
            matrixStack.scale(0.5f, 0.5f, 0.5f);
            c = hoverLeft ? YDEController.backColor : YDEController.backHoverColor;
            graphics.hLine(-1, 10, 1, c);
            graphics.vLine(10, 1, arrowHeight, c);
            graphics.hLine(-1, 10, arrowHeight, c);
            matrixStack.popPose();

            matrixStack.pushPose();
            matrixStack.translate(leftTab.getX() + leftTab.getWidth() + 1.0f, (tabHeight - UtilYDE.FONT.getHeight()) / 2.0f, 1.5f);
            UtilYDE.FONT.draw(graphics, leftTab.isYDEShow ? "<" : ">", 0.0f, 0.0f, color);
            matrixStack.popPose();
        }
        // right arrow
        if (rightTab.isYDEShow) {
            hoverRight = xMouse >= rightTab.getX() - 6.0f && xMouse < rightTab.getX();
            rightTab.transferTo((int) (w - leftTab.imageWidth), 0);
        }
        else {
            hoverRight = xMouse > w - 8.0f;
            rightTab.transferTo((int) (w + (hoverRight ? -1.0f * (xMouse - w + 8.0f): 0.0f)), 0);
        }
        if (rightTab.visible) {
            matrixStack.pushPose();
            int c = hoverRight ? YDEController.backHoverColor : YDEController.backColor;
            matrixStack.translate(rightTab.getX() - 6.0f, 0.0f, 1.0f);
            graphics.fill(0, 0, 6, tabHeight, 0xF0000000 | (c & 0xFFFFFF));
            matrixStack.translate(0.0f, (tabHeight - arrowHeight / 2.0f) / 2.0f, 0.0f);
            matrixStack.scale(0.5f, 0.5f, 0.5f);
            c = hoverRight ? YDEController.backColor : YDEController.backHoverColor;
            graphics.hLine(1, 12, 1, c);
            graphics.vLine(1, 1, arrowHeight, c);
            graphics.hLine(1, 12, arrowHeight, c);
            matrixStack.popPose();

            matrixStack.pushPose();
            matrixStack.translate(rightTab.getX() - 4.5f, (tabHeight - UtilYDE.FONT.getHeight()) / 2.0f, 1.5f);
            UtilYDE.FONT.draw(graphics, rightTab.isYDEShow ? ">" : "<", 0.0f, 0.0f, color);
            matrixStack.popPose();
        }
        leftTab.render(graphics, (int) xMouse, (int) yMouse, partialTicks);
        rightTab.render(graphics, (int) xMouse, (int) yMouse, partialTicks);
        matrixStack.popPose();
        matrixStack.popPose();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrolled) {
        boolean bo = leftTab.mouseScrolled(mouseX, mouseY, scrolled) ||
                rightTab.mouseScrolled(mouseX, mouseY, scrolled) ||
                wrapper.mouseScrolled(xMouse, yMouse, scrolled);
        if (!bo) {
            float f0 = category.getScale() * (scrolled < 0.0f ? 0.1f : -0.1f);
            float f1 = ValueUtil.correctFloat(category.getScale() + f0, 0.1f, 1.0f);
            if (f1 != category.getScale()) { category.setScale(f1); }
            bo = true;
        }
        return bo;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (hoverLeft) {
            mouseButtonEvent(leftTab.exit, 0);
            return true;
        } else if (hoverRight) {
            mouseButtonEvent(rightTab.exit, 0);
            return true;
        }
        return leftTab.mouseClicked(mouseX, mouseY, mouseButton) ||
                rightTab.mouseClicked(mouseX, mouseY, mouseButton) ||
                wrapper.mouseClicked(xMouse, yMouse, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy) {
        boolean bo = leftTab.mouseDragged(mouseX, mouseY, mouseButton, dx, dy) ||
                rightTab.mouseDragged(mouseX, mouseY, mouseButton, dx, dy) ||
                wrapper.mouseDragged(xMouse, yMouse, mouseButton, dx, dy);
        if (mouseOnGrid || !bo) {
            tempDx += dx;
            tempDy += dy;
            int x = (int) (Math.floor(tempDx) * guiScale / 2.0d);
            int y = (int) (Math.floor(tempDy) * guiScale / 2.0d);
            if (x != 0 || y != 0) {
                if (x != 0) {
                    category.x += x;
                    tempDx -= x / guiScale * 2.0d;
                }
                if (y != 0) {
                    category.y += y;
                    tempDy -= y / guiScale * 2.0d;
                }
            }
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        return leftTab.mouseReleased(mouseX, mouseY, mouseButton) ||
                rightTab.mouseReleased(mouseX, mouseY, mouseButton) ||
                wrapper.mouseReleased(xMouse, yMouse, mouseButton);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!GuiBasic.isEscKey(keyCode) && (leftTab.keyPressed(keyCode, scanCode, modifiers) ||
                rightTab.keyPressed(keyCode, scanCode, modifiers))) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        super.onClose();
        NoppesUtil.requestOpenGUI(EnumGuiType.MainMenuGlobal);
    }

    @Override
    public void save() {
        YDEController.getInstance().save();
    }

    public void movedSelectNodes(int addX, int addY) {
        YDEWindowNop hovered = null;
        boolean isMoved = false;
        for (IComponentGui component : wrapper.components) {
            if (component instanceof YDEWindowNop wNop) {
                if (wNop.isFocused()) {
                    wNop.setIsFocused(true);
                    wNop.moveTo(addX, addY);
                    isMoved = true;
                }
                if (wNop.isHovered()) { hovered = wNop; }
            }
        }
        if (!isMoved && hovered != null) {
            hovered.setIsFocused(true);
            hovered.moveTo(addX, addY);
        }
    }

    public void doubleClicked(YDEWindowNop windowNop) {
        for (IComponentGui component : wrapper.components) {
            if (component instanceof YDEWindowNop wNop) { wNop.setIsFocused(false); }
        }
        CustomNPCsScheduler.runTack(() -> selectLinks(windowNop), 100);
    }

    public void selectLinks(@Nullable YDEWindowNop windowNop) {
        if (windowNop != null) {
            windowNop.setIsFocused(true);
            for (YDELink link : windowNop.node.links) { selectLinks(get(link.nextNodId, YDEWindowNop.class)); }
        }
    }

    public void setActive(@Nullable YDEWindowNop windowNop) {
        if (windowNop != null) { select = windowNop.id; }
    }

}
