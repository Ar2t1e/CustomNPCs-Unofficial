package noppes.npcs.client.gui.yellow_de;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FastColor;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.handler.data.IDialogCategory;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.controllers.YDEController;
import noppes.npcs.client.gui.SubGuiEditText;
import noppes.npcs.client.gui.util.GuiTooltipUtils;
import noppes.npcs.client.gui.yellow_de.data.*;
import noppes.npcs.client.gui.yellow_de.data.nodes.*;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.*;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.shared.client.gui.listeners.IComponentGui;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.CustomNPCsScheduler;
import noppes.npcs.util.Util;
import noppes.npcs.util.ValueUtil;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GuiYellowDialogEditor extends GuiBasic
        implements ICustomScrollListener, ITextfieldListener {

    public static YDEData YDE_DATA = YDEController.getInstance().getLevelData(ScriptController.getLevelKey());
    // window
    protected final List<IComponentGui> notScaledComponents = new ArrayList<>();
    protected float xMouse;
    protected float yMouse;
    protected float w;
    protected float h;
    protected float centerU;
    protected float centerV;
    public float guiScale;
    // back
    protected int space = 30;
    protected final int[] pos = new int[2];
    public int color = 0xFFF0F0F0;
    // grid
    public YDEWindowNop hovered = null;
    protected boolean mouseOnGrid;
    protected double tempDx;
    protected double tempDy;
    // category
    public static @Nullable YDECategory category;
    protected Set<Integer> selects = new LinkedHashSet<>();
    // tabs
    protected final @Nonnull GuiCustomWindowNop leftTab;
    protected final @Nonnull GuiCustomWindowNop rightTab;
    protected boolean hoverLeft = false;
    protected boolean hoverRight = false;
    protected GuiCustomScrollNop helper;

    public GuiYellowDialogEditor() {
        super();
        if (minecraft == null) { minecraft = Minecraft.getInstance(); }
        hoverIsGame = true;
        hoverFont = UtilYDE.FONT;

        YDE_DATA = YDEController.getInstance().getLevelData(ScriptController.getLevelKey());

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

        rightTab = new GuiCustomWindowNop(this, 1, width, 0, tabW, height, Component.translatable("type.help"));
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

        if (FastColor.ARGB32.alpha(YDEController.backColor) != 192) {
            int alpha = 0xC0000000;
            YDEController.backColor = (YDEController.backColor & 0xFFFFFF) | alpha;
            YDEController.backHoverColor = (YDEController.backHoverColor & 0xFFFFFF) | alpha;
            YDEController.textColor = (YDEController.textColor & 0xFFFFFF) | alpha;
            YDEController.windowLineColor = (YDEController.windowLineColor & 0xFFFFFF) | alpha;
            YDEController.gridColor = (YDEController.gridColor & 0xFFFFFF) | alpha;
        }

        Packets.sendServer(new SPacketDialogCategoryGet());
    }

    @Override
    public void init() {
        if (minecraft == null) { minecraft = Minecraft.getInstance(); }
        if (hasSubGui()) { wrapper.subgui.init(minecraft, width, height); }
        // super init:
        guiScale = (float) minecraft.getWindow().getGuiScale();
        setFocused(null);
        renderables.clear();
        children().clear();
        wrapper.init(minecraft, width, height);
        notScaledComponents.clear();
        // this init
        if (category == null) {
            String name = "";
            List<IDialogCategory> cats = DialogController.instance.categories();
            if (!cats.isEmpty()) { name = cats.get(0).getName(); }
            category = YDE_DATA.getCategory(name);
        }
        category = YDE_DATA.checkCategory(category);

        leftTab.imageHeight = (int) (height * guiScale);
        rightTab.imageHeight = (int) (height * guiScale);
        GuiCustomScrollNop scroll = leftTab.getScroll(0);
        Component selectedCategory = null;
        if (scroll == null) { scroll = leftTab.addScroll(0); }
        List<Component> categories = new ArrayList<>();
        for (DialogCategory c : DialogController.instance.categories.values()) {
            Component key = Component.translatable(c.title);
            categories.add(key);
            if (c.title.equals(category.category)) { selectedCategory = key; }
        }
        scroll.setCustomFont(UtilYDE.FONT)
                .setPos(leftTab.getX() + 2, leftTab.getY() + 14)
                .setSize(leftTab.imageWidth - 4, leftTab.imageHeight / 2 - 34)
                .setNormalList(categories)
                .setSelectedIndex(selectedCategory);
        scroll.border = YDEController.windowLineColor;
        leftTab.addButton(0, 4, leftTab.imageHeight / 2 - 18, "gui.add")
                .setCustomFont(UtilYDE.FONT)
                .setTexture(ANIMATION_BUTTONS)
                .setIsAnim(true)
                .setUV(0, 96, 200, 20)
                .setColor(CustomNpcs.LableColor.getRGB())
                .setSize(40, 16);
        leftTab.addButton(1, 46, leftTab.imageHeight / 2 - 18, "gui.remove")
                .setCustomFont(UtilYDE.FONT)
                .setTexture(ANIMATION_BUTTONS)
                .setIsAnim(true)
                .setUV(0, 96, 200, 20)
                .setColor(CustomNpcs.LableColor.getRGB())
                .setSize(40, 16)
                .setIsEnabled(scroll.hasSelected());
        leftTab.addButton(2, 88, leftTab.imageHeight / 2 - 18, "gui.edit")
                .setCustomFont(UtilYDE.FONT)
                .setTexture(ANIMATION_BUTTONS)
                .setIsAnim(true)
                .setUV(0, 96, 200, 20)
                .setColor(CustomNpcs.LableColor.getRGB())
                .setSize(70, 16)
                .setIsEnabled(scroll.hasSelected());
        if (helper == null) { helper = addScroll(25).setIsVisible(false); }
        helper.setCustomFont(UtilYDE.FONT)
                .setIsSimpleSelect(true)
                .setHoverScale(guiScale, 1.0f)
                .disabledSearch();
        helper.border = YDEController.windowLineColor;
        add(helper);
        // category name
        YDE_DATA.check();
        // not scaled components
        GuiLabel label = addLabel(0, (int) (((width - UtilYDE.FONT_HEADLINE.width(category.title)) * guiScale) / 4.0f), 0, category.title)
                .setCustomFont(UtilYDE.FONT_HEADLINE)
                .setColor(color);
        notScaledComponents.add(label);
        GuiTextFieldNop textField = getTextField(0);
        boolean bo = textField != null && textField.isVisible();
        textField = addTextField(0, label.getX(), label.getY(), label.getWidth(), 20, category.category)
                .setIsVisible(bo);
        notScaledComponents.add(textField);
        notScaledComponents.add(leftTab);
        notScaledComponents.add(rightTab);
        notScaledComponents.add(helper);
        w = width * guiScale / 2.0f;
        h = height * guiScale / 2.0f;
        List<Integer> sls = new ArrayList<>(selects);
        selects.clear();
        for (YDENode node : YDE_DATA.nodes.values()) {
            if (!(node instanceof YDECategory) && node.category.equals(category.category)) {
                if (node instanceof YDEArea area) { add(new YDEAreaNop(this, area)); }
                else { add(new YDEWindowNop(this, node)); }
                if (sls.contains(node.id)) { addSelect(node.id); }
            }
        }
    }

    @Override
    public void mouseButtonEvent(GuiButtonNop button, int mouseButton) {
        if (!hasSubGui()) { mouseButtonEvent(null, button, mouseButton); }
    }

    public boolean mouseButtonEvent(YDEWindowNop window, @Nullable GuiButtonNop button, int mouseButton) {
        LogWriter.info("TEST: buttonID: "+(button == null ? "null" : button.id)+"; mouseButton: "+mouseButton+"; window "+window);
        if (mouseButton == 0 && button != null) {
            switch (button.id) {
                case 0: {
                    setSubGui(new SubGuiEditText(0, Component.translatable("gui.new").getString()));
                    return true;
                } // create dialog category
                case 1: {
                    ConfirmScreen guiYesNo = new ConfirmScreen((agree) -> {
                        if (agree) {
                            int catId = category.categoryId;
                            YDE_DATA.nodes.remove(category.id);
                            category = null;
                            leftTab.getScroll(0).setSelectedIndex("");
                            Packets.sendServer(new SPacketDialogCategoryRemove(catId));
                        }
                        NoppesUtil.openGUI(player, this);
                    },
                            category.title,
                            Component.translatable("message.delete"));
                    setScreen(guiYesNo);
                    return true;
                } // remove dialog category
                case 2: {
                    scrollDoubleClicked(leftTab.getScroll(0));
                    return true;
                } // rename dialog category
                case 2500: {
                    if (window == null) {
                        if (button.equals(leftTab.exit)) { leftTab.isYDEShow = !leftTab.isYDEShow; }
                        else { rightTab.isYDEShow = !rightTab.isYDEShow; }
                    } // show / hide tabs
                    else {
                        ConfirmScreen guiYesNo = new ConfirmScreen((agree) -> {
                            NoppesUtil.openGUI(player, this);
                            if (agree) {
                                for (YDELink link : window.node.links) {
                                    if (thereAreNoOthersBesides(window.node, link)) { YDE_DATA.nodes.remove(link.nextNodId); }
                                }
                                YDE_DATA.nodes.remove(window.node.id);
                                if (window.node instanceof YDEDialog yde_dialog) { Packets.sendServer(new SPacketDialogRemove(yde_dialog.dialogId)); }
                                selects.clear();
                            }
                        },
                                switch (window.node.type) {
                                    case CATEGORY -> Component.translatable("yde.delete.category", window.node.id, window.node.category);
                                    case DIALOG -> Component.translatable("yde.delete.dialog", ((YDEDialog) window.node).dialogId, ((YDEDialog) window.node).dialog.title);
                                    case NPC -> Component.translatable("yde.delete.npc", ((YDENpc) window.node).npcData.name, Component.translatable("menu.advanced").getString());
                                    case OPTION -> Component.translatable("yde.delete.option", ((YDEOption) window.node).option.slot);
                                    case QUEST -> Component.translatable("yde.delete.quest", ((YDEQuest) window.node).questId);
                                    case AREA -> Component.translatable("yde.delete.area", window.node.title.getString());
                                },
                                Component.translatable("message.delete"));
                        setScreen(guiYesNo);
                    } // remove node / exit window
                    return true;
                } // exit
                case 2501: {
                    window.isLock = !window.isLock;
                    if (window.isLock) {
                        button.txrX += button.txrW;
                        window.lock.layerColor = new Color(0xFFA0A000).getRGB();
                    }
                    else {
                        button.txrX -= button.txrW;
                        window.lock.layerColor = new Color(0xFFFFFF00).getRGB();
                    }
                    return true;
                } // lock node / window
            }
        }
        return false;
    }

    private static boolean thereAreNoOthersBesides(YDENode node, YDELink link) {
        for (YDENode n : YDE_DATA.nodes.values()) {
            if (n != node) {
                for (YDELink l : n.links) {
                    if (l.nextNodId == link.nextNodId) {
                        return false;
                    }
                }
            }
        }
        return true;
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
        renderSelectedBorder(graphics);
        matrixStack.popPose();
        // labels
        matrixStack.pushPose();
        matrixStack.translate(leftTab.getX() + leftTab.imageWidth + 8.0f, 0.0f, 0.0f);
        // grid offset
        String label = "U: " + -category.x + "; V: " + -category.y;
        UtilYDE.FONT.draw(graphics, label, 0.0f, 0.0f, color);
        // scale
        label = "x" + df2.format(category.getScale());
        UtilYDE.FONT.draw(graphics, label, 0.0f, UtilYDE.FONT.getHeight(), color);
        // mouse pos hover
        mouseOnGrid = hovered == null && !hoverLeft && !hoverRight;
        if (mouseOnGrid) {
            for (IComponentGui component : new ArrayList<>(wrapper.components)) {
                if ((component instanceof AbstractWidget element && element.isHovered()) ||
                        (component instanceof GuiCustomScrollNop scroll && scroll.isHovered()) ||
                        (component instanceof GuiCustomWindowNop window && window.isHovered())) {
                    mouseOnGrid = false;
                    break;
                }
            }
        }
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

    private void renderSelectedBorder(@Nonnull GuiGraphics graphics) {
        VertexConsumer consumer = graphics.bufferSource().getBuffer(RenderType.gui());
        Matrix4f matrix = graphics.pose().last().pose();
        int x;
        int y;
        int w;
        int h;
        float r, g, b;
        boolean isSelect = true;
        for (int select : new ArrayList<>(selects)) {
            IComponentGui sel = get(select);
            if (sel instanceof YDEWindowNop win) {
                x = win.getX();
                y = win.getY();
                w = win.imageWidth;
                h = win.imageHeight;
            }
            else if (sel instanceof YDEAreaNop area) {
                x = area.getX();
                y = area.getY();
                w = area.getWidth();
                h = area.getHeight();
            }
            else { continue; }
            if (isSelect) {
                r = FastColor.ARGB32.red(YDEController.selectLineColor) / 255.0f;
                g = FastColor.ARGB32.green(YDEController.selectLineColor) / 255.0f;
                b = FastColor.ARGB32.blue(YDEController.selectLineColor) / 255.0f;
                isSelect = false;
            }
            else {
                r = FastColor.ARGB32.red(YDEController.hoverLineColor) / 255.0f;
                g = FastColor.ARGB32.green(YDEController.hoverLineColor) / 255.0f;
                b = FastColor.ARGB32.blue(YDEController.hoverLineColor) / 255.0f;
            }
            x = (int) (x * category.getScale() + centerU - 1.0f);
            y = (int) (y * category.getScale() + centerV - 1.0f);
            w = (int) (w * category.getScale() + 2.75f);
            h = (int) (h * category.getScale() + 1.75f);
            int s;
            int e;
            int step = 5;
            int u = (int) ((System.currentTimeMillis() % (step * 100L)) / (step * 10L)) - step;
            while (u < w) {
                s = x + ValueUtil.correctInt(u, 0, w);
                e = x + ValueUtil.correctInt(u + step, 0, w);
                consumer.vertex(matrix, s, y, 0.0f).color(r, g, b, 1.0f).endVertex();
                consumer.vertex(matrix, s, y + 0.5f, 0.0f).color(r, g, b, 1.0f).endVertex();
                consumer.vertex(matrix, e, y + 0.5f, 0.0f).color(r, g, b, 1.0f).endVertex();
                consumer.vertex(matrix, e, y, 0.0f).color(r, g, b, 1.0f).endVertex();
                u += 2 * step;
            }
            u = u - w - 2 * step;
            x += w;
            while (u < h) {
                s = y + ValueUtil.correctInt(u, 0, h);
                e = y + ValueUtil.correctInt(u + step, 0, h + 1);
                consumer.vertex(matrix, x, s, 0.0f).color(r, g, b, 1.0f).endVertex();
                consumer.vertex(matrix, x, e, 0.0f).color(r, g, b, 1.0f).endVertex();
                consumer.vertex(matrix, x + 0.5f, e, 0.0f).color(r, g, b, 1.0f).endVertex();
                consumer.vertex(matrix, x + 0.5f, s, 0.0f).color(r, g, b, 1.0f).endVertex();
                u += 2 * step;
            }
            u = -1 * (u - h) + step;
            y += h + 1;
            while (u > - w - step) {
                s = x + ValueUtil.correctInt(u, -w, 0);
                e = x + ValueUtil.correctInt(u + step, -w, 0);
                consumer.vertex(matrix, s + 0.5f, y, 0.0f).color(r, g, b, 1.0f).endVertex();
                consumer.vertex(matrix, s + 0.5f, y + 0.5f, 0.0f).color(r, g, b, 1.0f).endVertex();
                consumer.vertex(matrix, e + 0.5f, y + 0.5f, 0.0f).color(r, g, b, 1.0f).endVertex();
                consumer.vertex(matrix, e + 0.5f, y, 0.0f).color(r, g, b, 1.0f).endVertex();
                u -= 2 * step;
            }
            u += w + 2 * step;
            x -= w;
            while (u > - h - step) {
                s = y + ValueUtil.correctInt(u, - h, 0);
                e = y + ValueUtil.correctInt(u + step, -h, 0);
                consumer.vertex(matrix, x, s, 0.0f).color(r, g, b, 1.0f).endVertex();
                consumer.vertex(matrix, x, e, 0.0f).color(r, g, b, 1.0f).endVertex();
                consumer.vertex(matrix, x + 0.5f, e, 0.0f).color(r, g, b, 1.0f).endVertex();
                consumer.vertex(matrix, x + 0.5f, s, 0.0f).color(r, g, b, 1.0f).endVertex();
                u -= 2 * step;
            }
        }
        graphics.bufferSource().endBatch();
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (minecraft == null) { minecraft = Minecraft.getInstance(); }
        float scale = 2.0f / guiScale;
        xMouse = mouseX / scale;
        yMouse = mouseY / scale;
        RenderSystem.enableBlend();
        // tabs
        PoseStack matrixStack = graphics.pose();
        int tabHeight = leftTab.imageHeight / 2;
        int arrowHeight = (int) (25.0f * guiScale);
        matrixStack.pushPose();
        matrixStack.scale(scale, scale, scale);
        // main
        wrapper.graphics = graphics;
        wrapper.mouseX = (int) (((hasSubGui() ? 0.0f : xMouse) - centerU) / category.getScale());
        wrapper.mouseY = (int) (((hasSubGui() ? 0.0f : yMouse) - centerV) / category.getScale());
        if (drawDefaultBackground) { renderBackground(graphics); }
        // super

        hovered = null;
            matrixStack.pushPose();
            GuiLabel label = getLabel(0);
            if (label != null) { label.render(graphics, mouseX, mouseY, partialTicks); }
            GuiTextFieldNop textField = getTextField(0);
            if (textField != null) { textField.render(graphics, mouseX, mouseY, partialTicks); }
            matrixStack.translate(centerU, centerV, 0.0f);
            matrixStack.scale(category.getScale(), category.getScale(), category.getScale());
            for (IComponentGui component : new ArrayList<>(wrapper.components)) {
                if (component instanceof Renderable renderable && !notScaledComponents.contains(component)) {
                    renderable.render(graphics, wrapper.mouseX, wrapper.mouseY, partialTicks);
                }
            }
            matrixStack.popPose();

            matrixStack.pushPose();
            if (hasSubGui()) {
                matrixStack.translate(0.0F, 0.0F, 60.0F);
                wrapper.subgui.render(graphics, mouseX, mouseY, partialTicks);
                matrixStack.translate(0.0F, 0.0F, -60.0F);
            }
            else {
                matrixStack.pushPose();
                matrixStack.translate(0.0f, 0.0f, 0.0f);
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
                matrixStack.popPose();
            }
            matrixStack.popPose();

            matrixStack.translate(0.0f, 0.0f, 500.0f);
            for (IComponentGui component : new ArrayList<>(notScaledComponents)) {
                if (component instanceof Renderable renderable) {
                    if (component == helper) { matrixStack.translate(0.0f, 0.0f, 1.0f); }
                    renderable.render(graphics, (int) xMouse, (int) yMouse, partialTicks);
                    if (component == helper) { matrixStack.translate(0.0f, 0.0f, -1.0f); }
                }
            }

        matrixStack.popPose();

        if ((hoverIsGame || (CustomNpcs.ShowDescriptions && GuiBasic.showHoverText)) && !hoverText.isEmpty()) {
            if (!hoverIsGame) { hoverText.add(Component.translatable("hover.alt.h")); }
            RenderSystem.disableDepthTest();
            if (hoverFont == null) { GuiTooltipUtils.renderTooltip(graphics, font, hoverText, Optional.empty(), mouseX, ValueUtil.correctInt(mouseY, 16, height)); }
            else { renderTooltipInternal(graphics, mouseX, ValueUtil.correctInt(mouseY, 16, height), hoverFont, hoverText, -0.25f * guiScale + 1.5f); }
            hoverText.clear();
        }

        if (rightTab.isHeadHovered() && rightTab.isYDEShow) {
            if (!helper.isVisible() || helper.type != 1) {
                helper.type = 1;
                Component h0 = Component.translatable("yde.help.multiple");
                Component h1 = Component.translatable("yde.help.sel.next");
                Component h2 = Component.translatable("yde.help.step.offset");
                Component h3 = Component.translatable("yde.help.hover.info.0");
                Component h4 = Component.translatable("yde.help.hover.info.1");
                Component h5 = Component.translatable("yde.help.extra");

                Component s0 = Component.literal("Ctrl").withStyle(ChatFormatting.AQUA)
                        .append(Component.literal("+").withStyle(ChatFormatting.WHITE))
                        .append(Component.translatable("yde.lmb").withStyle(ChatFormatting.YELLOW));
                Component s1 = Component.translatable("yde.help.double").append(" ")
                        .append(Component.translatable("yde.lmb").withStyle(ChatFormatting.YELLOW));
                Component s2 = Component.translatable("yde.help.hold").append(" ")
                        .append(Component.literal("Shift").withStyle(ChatFormatting.AQUA));
                Component s3 = Component.translatable("yde.rmb").withStyle(ChatFormatting.YELLOW);

                int w = ValueUtil.max(UtilYDE.FONT.width(h0) + UtilYDE.FONT.width(s0) + space,
                        UtilYDE.FONT.width(h1) + UtilYDE.FONT.width(s1) + space,
                        UtilYDE.FONT.width(h2) + UtilYDE.FONT.width(s2) + space,
                        UtilYDE.FONT.width(h5) + UtilYDE.FONT.width(s3) + space,
                        UtilYDE.FONT.width(h3), UtilYDE.FONT.width(h4));
                StringBuilder ig = new StringBuilder("―");
                while (UtilYDE.FONT.width(ig.toString()) < w) { ig.append("―"); }
                w = UtilYDE.FONT.width(ig.toString());
                Component ignore = Component.literal(ig.toString()).withStyle(ChatFormatting.GRAY);
                List<Component> list = Lists.newArrayList(h0, h1, h2, h5, ignore, h3, h4);
                helper.setPos((int) (xMouse - w - 5), (int) (yMouse + 5))
                        .setSize(w + 2, (UtilYDE.FONT.getHeight() + 4) * list.size() + 4)
                        .setIsEnabled(false)
                        .setIsVisible(true)
                        .setUnsortedList(list)
                        .setSuffixes(Lists.newArrayList(s0, s1, s2, s3, Component.empty(),
                                Component.empty(), Component.empty()))
                        .setSelectedIndex(-1);
            }
        }
        else {
            helper.setIsVisible(helper.isVisible() &&
                    xMouse >= helper.getX() - 10 && xMouse <= helper.getX() + helper.getWidth() + 10 &&
                    yMouse >= helper.getY() - 10 && yMouse <= helper.getY() + helper.getHeight() + 10);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrolled) {
        if (!hasSubGui()) {
            boolean bo = false;
            for (IComponentGui component : new ArrayList<>(notScaledComponents)) {
                if (component instanceof GuiEventListener element && element.mouseScrolled(mouseX, mouseY, scrolled)) { bo = true; }
            }
            if (!bo) { bo = wrapper.mouseScrolled(xMouse, yMouse, scrolled); }
            if (!bo) {
                float oldScale = category.getScale();
                float f0 = category.getScale() * (scrolled < 0.0f ? 0.1f : -0.1f);
                float newScale = ValueUtil.correctFloat(oldScale + f0, 0.1f, 1.0f);
                if (newScale != oldScale) {
                    float mouseGridX = (xMouse - centerU) / oldScale;
                    float mouseGridY = (yMouse - centerV) / oldScale;
                    float newCenterU = xMouse - mouseGridX * newScale;
                    float newCenterV = yMouse - mouseGridY * newScale;
                    category.x = (int) (newCenterU - w / 2.0f);
                    category.y = (int) (newCenterV - h / 2.0f);
                    category.setScale(newScale);
                }
                bo = true;
            }
            return bo;
        }
        return super.mouseScrolled(mouseX, mouseY, scrolled);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (!hasSubGui()) {
            if (mouseButton == 1 && mouseOnGrid) { showExtraMenu(); }
            if (hoverLeft) {
                mouseButtonEvent(leftTab.exit, 0);
                return true;
            } else if (hoverRight) {
                mouseButtonEvent(rightTab.exit, 0);
                return true;
            }
            boolean bo = false;
            for (IComponentGui component : new ArrayList<>(notScaledComponents)) {
                if (component instanceof GuiEventListener element && element.mouseClicked(mouseX, mouseY, mouseButton)) { bo = true; }
            }
            return bo || wrapper.mouseClicked(wrapper.mouseX, wrapper.mouseY, mouseButton);
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy) {
        if (!hasSubGui()) {
            boolean bo = false;
            for (IComponentGui component : new ArrayList<>(notScaledComponents)) {
                if (component instanceof GuiEventListener element && element.mouseDragged(mouseX, mouseY, mouseButton, dx, dy)) { bo = true; }
            }
            if (!bo) { bo =  wrapper.mouseDragged(xMouse, yMouse, mouseButton, dx, dy); }
            if (mouseOnGrid && !bo) {
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
        return super.mouseDragged(mouseX, mouseY, mouseButton, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (!hasSubGui()) {
            boolean bo = false;
            for (IComponentGui component : new ArrayList<>(notScaledComponents)) {
                if (component instanceof GuiEventListener element && element.mouseReleased(mouseX, mouseY, mouseButton)) { bo = true; }
            }
            return bo || wrapper.mouseReleased(xMouse, yMouse, mouseButton);
        }
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!hasSubGui() && !GuiBasic.isEscKey(keyCode)) {
            for (IComponentGui component : new ArrayList<>(notScaledComponents)) {
                if (component instanceof GuiEventListener element && element.keyPressed(keyCode, scanCode, modifiers)) { return true; }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char c, int keyId) {
        for (IComponentGui component : new ArrayList<>(notScaledComponents)) {
            if (component instanceof GuiEventListener element && element.charTyped(c, keyId)) { return true; }
        }
        return super.charTyped(c, keyId);
    }

    @Override
    public void onClose() {
        IComponentGui sel = getSelect();
        if (sel != null) { unFocused(sel); }
        super.onClose();
        NoppesUtil.requestOpenGUI(EnumGuiType.MainMenuGlobal);
    }

    @Override
    public void save() { YDEController.getInstance().save(); }

    @Override
    public void scrollClicked(GuiCustomScrollNop scroll) {
        if (scroll.id == 0) {
            if (!category.category.equals(scroll.getSelected())) {
                category = YDE_DATA.getCategory(scroll.getSelected());
                init();
            }
        } // select dialog category
    }

    @Override
    public void scrollDoubleClicked(GuiCustomScrollNop scroll) {
        if (scroll.id == 0) {
            setSubGui(new SubGuiEditText(1, category.category));
        } // rename dialog category
        if (scroll == helper) {
            helper.setIsVisible(false);
            switch (helper.getSelectedIndex()) {
                case 0: {
                    DialogCategory cat = DialogController.instance.getCategory(category.category);
                    if (cat != null) {
                        Dialog dialog = new Dialog(cat);
                        StringBuilder t = new StringBuilder(Component.translatable("gui.new").getString());
                        boolean has = true;
                        while (has) {
                            has = false;
                            for (Dialog dia : dialog.category.dialogs.values()) {
                                if (dia.id != dialog.id && dia.title.equalsIgnoreCase(t.toString())) {
                                    has = true;
                                    break;
                                }
                            }
                            if (has) { t.append("_"); }
                        }
                        dialog.title = t.toString();
                        YDEDialog yde_dialog = YDE_DATA.createDialog(dialog);
                        yde_dialog.x = pos[0];
                        yde_dialog.y = pos[1];
                        setActive(yde_dialog.id);
                        Packets.sendServer(new SPacketDialogSave(category.categoryId, dialog.save(new CompoundTag())));
                    }
                    break;
                } // add new dialog
                default: break;
            }
        }
    }

    @Override
    public void subGuiClosed(Screen subgui) {
        if (subgui instanceof SubGuiEditText gui && !gui.cancelled) {
            if (gui.id == 0) {
                DialogCategory cat = new DialogCategory();
                StringBuilder t = new StringBuilder(gui.text[0]);
                boolean has = true;
                while (has) {
                    has = false;
                    for (DialogCategory c : DialogController.instance.categories.values()) {
                        if (category.categoryId != c.id && c.title.equalsIgnoreCase(t.toString())) {
                            has = true;
                            break;
                        }
                    }
                    if (has) { t.append("_"); }
                }
                cat.title = t.toString();
                category = YDE_DATA.getCategory(cat.title);
                Packets.sendServer(new SPacketDialogCategorySave(cat.save(new CompoundTag())));
            } // create dialog category
            if (gui.id == 1) {
                DialogCategory cat = DialogController.instance.getCategory(category.category);
                if (cat != null && !cat.title.equals(gui.text[0])) {
                    cat.title = gui.text[0];
                    StringBuilder t = new StringBuilder(gui.text[0]);
                    boolean has = true;
                    while (has) {
                        has = false;
                        for (DialogCategory c : DialogController.instance.categories.values()) {
                            if (category.id != c.id && c.title.equalsIgnoreCase(t.toString())) {
                                has = true;
                                break;
                            }
                        }
                        if (has) { t.append("_"); }
                    }
                    cat.title = t.toString();
                    category.category = cat.title;
                    category.title = Component.empty();
                    if (category.id > -1) {
                        ((MutableComponent) category.title).append(Component.translatable("drop.category").withStyle(ChatFormatting.GRAY))
                                .append(Component.literal(" ID:" + cat.id + " ").withStyle(ChatFormatting.GRAY))
                                .append(Component.translatable(cat.title).withStyle(ChatFormatting.RESET));
                    }
                    Packets.sendServer(new SPacketDialogCategorySave(cat.save(new CompoundTag())));
                    init();
                }
            } // rename dialog category
        }
    }

    public void movedSelectNodes(int addX, int addY) {
        for (int id : selects) {
            if (get(id) instanceof YDEWindowNop wNop) {
                wNop.setIsFocused(true);
                wNop.moveTo(addX, addY);
            }
        }
    }

    @Override
    public boolean doubleClicked(IComponentGui component) {
        if (component instanceof YDEWindowNop windowNop) {
            for (IComponentGui c : wrapper.components) {
                if (c instanceof YDEWindowNop wNop) { wNop.setIsFocused(false); }
            }
            CustomNPCsScheduler.runTack(() -> selectLinks(windowNop), 100);
            return true;
        }
        if (component instanceof GuiLabel label && label.id == 0) {
            GuiTextFieldNop textField = getTextField(0);
            if (textField != null) {
                label.setIsVisible(false);
                textField.setIsVisible(true)
                        .setIsFocused(true);
                CustomNPCsScheduler.runTack(() ->
                        textField.mouseClicked(textField.getX() + 1, textField.getY() + 1, 0),
                        100);
            }
            return true;
        }
        return false;
    }

    @Override
    public void unFocused(GuiTextFieldNop textField) {
        if (textField.id == 0) {
            if (!textField.getValue().isEmpty()) {
                DialogCategory cat = DialogController.instance.getCategory(category.category);
                if (cat != null && !cat.title.equals(textField.getValue())) {
                    cat.title = textField.getValue();
                    StringBuilder t = new StringBuilder(textField.getValue());
                    boolean has = true;
                    while (has) {
                        has = false;
                        for (DialogCategory c : DialogController.instance.categories.values()) {
                            if (cat.id != c.id && c.title.equalsIgnoreCase(t.toString())) {
                                has = true;
                                break;
                            }
                        }
                        if (has) { t.append("_"); }
                    }
                    cat.title = t.toString();
                    category.category = cat.title;
                    category.title = Component.empty();
                    if (category.id > -1) {
                        ((MutableComponent) category.title).append(Component.translatable("drop.category").withStyle(ChatFormatting.GRAY))
                                .append(Component.literal(" ID:" + cat.id + " ").withStyle(ChatFormatting.GRAY))
                                .append(Component.translatable(cat.title).withStyle(ChatFormatting.RESET));
                    }
                    Packets.sendServer(new SPacketDialogCategorySave(cat.save(new CompoundTag())));
                    init();
                }
            }
        }
    }

    private void showExtraMenu() {
        if (!helper.isVisible() || helper.type != 2) {
            helper.type = 2;
            Component h0 = Component.translatable("yde.extra.add.dialog");
            Component h1 = Component.translatable("yde.extra.add.option");
            Component h2 = Component.translatable("yde.extra.reset.grid");

            Component s0 = Component.literal("Shift").withStyle(ChatFormatting.AQUA)
                    .append(Component.literal("+").withStyle(ChatFormatting.WHITE)
                            .append(Component.literal("D").withStyle(ChatFormatting.GOLD)));
            Component s1 = Component.literal("Shift").withStyle(ChatFormatting.AQUA)
                    .append(Component.literal("+").withStyle(ChatFormatting.WHITE)
                            .append(Component.literal("E").withStyle(ChatFormatting.GOLD)));
            Component s2 = Component.literal("Shift").withStyle(ChatFormatting.AQUA)
                    .append(Component.literal("+").withStyle(ChatFormatting.WHITE)
                            .append(Component.literal("R").withStyle(ChatFormatting.GOLD)));

            int w0 = ValueUtil.max(UtilYDE.FONT.width(h0) + UtilYDE.FONT.width(s0) + space,
                    UtilYDE.FONT.width(h1) + UtilYDE.FONT.width(s1) + space,
                    UtilYDE.FONT.width(h2) + UtilYDE.FONT.width(s2) + space);
            StringBuilder ig = new StringBuilder("―");
            while (UtilYDE.FONT.width(ig.toString()) < w0) { ig.append("―"); }
            w0 = UtilYDE.FONT.width(ig.toString());
            Component ignore = Component.literal(ig.toString()).withStyle(ChatFormatting.GRAY);
            int x = (int) (xMouse); // (int) (xMouse * 2.0f / guiScale);
            int y = (int) (yMouse); // (int) (yMouse * 2.0f / guiScale);

            if (x + w0 > (width * guiScale / 2.0f)) { x = (int) xMouse - w0 - 2; }
            pos[0] = (int) ((xMouse - w / 2.0f - category.x) / category.getScale());
            pos[1] = (int) ((yMouse - h / 2.0f - category.y) / category.getScale());

            LinkedHashMap<Integer, List<Component>> htm = new LinkedHashMap<>();
            Component posC = Component.translatable("yde.hover.extra.pos");
            List<Component> l0 = new ArrayList<>();
            Util.instance.putHovers(l0, Component.translatable("yde.hover.extra.add.dialog"), posC);
            htm.put(0, l0);
            List<Component> l1 = new ArrayList<>();
            Util.instance.putHovers(l1, Component.translatable("yde.hover.extra.add.option"), posC);
            htm.put(1, l1);
            htm.put(2, new ArrayList<>());
            List<Component> l3 = new ArrayList<>();
            Util.instance.putHovers(l3, Component.translatable("yde.hover.extra.reset.grid"));
            htm.put(3, l3);

            List<Component> list = Lists.newArrayList(h0, h1, ignore, h2);
            helper.setPos(x, y)
                    .setSize(w0 + 2, (UtilYDE.FONT.getHeight() + 4) * list.size() + 4)
                    .setIsEnabled(true)
                    .setIsVisible(true)
                    .setUnsortedList(list)
                    .setSuffixes(Lists.newArrayList(s0, s1, Component.empty(), s2))
                    .setHoverTexts(htm)
                    .setIgnoreSelected(Lists.newArrayList(ignore))
                    .setSelectedIndex(-1);
        }
    }

    public void selectLinks(@Nullable YDEWindowNop windowNop) {
        if (windowNop != null) {
            windowNop.setIsFocused(true);
            for (YDELink link : windowNop.node.links) {
                YDEWindowNop window = get(link.nextNodId, YDEWindowNop.class);
                if (window != null) {
                    addSelect(window.id);
                    selectLinks(window);
                }
            }
        }
    }

    private IComponentGui getSelect() {
        for (int id : selects) {
            IComponentGui sel = get(id);
            if (sel instanceof YDEWindowNop || sel instanceof YDEAreaNop) { return sel; }
        }
        return null;
    }

    public void setActive(int id) {
        IComponentGui sel = getSelect();
        if (sel != null && sel.getId() != id) { unFocused(sel); }
        selects.clear();
        addSelect(id);
    }

    public void addSelect(int id) { selects.add(id); }

    public void removeSelect(int id) { selects.remove(id); }

    public boolean hasSelect(int id) { return selects.contains(id); }

    public void unFocused(IComponentGui component) {
        DialogController dData = DialogController.instance;
        if (component instanceof YDEWindowNop window) {
            Dialog dialog = null;
            if (window.node instanceof YDEDialog yde_dialog && dData.hasDialog(yde_dialog.dialog.id)) { dialog = yde_dialog.dialog; }
            if (window.node instanceof YDEOption yde_option && dData.hasDialog(yde_option.dialog.id)) { dialog = yde_option.dialog; }
            if (dialog != null) { Packets.sendServer(new SPacketDialogSave(dialog.category.id, dialog.save(new CompoundTag()))); }
        }
    }

    public void unFocused(YDEWindowNop window, GuiTextFieldNop textField) {
        if (window.node instanceof YDEDialog yde_dialog) {
            yde_dialog.dialog.title = textField.getValue();
        }
    }

    public void textUpdate(YDEWindowNop window, IComponentGui component, String text) {
        if (window.node instanceof YDEDialog yde_dialog) {
            if (component instanceof GuiTextFieldNop) { yde_dialog.dialog.title = text; } // name
            if (component instanceof GuiTextArea) { yde_dialog.dialog.text = text; } // text
        }
    }

}
