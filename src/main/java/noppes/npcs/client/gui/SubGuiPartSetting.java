package noppes.npcs.client.gui;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.gui.subgui.IPartSetting;
import noppes.npcs.client.gui.model.GuiCreationParts;
import noppes.npcs.client.gui.select.SubGuiColorSelector;
import noppes.npcs.client.gui.select.SubGuiTextureSelection;
import noppes.npcs.client.layer.LayerParts;
import noppes.npcs.client.parts.ModelEyeData;
import noppes.npcs.client.parts.MpmPart;
import noppes.npcs.client.parts.MpmPartData;
import noppes.npcs.client.parts.MpmPartEyes;
import noppes.npcs.constants.BodyPart;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.GuiModelPart;
import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.shared.common.util.ColorUtil;
import noppes.npcs.shared.common.util.NopVector2i;
import noppes.npcs.shared.common.util.NopVector3f;

import java.awt.*;

public class SubGuiPartSetting extends GuiBasic implements ITextfieldListener, IPartSetting {

    protected static final ResourceLocation resource = new ResourceLocation(CustomNpcs.MODID, "textures/gui/components.png");

    protected final MpmPart part;
    protected final MpmPartData data;
    protected GuiModelPart partGui;
    protected final GuiCreationParts parent;
    protected SettingCallback callback;

    public SubGuiPartSetting(GuiCreationParts parentIn, MpmPartData dataIn, MpmPart partIn) {
        super();
        imageWidth = 310;
        imageHeight = 200;
        setBackground("bgfilled.png");

        parent = parentIn;
        data = dataIn;
        part = partIn;
    }

    public SubGuiPartSetting(GuiCreationParts parentIn, MpmPartData dataIn, MpmPart partIn, SettingCallback callbackIn) {
        this(parentIn, dataIn, partIn);
        callback = callbackIn;
    }

    @Override
    public void buttonEvent(GuiButtonNop guiButton) {
        boolean update = false;
        switch (guiButton.id) {
            case 3: {
                SubGuiTextureSelection.dark = ((GuiCheckBoxNop) guiButton).selected();
                break;
            } // back color
            case 22: {
                if (data instanceof ModelEyeData eyeData) {
                    eyeData.skinType = guiButton.getValue();
                    update = true;
                }
                break;
            } // eye skin type
            case 26: {
                if (data instanceof ModelEyeData eyeData) {
                    eyeData.eyeSize = guiButton.getValue();
                    update = true;
                }
                break;
            } // eye size
            case 27: {
                if (data instanceof ModelEyeData eyeData) {
                    eyeData.mirror = guiButton.getValue() == 1;
                    update = true;
                }
                break;
            } // eye mirror
            case 29: {
                if (data instanceof ModelEyeData eyeData) {
                    eyeData.eyePos = new NopVector2i(eyeData.eyePos.x, guiButton.getValue() - 2);
                    update = true;
                }
                break;
            } // eye pos y
            case 30: {
                if (data instanceof ModelEyeData eyeData) {
                    eyeData.eyePos = new NopVector2i(guiButton.getValue() - 1, eyeData.eyePos.y);
                    update = true;
                }
                break;
            } // eye pos x
            case 32: {
                if (data instanceof ModelEyeData eyeData) {
                    eyeData.glint = ((GuiButtonYesNo) guiButton).getBoolean();
                    update = true;
                }
                break;
            } // eye glint
            case 35: {
                if (data instanceof ModelEyeData eyeData) {
                    eyeData.browThickness = new NopVector3f(1.0F, (float) guiButton.getValue() / 10.0F, 1.0F);
                    update = true;
                }
                break;
            } // eye browThickness
            case 37: {
                if (data instanceof ModelEyeData eyeData) {
                    eyeData.disableBlink = ((GuiButtonYesNo) guiButton).getBoolean();
                    update = true;
                }
                break;
            } // eye disableBlink
            case 66: onClose(); break;
        }
        if (update) {
            partGui.modelData.refreshParts();
            save();
            init();
        }
    }

