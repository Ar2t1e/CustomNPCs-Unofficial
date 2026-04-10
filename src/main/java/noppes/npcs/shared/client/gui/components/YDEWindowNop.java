package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.controllers.YDEController;
import noppes.npcs.client.gui.yellow_de.GuiYellowDialogEditor;
import noppes.npcs.client.gui.yellow_de.data.EnumYDEType;
import noppes.npcs.client.gui.yellow_de.data.UtilYDE;
import noppes.npcs.client.gui.yellow_de.data.YDELink;
import noppes.npcs.client.gui.yellow_de.data.YDENode;
import noppes.npcs.client.gui.yellow_de.data.nodes.YDEDialog;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.shared.client.gui.listeners.IComponentGui;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class YDEWindowNop extends GuiCustomWindowNop {

    protected static final ClientProxy.FontContainer AREA_FONT = new ClientProxy.FontContainer("JetBrainsMono", 12);

    public final YDENode node;
    public final GuiYellowDialogEditor listener;
    protected GuiButtonNop b0;
    protected GuiButtonNop b1;
    protected GuiButtonNop b2;
    protected GuiButtonNop b3;
    protected double tempDx;
    protected double tempDy;
    protected long lastClicked = 0L;

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
        int y;
        int w = imageWidth - 6;
        int h0 = UtilYDE.FONT.getHeight() + 2;
        if (node instanceof YDEDialog yde_dialog) {
            Dialog dialog = DialogController.instance.get(yde_dialog.dialogId);
            if (dialog == null) {
                dialog = new Dialog(DialogController.instance.getCategory(node.category));
            }
            addTextField(0, 3, 21, w, h0, dialog.title)
                    .setColor(YDEController.textColor)
                    .setCustomFont(UtilYDE.FONT);
            y = 32 + h0;
            GuiTextArea compArea;
            add(compArea = new GuiTextArea(0, guiLeft + 4, guiTop + y, w - 2, imageHeight - y - 4, dialog.text)
                    .setColor(YDEController.textColor)
                    .setCustomFont(AREA_FONT));
            compArea.isYDE = true;
            // link buttons
            // -> options
            b0 = addButton(0, imageWidth - 4, imageHeight / 2 - 4, "")
                    .setSize(7, 7)
                    .setTexture(INFO)
                    .setDefBack(false)
                    .setIsAnim(true)
                    .setUV(0, 18, 14, 14)
                    .setHoverTexts("yde.hover.node.dialog.next.option");
            // <- back to options
            b1 = addButton(1, -4, imageHeight / 2 - 4, "")
                    .setSize(7, 7)
                    .setTexture(INFO)
                    .setDefBack(false)
                    .setIsAnim(true)
                    .setUV(0, 18, 14, 14)
                    .setHoverTexts("yde.hover.node.dialog.back.option");
            // -> npc
            b2 = addButton(2, -4, (int) (imageHeight * 0.8f - 4.0f), "")
                    .setSize(7, 7)
                    .setTexture(INFO)
                    .setDefBack(false)
                    .setIsAnim(true)
                    .setUV(0, 18, 14, 14)
                    .setHoverTexts("yde.hover.node.dialog.npc");
            // -> quest
            b3 = addButton(3, imageWidth / 2 - 4, imageHeight - 4, "")
                    .setSize(7, 7)
                    .setTexture(INFO)
                    .setDefBack(false)
                    .setIsAnim(true)
                    .setUV(0, 18, 14, 14)
                    .setHoverTexts("yde.hover.node.dialog.quest");
        }
        else if (node.type == EnumYDEType.OPTION) {
            // -> options
            b0 = addButton(0, imageWidth - 4, imageHeight / 2 - 4, "")
                    .setSize(7, 7)
                    .setTexture(INFO)
                    .setDefBack(false)
                    .setIsAnim(true)
                    .setUV(0, 18, 14, 14)
                    .setHoverTexts("yde.hover.node.option.next.dialog");
            // <- back to dialog
            b1 = addButton(1, -4, imageHeight / 2 - 4, "")
                    .setSize(7, 7)
                    .setTexture(INFO)
                    .setDefBack(false)
                    .setIsAnim(true)
                    .setUV(0, 18, 14, 14)
                    .setHoverTexts("yde.hover.node.option.back.dialog");
        }
        else if (node.type == EnumYDEType.QUEST) {
            // <- back to dialog
            b0 = addButton(0, imageWidth / 2 - 4, -4, "")
                    .setSize(7, 7)
                    .setTexture(INFO)
                    .setDefBack(false)
                    .setIsAnim(true)
                    .setUV(0, 18, 14, 14)
                    .setHoverTexts("yde.hover.node.quest.back.dialog");
        }
        else if (node.type == EnumYDEType.NPC) {
            // <- back to dialog
            b0 = addButton(0, imageWidth - 4,  imageHeight / 2 - 4, "")
                    .setSize(7, 7)
                    .setTexture(INFO)
                    .setDefBack(false)
                    .setIsAnim(true)
                    .setUV(0, 18, 14, 14)
                    .setHoverTexts("yde.hover.node.npc.back.dialog");
        }
    }

    @Override
    public void renderBackground(@Nonnull GuiGraphics graphics) {
        PoseStack matrixStack = graphics.pose();
        // links
        if (!node.links.isEmpty()) {
            matrixStack.pushPose();
            float zDepth = (float) id / 10000.0f;
            matrixStack.translate(0, 0, zDepth - 0.001f);
            // link dots
            for (YDELink link : node.links) {
                if (link.backNodeId == node.id) {
                    YDEWindowNop nextNode = listener.get(link.nextNodId, YDEWindowNop.class);
                    if (nextNode != null) {
                        if (link.type == EnumYDEType.OPTION) {
                            UtilYDE.renderSpline(graphics, new float[] { getX() + imageWidth, getY() + imageHeight / 2.0f },
                                    new float[] { nextNode.getX(), nextNode.getY() + nextNode.imageHeight / 2.0f },
                                    false, getX() + imageWidth > nextNode.getX(),
                                    EnumYDEType.DIALOG.color, zDepth - 0.001f);
                        }
                        else if (link.type == EnumYDEType.DIALOG) {
                            UtilYDE.renderSpline(graphics, new float[] { getX() + imageWidth, getY() + imageHeight / 2.0f },
                                    new float[] { nextNode.getX(), nextNode.getY() + nextNode.imageHeight / 2.0f },
                                    false, getX() + imageWidth > nextNode.getX(), EnumYDEType.OPTION.color, zDepth - 0.001f);
                        }
                        else if (link.type == EnumYDEType.NPC) {
                            UtilYDE.renderSpline(graphics, new float[] { getX(), getY() + imageHeight * 0.8f },
                                    new float[] { nextNode.getX() + nextNode.imageWidth, nextNode.getY() + nextNode.imageHeight / 2.0f },
                                    false, getX() > nextNode.getX() + nextNode.imageWidth, EnumYDEType.NPC.color, zDepth - 0.001f);
                        }
                        else if (link.type == EnumYDEType.QUEST) {
                            UtilYDE.renderSpline(graphics, new float[] { getX() + imageWidth / 2.0f, getY() + imageHeight },
                                    new float[] { nextNode.getX() + nextNode.imageWidth / 2.0f, nextNode.getY() },
                                    false, getY() + imageHeight < nextNode.getY(), EnumYDEType.QUEST.color, zDepth - 0.001f);
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
        if (node.type == EnumYDEType.DIALOG) {
            // name
            UtilYDE.FONT.draw(graphics, Component.translatable("gui.name").append(":"), 3, 12, YDEController.textColor);
            // text
            UtilYDE.FONT.draw(graphics, Component.translatable("gui.text").append(":"), 3, UtilYDE.FONT.getHeight() + 24, YDEController.textColor);
        }
        matrixStack.translate(-2, -2, 0.0f);
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
            listener.setActive(node.id);
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
        boolean hover = isHovered;
        if (hover) {
            for (IComponentGui component : new ArrayList<>(wrapper.components)) {
                if (component instanceof AbstractWidget widget && widget.isHovered()) {
                    hover = false;
                    break;
                }
            }
        }
        if (hover && !bo) {
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

}
