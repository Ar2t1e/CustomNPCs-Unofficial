package noppes.npcs.shared.client.gui;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.gui.subgui.IPartSetting;
import noppes.npcs.client.gui.select.SubGuiColorSelector;
import noppes.npcs.client.gui.SubGuiPartSetting;
import noppes.npcs.client.gui.model.GuiCreationParts;
import noppes.npcs.client.layer.LayerParts;
import noppes.npcs.client.parts.*;
import noppes.npcs.constants.BodyPart;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCustomGuiParts;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCheckBoxNop;
import noppes.npcs.shared.common.util.NopVector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class GuiModelPart extends GuiBasic {

    public static PlayerModel<LivingEntity> biped;
    protected static final ResourceLocation resource = new ResourceLocation(CustomNpcs.MODID, "textures/gui/components.png");
    protected static final int SIZE = 53;

    protected final GuiCreationParts parent;
    public ModelData modelData;
    public ModelData renderData = new ModelData(null);
    protected final int id;
    public float xPos;
    public float y;
    public boolean isHovered;

    protected List<MpmPart> all = new ArrayList<>();
    protected MpmPart part;
    protected MpmPartData data;
    protected boolean selected = true;
    protected boolean disableButtons = false;

    public int zPos = 0;
    public boolean basic = false;

    public GuiModelPart(GuiCreationParts parentIn, int idIn, float xPosIn, MpmPart mpmPartIn) {
        super();
        parent = parentIn;
        id = idIn;

        if (minecraft == null) { minecraft = Minecraft.getInstance(); }
        biped = new PlayerModel<>(minecraft.getEntityModels().bakeLayer(ModelLayers.PLAYER), true);
        modelData = ((EntityCustomNpc) parent.npc).modelData;

        xPos = xPosIn + (idIn % 2) * 70.0f;
        part = mpmPartIn;

        all.add(part);
        for (Map.Entry<ResourceLocation, MpmPart> entry : MpmPartReader.PARTS.entrySet()) {
            if (entry.getValue().parentId != null && entry.getValue().parentId.equals(part.id)) {
                all.add(entry.getValue());
            }
        }
        for (MpmPart p : all) {
            data = modelData.mpmParts.stream().filter((mpmPartData) -> mpmPartData.partId.equals(p.id)).findFirst().orElse(null);
            if (data != null) {
                part = p;
                break;
            }
        }

        all = all.stream().sorted(Comparator.comparing((t) -> t.id)).collect(Collectors.toList());
        if (data == null) {
            if (!part.id.equals(ModelEyeData.RESOURCE) && !part.id.equals(ModelEyeData.RESOURCE_RIGHT) && !part.id.equals(ModelEyeData.RESOURCE_LEFT)) { data = new MpmPartData(); }
            else { data = new ModelEyeData(); }
            data.partId = part.id;
            data.usePlayerSkin = part.defaultUsePlayerSkins;
            selected = false;
        }
    }

    public GuiModelPart(GuiCreationParts parentIn, int idIn, float xPosIn, MpmPart mpmPartIn, boolean disableButtonsIn) {
        this(parentIn, idIn, xPosIn, mpmPartIn);
        disableButtons = disableButtonsIn;
    }

    @Override
    public void buttonEvent(GuiButtonNop button) {
        switch (button.id) {
            case 0: {
                if (!part.isEnabled || basic) { return; }
                selected = !selected;
                if (selected) { modelData.mpmParts.add(data); }
                else { modelData.mpmParts.removeIf((t) -> t.partId.equals(data.partId)); }
                modelData.refreshParts();
                save();
                break;
            } // selected
            case 1: {
                parent.setSubGui(new SubGuiColorSelector(data.getColor(), new SubGuiColorSelector.ColorCallback() {
                    @Override
                    public void color(int colorIn) {
                        data.setColor(colorIn);
                        save();
                    }
                    @Override
                    public void preColor(int colorIn) { data.setColor(colorIn); }
                }));
                break;
            } // color
            case 2: {
                parent.setSubGui(new SubGuiPartSetting(parent, data, part, (subGuiPart) -> save()));
                break;
            } // settings
            case 3: {
                part = all.get(button.getValue());
                data.partId = part.id;
                break;
            } // variant
        }
        parent.buttonEvent(button);
    }

    public void render(float yOffset, int hoverID, GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        y = -yOffset + (float) Math.floor((float) id / 2.0f) * 70.0f + 7.0f;
        if (y < -67 || y > height - 5) { return; }
        isHovered = hoverID == id;
        customRender(graphics, mouseX, mouseY, partialTicks);
    }

    public void customRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(xPos, y, 0.0f);
        // background
        graphics.fill(0, 0, 68, 68, 0x80000000);
        graphics.fill(1, 1, 67, 67, 0xFFC0C0C0);
        matrixStack.pushPose();
        graphics.enableScissor((int) xPos + 1, (int) y + 1, (int) xPos + 67, (int) y + 67);
        if (!parent.hasSubGui()) { renderModel(graphics); }
        else if (parent.getSubGui() instanceof IPartSetting && disableButtons) { renderModel(graphics); }
        int x = 54;
        if (getButton(0) instanceof GuiCheckBoxNop checkBox && checkBox.selected()) {
            graphics.vLine(x, 0, 40, 0x80000000);
            graphics.hLine(x + 1, x + 12, 26, 0x80000000);
            graphics.hLine(x + 1, x + 12, 39, 0x80000000);
        }
        else if (!disableButtons) { graphics.vLine(x, 0, 14, 0x80000000); }
        if (!disableButtons) { graphics.hLine(x + 1, x + 12, 13, 0x80000000); }
        for (GuiButtonNop button : wrapper.getComponents(GuiButtonNop.class)) { button.offsetHover((int) xPos, (int) y); }
        if (isHovered && !parent.hasSubGui()) {
            List<Component> text = new ArrayList<>();
            text.add(Component.translatable("gui.name")
                    .append(Component.literal(": "))
                    .append(Component.translatable(part.name)));
            text.add(Component.translatable("message.madeby", part.author));
            if (!part.isEnabled) {
                text.add(Component.translatable("gui.disabled"));
            }
            setHoverText(text);
        }
        matrixStack.popPose();
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.disableScissor();
        matrixStack.popPose();
    }

    @Override
    public void init() {
        super.init();
        if (disableButtons) { return; }
        int x = 55;
        int y = 1;
        addCheckBox(0, x, y, Component.empty(), null, selected)
                .setSize(12, 12);
        addButton(1, x, y += 13, Component.empty())
                .setSize(12, 12)
                .setHoverTexts("hover.set.color")
                .setTexture(resource)
                .setUV(98, 0, 24, 24);
        addButton(2, x, y += 13, Component.empty())
                .setSize(12, 12)
                .setHoverTexts("gui.settings")
                .setTexture(resource)
                .setUV(144, 0, 21, 21);
        if (all.size() > 1) {
            Object[] variants = new Object[all.size()];
            for (int i = 0; i < all.size(); i++) { variants[i] = i + 1; }
            addButton(3, x - 24, y + 27, true, 0, variants)
                    .setSize(36, 12)
                    .setHoverTexts("part.hover.variant");
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (isHovered) { return super.mouseClicked(mouseX - xPos, mouseY - y, mouseButton); }
        return false;
    }

    @Override
    public void setHoverText(@Nullable List<Component> hoverText) { parent.setHoverText(hoverText); }

    @Override
    public void save() {
        Packets.sendServer(new SPacketCustomGuiParts(modelData.save()));
    }

    private void renderModel(GuiGraphics graphics) {
        if (minecraft == null) { minecraft = Minecraft.getInstance(); }
        int lt = LightTexture.FULL_BRIGHT;
        int ot = OverlayTexture.NO_OVERLAY;
        renderData.mpmParts = modelData.mpmParts;
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(1.0D, 1.0D, 100.0D + (double) zPos);
        if (part.menu.equals("part.arms")) { matrixStack.translate(10.0D, 5.0D, 0.0D); }
        else if (part.menu.equals("part.body")) { matrixStack.translate(6.0D, 0.0D, 0.0D); }
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();

        matrixStack.pushPose();
        EntityRenderDispatcher entityRendererManager = minecraft.getEntityRenderDispatcher();
        entityRendererManager.setRenderShadow(false);
        MultiBufferSource.BufferSource iRenderTypeBuffer = minecraft.renderBuffers().bufferSource();
        VertexConsumer iVertex = iRenderTypeBuffer.getBuffer(RenderType.entityCutoutNoCull(parent.npc.textureLocation));

        Lighting.setupForFlatItems();
        RenderSystem.runAsFancy(() -> {
            biped.leftLeg.visible = !part.hiddenParts.contains(BodyPart.LEFT_LEG) && !part.hiddenParts.contains(BodyPart.LEGS);
            biped.leftPants.visible = biped.leftPants.visible && biped.leftLeg.visible;
            biped.rightLeg.visible = !part.hiddenParts.contains(BodyPart.RIGHT_LEG) && !part.hiddenParts.contains(BodyPart.LEGS);
            biped.rightPants.visible = biped.rightPants.visible && biped.rightLeg.visible;
            biped.leftArm.visible = !part.hiddenParts.contains(BodyPart.LEFT_ARM) && !part.hiddenParts.contains(BodyPart.ARMS);
            biped.leftSleeve.visible = biped.leftSleeve.visible && biped.leftArm.visible;
            biped.rightArm.visible = !part.hiddenParts.contains(BodyPart.RIGHT_ARM) && !part.hiddenParts.contains(BodyPart.ARMS);
            biped.rightSleeve.visible = biped.rightSleeve.visible && biped.rightArm.visible;
            biped.body.visible = !part.hiddenParts.contains(BodyPart.BODY);
            biped.jacket.visible = biped.jacket.visible && biped.body.visible;
            biped.head.visible = !part.hiddenParts.contains(BodyPart.HEAD);
            biped.hat.visible = biped.hat.visible && biped.head.visible;
            if (part.bodyPart == BodyPart.HEAD) {
                matrixStack.translate(32.0F, 46.0F, 25.0F);
                matrixStack.scale(36.0F, 36.0F, 36.0F);
                matrixStack.mulPose(Axis.XP.rotation(0.3926991F));
                matrixStack.mulPose(Axis.YP.rotation((float)part.previewRotation * 0.017453292F));
                biped.head.render(matrixStack, iVertex, lt, ot);
                biped.hat.render(matrixStack, iVertex, lt, ot);
            }
            ModelPartWrapper modelPart;
            if (part.bodyPart == BodyPart.LEGS) {
                matrixStack.translate(18.0F, 12.0F, 25.0F);
                matrixStack.scale(36.0F, 36.0F, 36.0F);
                matrixStack.mulPose(Axis.XP.rotation(0.3926991F));
                matrixStack.mulPose(Axis.YP.rotation((float)part.previewRotation * 0.017453292F));
                biped.body.render(matrixStack, iVertex, lt, ot);
                biped.jacket.render(matrixStack, iVertex, lt, ot);
                if (part.animationType == PartBehaviorType.LEGS) {
                    modelPart = part.getPart("right_leg");
                    if (modelPart != null) {
                        modelPart.setRot(new NopVector3f(biped.rightLeg.xRot, biped.rightLeg.yRot, biped.rightLeg.zRot));
                        modelPart.setPos(new NopVector3f(biped.rightLeg.x, biped.rightLeg.y, biped.rightLeg.z));
                    }

                    modelPart = part.getPart("left_leg");
                    if (modelPart != null) {
                        modelPart.setRot(new NopVector3f(biped.leftLeg.xRot, biped.leftLeg.yRot, biped.leftLeg.zRot));
                        modelPart.setPos(new NopVector3f(biped.leftLeg.x, biped.leftLeg.y, biped.leftLeg.z));
                    }
                }
                biped.rightLeg.render(matrixStack, iVertex, lt, ot);
                biped.rightPants.render(matrixStack, iVertex, lt, ot);
                biped.leftLeg.render(matrixStack, iVertex, lt, ot);
                biped.leftPants.render(matrixStack, iVertex, lt, ot);
            }
            if (part.bodyPart == BodyPart.ARMS) {
                matrixStack.translate(18.0F, 12.0F, 25.0F);
                matrixStack.scale(36.0F, 36.0F, 36.0F);
                matrixStack.mulPose(Axis.XP.rotation(0.3926991F));
                matrixStack.mulPose(Axis.YP.rotation((float)part.previewRotation * 0.017453292F));
                biped.body.render(matrixStack, iVertex, lt, ot);
                biped.jacket.render(matrixStack, iVertex, lt, ot);
                if (part.animationType == PartBehaviorType.ARMS) {
                    modelPart = part.getPart("right_arm");
                    if (modelPart != null) {
                        modelPart.setRot(new NopVector3f(biped.rightArm.xRot, biped.rightArm.yRot, biped.rightArm.zRot));
                        modelPart.setPos(new NopVector3f(biped.rightArm.x, biped.rightArm.y, biped.rightArm.z));
                    }

                    modelPart = part.getPart("left_arm");
                    if (modelPart != null) {
                        modelPart.setRot(new NopVector3f(biped.leftArm.xRot, biped.leftArm.yRot, biped.leftArm.zRot));
                        modelPart.setPos(new NopVector3f(biped.leftArm.x, biped.leftArm.y, biped.leftArm.z));
                    }
                }
                biped.rightArm.render(matrixStack, iVertex, lt, ot);
                biped.rightSleeve.render(matrixStack, iVertex, lt, ot);
                biped.leftArm.render(matrixStack, iVertex, lt, ot);
                biped.leftSleeve.render(matrixStack, iVertex, lt, ot);
            }
            if (part.bodyPart == BodyPart.BODY) {
                matrixStack.translate(18.0F, 18.0F, 25.0F);
                matrixStack.scale(36.0F, 36.0F, 36.0F);
                matrixStack.mulPose(Axis.XP.rotation(0.3926991F));
                matrixStack.mulPose(Axis.YP.rotation((float)part.previewRotation * 0.017453292F));
                biped.body.render(matrixStack, iVertex, lt, ot);
                biped.jacket.render(matrixStack, iVertex, lt, ot);
            }
            if (part.renderType != PartRenderType.NONE) {
                MpmPartAbstractClient partC = (MpmPartAbstractClient)part;
                partC.pos = NopVector3f.ZERO;
                partC.rot = NopVector3f.ZERO;
                LayerParts.renderPart(data, partC, matrixStack, iRenderTypeBuffer, lt, (EntityCustomNpc) parent.npc, biped, renderData);
            }
        });
        iRenderTypeBuffer.endBatch();
        matrixStack.popPose();

        matrixStack.popPose();
        entityRendererManager.setRenderShadow(true);
        RenderSystem.applyModelViewMatrix();
    }

}