    @SuppressWarnings("all")
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        PoseStack matrixStack = graphics.pose();
        if (part instanceof MpmPartEyes eyeData) {
            matrixStack.pushPose();
            matrixStack.translate(guiLeft + 10, guiTop + 11, 1.0f);

            matrixStack.scale(1.0F, 1.0F, -1.0F);
            RenderSystem.applyModelViewMatrix();

            matrixStack.pushPose();
            EntityRenderDispatcher entityRendererManager = minecraft.getEntityRenderDispatcher();
            entityRendererManager.setRenderShadow(false);
            MultiBufferSource.BufferSource iRenderTypeBuffer = minecraft.renderBuffers().bufferSource();
            VertexConsumer iVertex = iRenderTypeBuffer.getBuffer(RenderType.entityCutoutNoCull(parent.npc.textureLocation));

            Lighting.setupForFlatItems();
            RenderSystem.runAsFancy(() -> {
                GuiModelPart.biped.body.visible = !part.hiddenParts.contains(BodyPart.BODY);
                GuiModelPart.biped.jacket.visible = GuiModelPart.biped.jacket.visible && GuiModelPart.biped.body.visible;
                GuiModelPart.biped.head.visible = !part.hiddenParts.contains(BodyPart.HEAD);
                GuiModelPart.biped.hat.visible = GuiModelPart.biped.hat.visible && GuiModelPart.biped.head.visible;
                matrixStack.translate(19.0F, 43.0F, 25.0F);
                matrixStack.scale(100.0F, 100.0F, 100.0F);
                GuiModelPart.biped.head.render(matrixStack, iVertex, 15728880, OverlayTexture.NO_OVERLAY);
                eyeData.pos = NopVector3f.ZERO;
                eyeData.rot = NopVector3f.ZERO;
                LayerParts.renderPart(data, eyeData, matrixStack, iRenderTypeBuffer, LightTexture.FULL_BRIGHT, (EntityCustomNpc) parent.npc, GuiModelPart.biped, partGui.renderData);
            });
            iRenderTypeBuffer.endBatch();
            matrixStack.popPose();

            matrixStack.popPose();
            entityRendererManager.setRenderShadow(true);
            RenderSystem.applyModelViewMatrix();
            super.render(graphics, mouseX, mouseY, partialTicks);
            return;
        }
        super.render(graphics, mouseX, mouseY, partialTicks);

        partGui.y = guiTop + 4.0f;
        partGui.isHovered = false;

        partGui.customRender(graphics, mouseX, mouseY, partialTicks);;
        ResourceLocation location;
        if (data.usePlayerSkin) { location = parent.npc.textureLocation; }
        else { location = data.getDefaultTexture() == null ? data.getUrlTexture() : data.getDefaultTexture(); }
        if (location == null) { return; }
        int size = 135;
        matrixStack.pushPose();
        matrixStack.translate(guiLeft + 130, guiTop + 5, 1.0f);
        // background
        graphics.fill(- 1, - 1, size + 1, size + 1, SubGuiTextureSelection.dark ?
                new Color(0xFFE0E0E0).getRGB() :
                new Color(0xFF202020).getRGB());
        graphics.fill(0, 0, size, size, SubGuiTextureSelection.dark ?
                new Color(0xFF000000).getRGB() :
                new Color(0xFFFFFFFF).getRGB());
        int g = 5;
        for (int u = 0; u < size / g; u++) {
            for (int v = 0; v < size / g; v++) {
                if (u % 2 == (v % 2 == 0 ? 1 : 0)) {
                    graphics.fill(u * g, v * g, u * g + g, v * g + g, SubGuiTextureSelection.dark ?
                            new Color(0xFF343434).getRGB() :
                            new Color(0xFFCCCCCC).getRGB());
                }
            }
        }
        // texture
        matrixStack.pushPose();
        float s = (float) size / 256.0f;
        matrixStack.scale(s, s, 1.0f);
        graphics.blit(location, 0, 0, 0, 0, 256, 256);
        matrixStack.popPose();

