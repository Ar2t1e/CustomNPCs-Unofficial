package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.controllers.YDEController;
import noppes.npcs.client.gui.yellow_de.GuiYellowDialogEditor;
import noppes.npcs.client.gui.yellow_de.data.EnumYDEType;
import noppes.npcs.client.gui.yellow_de.data.UtilYDE;
import noppes.npcs.client.gui.yellow_de.data.YDELink;
import noppes.npcs.client.gui.yellow_de.data.YDENode;
import noppes.npcs.util.ValueUtil;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;

public class YDEWindowNop extends GuiCustomWindowNop {

    public final YDENode node;
    protected GuiButtonNop b0;
    protected GuiButtonNop b1;
    protected GuiButtonNop b2;
    protected GuiButtonNop b3;
    protected double tempDx;
    protected double tempDy;
    protected long lastClicked = 0L;
    public GuiYellowDialogEditor listener;

    public YDEWindowNop(GuiYellowDialogEditor gui, YDENode nodeIn) {
        super(gui, nodeIn.id, nodeIn.x, nodeIn.y, nodeIn.width, nodeIn.height, nodeIn.title);
        node = nodeIn;
        listener = gui;

        exit.setX(exit.getX() - 2);
        exit.setY(exit.getY() - 1);
        exit.setCustomFont(UtilYDE.FONT)
                .setColor(gui.color)
                .setHoverTexts("yde.node.exit");
        exit.isScissor = false;

        lock.setX(lock.getX() - 2);
        lock.setY(lock.getY() - 1);
        lock.setCustomFont(UtilYDE.FONT)
                .setColor(gui.color)
                .setHoverTexts("yde.node.lock");
        lock.isScissor = false;

        init();
    }

    @Override
    public void init() {
        if (node.type == EnumYDEType.DIALOG) {
            // -> options
            b0 = addButton(0, imageWidth - 4, imageHeight / 2 - 4, "")
                    .setSize(7, 7)
                    .setTexture(INFO)
                    .setDefBack(false)
                    .setIsAnim(true)
                    .setUV(0, 18, 14, 14)
                    .setHoverTexts("yed.hover.node.dialog.next.option");
            // <- back to options
            b1 = addButton(1, -4, imageHeight / 2 - 4, "")
                    .setSize(7, 7)
                    .setTexture(INFO)
                    .setDefBack(false)
                    .setIsAnim(true)
                    .setUV(0, 18, 14, 14)
                    .setHoverTexts("yed.hover.node.dialog.back.option");
            // -> npc
            b2 = addButton(2, -4, (int) (imageHeight - 4.0f), "")
                    .setSize(7, 7)
                    .setTexture(INFO)
                    .setDefBack(false)
                    .setIsAnim(true)
                    .setUV(0, 18, 14, 14)
                    .setHoverTexts("yed.hover.node.dialog.npc");
            // -> quest
            b3 = addButton(3, imageWidth / 2 - 4, imageHeight - 4, "")
                    .setSize(7, 7)
                    .setTexture(INFO)
                    .setDefBack(false)
                    .setIsAnim(true)
                    .setUV(0, 18, 14, 14)
                    .setHoverTexts("yed.hover.node.dialog.quest");
        }
        else if (node.type == EnumYDEType.OPTION) {
            // -> options
            b0 = addButton(0, imageWidth - 4, imageHeight / 2 - 4, "")
                    .setSize(7, 7)
                    .setTexture(INFO)
                    .setDefBack(false)
                    .setIsAnim(true)
                    .setUV(0, 18, 14, 14)
                    .setHoverTexts("yed.hover.node.option.next.dialog");
            // <- back to dialog
            b1 = addButton(1, -4, imageHeight / 2 - 4, "")
                    .setSize(7, 7)
                    .setTexture(INFO)
                    .setDefBack(false)
                    .setIsAnim(true)
                    .setUV(0, 18, 14, 14)
                    .setHoverTexts("yed.hover.node.option.back.dialog");
        }
        else if (node.type == EnumYDEType.QUEST) {
            // <- back to dialog
            b0 = addButton(0, imageWidth / 2 - 4, -4, "")
                    .setSize(7, 7)
                    .setTexture(INFO)
                    .setDefBack(false)
                    .setIsAnim(true)
                    .setUV(0, 18, 14, 14)
                    .setHoverTexts("yed.hover.node.quest.back.dialog");
        }
        else if (node.type == EnumYDEType.NPC) {
            // <- back to dialog
            b0 = addButton(0, imageWidth - 4, -4, "")
                    .setSize(7, 7)
                    .setTexture(INFO)
                    .setDefBack(false)
                    .setIsAnim(true)
                    .setUV(0, 18, 14, 14)
                    .setHoverTexts("yed.hover.node.npc.back.dialog");
        }
    }