        matrixStack.popPose();
    }

    @Override
    public void init() {
        super.init();
        partGui = new GuiModelPart(parent, 0, guiLeft + 4, part, true);
        partGui.height = 68;
        partGui.zPos = 50;
        partGui.basic = true;
        partGui.init();

        int lId = 0;
        int w = 220;
        int x0 = guiLeft + 6;
        int x1 = guiLeft + w + 8;
        int y = guiTop + 5;
        addButton(66, guiLeft + imageWidth - 24, y, "x")
                .setSize(20, 20)
                .setHoverTexts("hover.back");
        if (data instanceof ModelEyeData eyeData) {
            int x2 = guiLeft + 56;
            int x3 = x2 + 108;
            addLabel(lId++, x2, y + 5, "part.eyes").setSize(52, 10);
            addButton(22, x2 + 54, y, true, eyeData.skinType, "gui.playerskin", "gui.normal", "gui.texture")
                    .setSize(110, 20);
            if (eyeData.skinType == 1) {
                int color = ColorUtil.rgbToColor(eyeData.color);
                StringBuilder str = new StringBuilder(Integer.toHexString(color));
                while (str.length() < 6) { str.insert(0, "0"); }
                while (str.length() > 6) { str.deleteCharAt(0); }
                add(new GuiButtonNop(this, 23, str, guiLeft + 230, y,(button) ->
                        setSubGui(new SubGuiColorSelector(color, new SubGuiColorSelector.ColorCallback() {
                            @Override
                            public void color(int colorIn) {
                                eyeData.color = ColorUtil.colorToRgb(colorIn);
                                partGui.modelData.refreshParts();
                                save();
                                init();
                            }
                            @Override
                            public void preColor(int colorIn) {  }
                        })))
                        .setSize(50, 20)
                        .setColor(color));
                y += 50;
            }
            else if (eyeData.skinType == 2) {
                add(new GuiButtonNop(this, 20, "gui.select", guiLeft + 230, y, (button) ->
                        setSubGui(new SubGuiTextureSelection(this, 0, parent.npc, eyeData.url, ".png", 3, (subGuiTexture) -> {
                            if (subGuiTexture.resource != null) {
                                eyeData.setUrl(subGuiTexture.resource.toString());
                                partGui.modelData.refreshParts();
                                save();
                                init();
                            }
                        }))).setSize(50, 20));
                addLabel(lId++, x2, (y += 25) + 5, "config.skinurl")
                        .setSize(52, 10);
                addTextField(25, x2 + 54, y, 195, 20, eyeData.url);
                y += 25;
            }
            else { y += 50; }
            addLabel(lId++, x0, y + 5, "eye.pupil").setSize(48, 10);
            addButton(26, x2, y, true, eyeData.eyeSize, "gui.normal", "gui.big").setSize(100, 20);
            addButton(27, x3, y, true, eyeData.mirror ? 1 : 0, "gui.normal", "gui.mirror")
                    .setSize(100, 20)
                    .setIsVisible(eyeData.glint || eyeData.skinType == 1 || eyeData.skinType == 2);
            addLabel(lId++, x0, (y += 25) + 5, "gui.position").setSize(48, 10);
            addButton(29, x2, y, true, eyeData.eyePos.y + 2,
                    Component.translatable("gui.down").append(" x2"),
                    "gui.down", "gui.normal", "gui.up",
                    Component.translatable("gui.up").append(" x2")).setSize(100, 20);
            addButton(30, x3, y, true, eyeData.eyePos.x + 1, "gui.inward", "gui.normal", "gui.outward")
                    .setSize(100, 20);
            addLabel(lId++, x0, (y += 25) + 5, "eye.glint").setSize(48, 10);
            addYesNo(32, x2, y, eyeData.glint).setSize(50, 20);
            addLabel(lId++, x3 - 50, y + 5, "eye.brow").setSize(48, 10);
            int browColor = ColorUtil.rgbToColor(eyeData.browColor);
            StringBuilder str1 = new StringBuilder(Integer.toHexString(browColor));
            while (str1.length() < 6) { str1.insert(0, "0"); }
            while (str1.length() > 6) { str1.deleteCharAt(0); }
            add(new GuiButtonNop(this, 34, str1, x3, y, (button) ->
                    setSubGui(new SubGuiColorSelector(browColor, new SubGuiColorSelector.ColorCallback() {
                        @Override
                        public void color(int colorIn) {
                            eyeData.browColor = ColorUtil.colorToRgb(colorIn);
                            partGui.modelData.refreshParts();
                            save();
                            init();
                        }
                        @Override
                        public void preColor(int colorIn) {  }
                    })))
                    .setSize(50, 20)
                    .setColor(browColor));
            addButton(35, x3 + 55, y, true, (int) (eyeData.browThickness.y * 10.0f), "gui.disabled", "1", "2", "3", "4", "5", "6", "7", "8")
                    .setSize(70, 20);
            addLabel(lId++, x0, (y += 25) + 5, "eye.blink").setSize(48, 10);
            addYesNo(37, x2, y, eyeData.disableBlink).setSize(50, 20);
            addLabel(lId, x3 - 50, y + 5, "eye.lid")
                    .setSize(48, 10)
                    .setIsVisible(!eyeData.disableBlink);
            int lidColor = ColorUtil.rgbToColor(eyeData.lidColor);
            StringBuilder str2 = new StringBuilder(Integer.toHexString(lidColor));
            while (str2.length() < 6) { str2.insert(0, "0"); }
            while (str2.length() > 6) { str2.deleteCharAt(0); }
            add(new GuiButtonNop(this, 40, str2, x3, y, (button) ->
                    setSubGui(new SubGuiColorSelector(lidColor, new SubGuiColorSelector.ColorCallback() {
                        @Override
                        public void color(int colorIn) {
                            eyeData.lidColor = ColorUtil.colorToRgb(colorIn);
                            partGui.modelData.refreshParts();
                            save();
                            init();
                        }
                        @Override
                        public void preColor(int colorIn) {  }
                    })))
                    .setSize(50, 20)
                    .setColor(lidColor)
                    .setIsVisible(!eyeData.disableBlink));
            return;
        }
        addCheckBox(3, guiLeft + 118, y, null, null, SubGuiTextureSelection.dark)
                .setSize(10, 10)
                .setHoverTexts("texture.hover.dark");
        if (part.disableCustomTextures) { return; }
        y = guiTop + 112;
        addLabel(lId++, x0, y + 4, "gui.playerskin").setSize(100, 10);
        add(new GuiButtonYesNo(this, 22, guiLeft + 74, y, data.usePlayerSkin, (button) -> {
            data.usePlayerSkin = ((GuiButtonYesNo) button).getBoolean();
            partGui.modelData.refreshParts();
            save();
            init();
        }).setSize(50, 18));
        if (data.usePlayerSkin) { return; }
        addLabel(lId++, x0, y += 20, "gui.texture").setSize(100, 10);
        addTextField(24, x0, y += 12, w, 18, data.getDefaultTexture());
        add(new GuiButtonNop(this, 25, "gui.select", x1, y, (button) ->
                setSubGui(new SubGuiTextureSelection(this, 0, parent.npc, data.getDefaultTexture() == null ? "" : data.getDefaultTexture().toString(), ".png", 3, (subGuiTexture) -> {
                    if (subGuiTexture.resource != null) {
                        data.setTexture(subGuiTexture.resource.toString());
                        partGui.modelData.refreshParts();
                        save();
                        init();
                    }
                }))).setSize(50, 18));
        addLabel(lId, x0, y += 20, "config.skinurl").setSize(100, 10);
        addTextField(27, x0, y += 12, w, 18, data.getUrlTexture());
        add(new GuiButtonNop(this, 26, "gui.select", x1, y, (button) ->
                setSubGui(new SubGuiTextureSelection(this, 0, parent.npc, data.getDefaultTexture() == null ? "" : data.getDefaultTexture().toString(), ".png", 3, (subGuiTexture) -> {
                    if (subGuiTexture.resource != null) {
                        data.setUrl(subGuiTexture.resource.toString());
                        partGui.modelData.refreshParts();
                        save();
                        init();
                    }
                }))).setSize(50, 18));
    }

    public void onClose() {
        super.onClose();
        if (callback != null) { callback.close(this); }
    }

    @Override
    public void unFocused(GuiTextFieldNop textField) {
        switch (textField.id) {
            case 24: data.setTexture(textField.getValue()); break;
            case 25: {
                if (data instanceof ModelEyeData eyeData) { eyeData.setUrl(textField.getValue()); }
                break;
            }
            case 27: data.setUrl(textField.getValue()); break;
        }
        partGui.modelData.refreshParts();
        save();
        init();
    }

    public interface SettingCallback { void close(SubGuiPartSetting subGuiPart); }

}