    @Override
    public void renderBackground(@Nonnull GuiGraphics graphics) {
        PoseStack matrixStack = graphics.pose();
        if (!node.links.isEmpty()) {
            matrixStack.pushPose();
            // link dots
            for (YDELink link : node.links) {
                if (link.backNodeId == node.id) {
                    YDEWindowNop nextNode = listener.get(link.nextNodId, YDEWindowNop.class);
                    if (nextNode != null) {
                        if (link.type == EnumYDEType.OPTION) {
                            UtilYDE.renderSpline(graphics, new float[] { getX() + imageWidth, getY() + imageHeight / 2.0f },
                                    new float[] { nextNode.getX(), nextNode.getY() + nextNode.imageHeight / 2.0f },
                                    false, (getX() + imageWidth > nextNode.getX()), EnumYDEType.DIALOG.color);
                        }
                        else if (link.type == EnumYDEType.DIALOG) {
                            UtilYDE.renderSpline(graphics, new float[] { getX() + imageWidth, getY() + imageHeight / 2.0f },
                                    new float[] { nextNode.getX(), nextNode.getY() + nextNode.imageHeight / 2.0f },
                                    false, (getX() + imageWidth > nextNode.getX()), EnumYDEType.OPTION.color);
                        }
                        else if (link.type == EnumYDEType.NPC) {
                            UtilYDE.renderSpline(graphics, new float[] { getX(), getY() + imageHeight },
                                    new float[] { nextNode.getX() + nextNode.imageWidth, nextNode.getY() },
                                    false, false, EnumYDEType.NPC.color);
                        }
                        else if (link.type == EnumYDEType.QUEST) {
                            UtilYDE.renderSpline(graphics, new float[] { getX() + imageWidth / 2.0f, getY() + imageHeight },
                                    new float[] { nextNode.getX() + nextNode.imageWidth / 2.0f, nextNode.getY() },
                                    false, false, EnumYDEType.QUEST.color);
                        }
                    }
                }
            }
            matrixStack.popPose();
        }
        matrixStack.pushPose();
        matrixStack.translate(guiLeft, guiTop, -1.0f);
        matrixStack.pushPose();
        // background
        matrixStack.scale(0.5f * bgScale, 0.5f * bgScale, 0.5f * bgScale);
        int w = imageWidth * 2;
        int h = imageHeight * 2;
        int color = isEnabled() && (isHovered || focused) ? YDEController.backHoverColor : YDEController.backColor;
        graphics.fill(0, 0, w, h, color);
        color = isEnabled() && (isHovered || isFocused()) ? YDEController.backColor : YDEController.lineColor;
        graphics.hLine(1, w - 2, 1, color);
        graphics.vLine(1, 1, h - 2, color);
        graphics.vLine(w - 2, 1, h - 2, color);
        graphics.hLine(1, w - 2, h - 2, color);
        // dots
        if (node.type == EnumYDEType.DIALOG) {
            b0.layerColor = !isEnabled() ? EnumYDEType.DIALOG.disableColor : isHovered || focused ? EnumYDEType.DIALOG.hoverColor : EnumYDEType.DIALOG.color;
            b1.layerColor = !isEnabled() ? EnumYDEType.OPTION.disableColor : isHovered || focused ? EnumYDEType.OPTION.hoverColor : EnumYDEType.OPTION.color;
            b2.layerColor = !isEnabled() ? EnumYDEType.NPC.disableColor : isHovered || focused ? EnumYDEType.NPC.hoverColor : EnumYDEType.NPC.color;
            b3.layerColor = !isEnabled() ? EnumYDEType.QUEST.disableColor : isHovered || focused ? EnumYDEType.QUEST.hoverColor : EnumYDEType.QUEST.color;
        }
        else if (node.type == EnumYDEType.OPTION) {
            b0.layerColor = !isEnabled() ? EnumYDEType.OPTION.disableColor : isHovered || focused ? EnumYDEType.OPTION.hoverColor : EnumYDEType.OPTION.color;
            b1.layerColor = !isEnabled() ? EnumYDEType.DIALOG.disableColor : isHovered || focused ? EnumYDEType.DIALOG.hoverColor : EnumYDEType.DIALOG.color;
        }
        else if (node.type == EnumYDEType.QUEST) {
            b0.layerColor = !isEnabled() ? EnumYDEType.QUEST.disableColor : isHovered || focused ? EnumYDEType.QUEST.hoverColor : EnumYDEType.QUEST.color;
        }
        else if (node.type == EnumYDEType.NPC) {
            b0.layerColor = !isEnabled() ? EnumYDEType.NPC.disableColor : isHovered || focused ? EnumYDEType.NPC.hoverColor : EnumYDEType.NPC.color;
        }
        // head
        color = !isEnabled() ? node.type.disableColor : isHovered || focused ? node.type.hoverColor : node.type.color;
        matrixStack.translate(3.0f, 3.0f, 0.0f);
        float r = (color >> 16) / 255.0f;
        float g = (color >> 8) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float right = w - 6;
        VertexConsumer consumer = graphics.bufferSource().getBuffer(RenderType.gui());
        Matrix4f matrix = graphics.pose().last().pose();
        consumer.vertex(matrix, 6.0f, 0.0f, 0.0f).color(r, g, b, 1.0f).endVertex();
        consumer.vertex(matrix, 0.0f, 6.0f, 0.0f).color(r, g, b, 0.75f).endVertex();
        consumer.vertex(matrix, right, 6.0f, 0.0f).color(r, g, b, 0.75f).endVertex();
        consumer.vertex(matrix, right - 6.0f, 0.0f, 0.0f).color(r, g, b, 1.0f).endVertex();
        consumer.vertex(matrix, 0.0f, 6.0f, 0.0f).color(r, g, b, 0.75f).endVertex();
        consumer.vertex(matrix, 0.0f, 19.0f, 0.0f).color(r, g, b, 0.25f).endVertex();
        consumer.vertex(matrix, right, 19.0f, 0.0f).color(r, g, b, 0.25f).endVertex();
        consumer.vertex(matrix, right, 6.0f, 0.0f).color(r, g, b, 0.75f).endVertex();
        graphics.bufferSource().endBatch();
        matrixStack.popPose();
        // title
        if (title != null && !title.getString().isEmpty()) {
            UtilYDE.FONT.draw(graphics, title, 3, 2, CustomNpcs.MainColor.getRGB() | 255 << 24);
        }
        matrixStack.translate(-2, -2, 0.0f);
        if (listener.select == id) { renderSelectedBorder(graphics); }
        matrixStack.popPose();
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(0.0f, 0.0f, (float) id / 10000.0f);
        super.render(graphics, mouseX, mouseY, partialTicks);
        isHeadHovered = isHovered && isMouseHover(mouseX, mouseY, getX(), guiTop, imageWidth, 11);
        if (isHovered) { listener.hovered = this; }
        matrixStack.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (isHovered) {
            listener.setActive(this);
            if (lastClicked + 500L > System.currentTimeMillis()) { listener.doubleClicked(this); }
            else { lastClicked = System.currentTimeMillis(); }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (focused) {
            if (isHovered || !hasShiftDown()) { focused = false; }
        }
        else { focused = isHovered; }
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy) {
        boolean bo = wrapper.mouseDragged(mouseX, mouseY, mouseButton, dx, dy);
        if (isHovered && !bo) {
            if (!isLock) {
                tempDx += dx;
                tempDy += dy;
                int x = (int) (Math.floor(tempDx) * listener.guiScale / listener.category.getScale() / 2.0d);
                int y = (int) (Math.floor(tempDy) * listener.guiScale / listener.category.getScale() / 2.0d);
                if (x != 0 || y != 0) {
                    if (x != 0) { tempDx -= x / listener.guiScale * listener.category.getScale() * 2.0d; }
                    if (y != 0) { tempDy -= y / listener.guiScale * listener.category.getScale() * 2.0d; }
                    listener.movedSelectNodes(x, y);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void moveTo(int addX, int addY) {
        super.moveTo(addX, addY);
        node.x = guiLeft;
        node.y = guiTop;
    }

    private void renderSelectedBorder(GuiGraphics graphics) {
        VertexConsumer consumer = graphics.bufferSource().getBuffer(RenderType.gui());
        Matrix4f matrix = graphics.pose().last().pose();
        int x = (int) ((System.currentTimeMillis() % 500L) / 50L) - 10;
        int y = 0;
        int s;
        int e;
        int w = imageWidth + 4;
        int h = imageHeight + 4;
        while (x < w) {
            s = ValueUtil.correctInt(x, 0, w);
            e = ValueUtil.correctInt(x + 5, 0, w);
            consumer.vertex(matrix, s, y, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
            consumer.vertex(matrix, s, y + 1, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
            consumer.vertex(matrix, e, y + 1, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
            consumer.vertex(matrix, e, y, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
            x += 10;
        }
        x -= w + 10;
        y = w;
        while (x < h) {
            s = ValueUtil.correctInt(x, 0, h);
            e = ValueUtil.correctInt(x + 5, 0, h + 1);
            consumer.vertex(matrix, y, s, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
            consumer.vertex(matrix, y, e, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
            consumer.vertex(matrix, y + 1, e, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
            consumer.vertex(matrix, y + 1, s, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
            x += 10;
        }
        x -= h + 6;
        x *= -1;
        x += w;
        y = h;
        while (x > -10) {
            s = ValueUtil.correctInt(x, 0, w);
            e = ValueUtil.correctInt(x + 5, 0, w);
            consumer.vertex(matrix, s, y, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
            consumer.vertex(matrix, s, y + 1, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
            consumer.vertex(matrix, e, y + 1, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
            consumer.vertex(matrix, e, y, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
            x -= 10;
        }
        x += w - 10;
        y = 0;
        while (x > -10) {
            s = ValueUtil.correctInt(x, 0, h);
            e = ValueUtil.correctInt(x + 5, 0, h + 1);
            consumer.vertex(matrix, y, s, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
            consumer.vertex(matrix, y, e, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
            consumer.vertex(matrix, y + 1, e, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
            consumer.vertex(matrix, y + 1, s, 0.0f).color(1.0f, 1.0f, 1.0f, 1.0f).endVertex();
            x -= 10;
        }
        graphics.bufferSource().endBatch();
    }

}
