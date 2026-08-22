package noppes.npcs.client.gui.animation;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.select.SubGuiTextureSelection;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.client.model.ModelNpcAlt;
import noppes.npcs.client.model.ModelRendererAlt;
import noppes.npcs.client.model.animation.AddedPartConfig;
import noppes.npcs.client.model.animation.AnimationConfig;
import noppes.npcs.client.model.animation.AnimationFrameConfig;
import noppes.npcs.client.model.animation.PartConfig;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SubGuiEditAddPart extends GuiNPCInterface implements ITextfieldListener {

    protected static final ResourceLocation backResource = getResource("bgfilled.png");

    public final AddedPartConfig addPart;
    public final PartConfig part;
    public final boolean isNew;
    public final SubGuiEditAnimation parent;
    public final Map<Component, Integer> dataPartIDs = new LinkedHashMap<>();
    public final AnimationConfig animation;
    public boolean isSave = false;
    protected boolean onlyPart = false;
    protected final EntityNPCInterface npcPart;
    protected ModelRendererAlt partRender;
    protected final ModelNpcAlt model;
    protected int typeModel = 0;
    protected int typePart = 0;

    // display
    protected int workU;
    protected int workV;
    protected final int workS = 144;
    protected double w = -1.0d;
    protected double h = -1.0d;
    protected int mousePressId = -1;
    protected double mousePressX = 0;
    protected double mousePressY = 0;
    protected boolean hovered = false;
    protected final float[] dispRot = new float[] { 45.0f, 345.0f, 345.0f };
    protected final float[] dispPos = new float[] { 0.0f, 0.0f, 0.0f };
    protected float dispScale = 1.0f;

    @SuppressWarnings("rawtypes")
    public SubGuiEditAddPart(SubGuiEditAnimation gui, EntityNPCInterface npc, EntityNPCInterface npcPartIn, AddedPartConfig parentAddPart, PartConfig parentPart) {
        super(npc);
        imageWidth = 427;
        imageHeight = 240;
        widthTexture = 256;
        heightTexture = 256;

        npcPart = npcPartIn;
        animation = npcPart.animation.getAnimation();
        parent = gui;
        if (parentAddPart == null || parentPart == null) {
            addPart = new AddedPartConfig();
            addPart.id = -1;
            part = new PartConfig();
            part.name = "custom.name";
            part.id = -1;
            isNew = true;
        } // create
        else {
            addPart = parentAddPart;
            part = parentPart;
            isNew = false;
            isSave = true;
            typePart = parentAddPart.isNormal ? 0 : 1;
            if (parentAddPart.objUp != null) {
                try {
                    minecraft.getResourceManager().getResource(parentAddPart.objUp);
                    typeModel = 1;
                } catch (Exception ignored) {  }
                if (!parentAddPart.isNormal) {
                    try {
                        minecraft.getResourceManager().getResource(parentAddPart.objDown);
                        typePart = 1;
                    } catch (Exception ignored) {
                    }
                }
            }
        } // edit
        GuiNpcAnimation.backColor = 0xFFFFFFFF;

        RenderNPCInterface<?> render = (RenderNPCInterface) minecraft.getRenderManager().getEntityClassRenderObject(EntityCustomNpc.class);
        model = (ModelNpcAlt) render.getMainModel();
    }

    @Override
    public void buttonEvent(@Nonnull GuiButtonNop button) {
        switch (button.id) {
            case 0: {
                if (!dataPartIDs.containsKey(button.getMessage())) { return; }
                addPart.parentPart = dataPartIDs.get(button.getMessage());
                String hover = Component.translatable("info.item.cloner.empty.0").getFormattedText();
                if (parent.frame.parts.containsKey(addPart.parentPart)) { hover = Component.translatable(parent.frame.parts.get(addPart.parentPart).name).getFormattedText(); }
                if (getTextField(1) != null) {
                    getTextField(1).setValue("" + addPart.parentPart);
                    getTextField(1).setHoverTexts(Component.translatable("animation.add.part.hover.part.ids", hover));
                }
                button.setHoverTexts(Component.translatable("animation.add.part.hover.part.ids", hover));
                initGui();
                break;
            } // set new parentPart ID
            case 1: {
                onlyPart = !onlyPart;
                dispScale = 1.0f;
                dispRot[0] = 45.0f;
                dispRot[1] = 345.0f;
                dispRot[2] = 345.0f;
                for (int i = 0; i < 3; i++) { dispPos[i] = 0.0f; }
                initGui();
                break;
            } // only part
            case 2: {
                GuiNpcAnimation.backColor = (GuiNpcAnimation.backColor == 0xFF000000 ? 0xFFFFFFFF : 0xFF000000);
                button.setColor(GuiNpcAnimation.backColor == 0xFF000000 ? 0xFF00FFFF : 0xFF008080);
                break;
            } // back color
            case 3: typeModel = button.getValue(); initGui(); break; // model type
            case 4: {
                typePart = button.getValue();
                addPart.isNormal = typePart == 0;
                initGui();
                break;
            } // part type
            case 18: dispScale = 1.0f; break; // display reset scale
            case 19: {
                for (int i = 0; i < 3; i++) { dispPos[i] = 0.0f; }
                break;
            } // display reset pos
            case 20: {
                dispRot[0] = 45.0f;
                dispRot[1] = 345.0f;
                dispRot[2] = 345.0f;
                break;
            } // display reset rot
            case 65: isSave = true; onClose(); break; // done
            case 66: onClose(); break; // back
        }
    }

    private void displayOffset(float x, float y) {
        for (int i = 0; i < 2; i++) {
            dispPos[i] += (i == 0 ? x : y);
            int wS = (i == 1 ? 144 : 108);
            if (dispPos[i] > wS * dispScale) { dispPos[i] = wS * dispScale; }
            else if (dispPos[i] < -wS * dispScale) { dispPos[i] = -wS * dispScale; }
        }
    }

    private void displayRotate(float x, float y) {
        dispRot[0] += x;
        dispRot[1] += (float) (Math.cos(dispRot[0] * Math.PI / 180.0f) * y);
        dispRot[2] += (float) (Math.sin(dispRot[0] * Math.PI / 180.0f) * y);
        for (int i = 0; i < 3; i++) {
            if (dispRot[i] > 360.0f) { dispRot[i] -= 360.0f; }
            else if (dispRot[i] < 0.0f) { dispRot[i] += 360.0f; }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!hasSubGui()) {
            if (w < 0 || h < 0) {
                ScaledResolution sw = new ScaledResolution(mc);
                w = sw.getScaledWidth();
                h = sw.getScaledHeight();
            }
            npcPart.animation.updateTime();
            npcPart.chasingPosZ = npc.chasingPosZ;
            npcPart.chasingPosY = npc.chasingPosY;
            npcPart.chasingPosX = npc.chasingPosX;
            npcPart.prevChasingPosZ = npc.prevChasingPosZ;
            npcPart.prevChasingPosY = npc.prevChasingPosY;
            npcPart.prevChasingPosX = npc.prevChasingPosX;
            npcPart.ticksExisted = npc.ticksExisted;
            // display data
            if (Mouse.isButtonDown(mousePressId)) {
                float x = mouseX - (float) mousePressX;
                float y = mouseY - (float) mousePressY;
                if (x != 0 || y != 0) {
                    if (mousePressId == 0) { displayOffset(x, y); } // LMB
                    else if (mousePressId == 1) { displayRotate(x, -y); } // RMB
                    mousePressX = mouseX;
                    mousePressY = mouseY;
                }
            }
            else { mousePressId = -1; }
            hovered = isMouseHover(mouseX, mouseY, workU + 1, workV + 1, workS - 2, workS - 2);
            if (hovered) {
                int dWheel = Mouse.getDWheel();
                if (dWheel != 0) {
                    dispScale += dispScale * (dWheel < 0 ? 0.1f : -0.1f);
                    if (dispScale < 0.5f) { dispScale = 0.5f; }
                    else if (dispScale > 10.0f) { dispScale = 10.0f; }
                    dispScale = (float) (Math.round(dispScale * 20.0d) / 20.0d);
                    if (dispScale == 0.95f || dispScale == 1.05f) { dispScale = 1.0f; }
                }
            }
            // work place
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0f, 0.0f, -300.0f);
            minecraft.getTextureManager().bindTexture(backResource);
            int tilesWL = imageWidth / 2;
            int tilesWR = imageWidth - tilesWL;
            int tilesHL = imageHeight / 2;
            int tilesHR = imageHeight - tilesHL;
            drawTexturedModalRect(guiLeft, guiTop, 0, 0, tilesWL, tilesHL);
            drawTexturedModalRect(guiLeft + tilesWL, guiTop, widthTexture - tilesWR, 0, tilesWR, tilesHL);
            drawTexturedModalRect(guiLeft, guiTop + tilesHL, 0, heightTexture - tilesHR, tilesWL, tilesHR);
            drawTexturedModalRect(guiLeft + tilesWL, guiTop + tilesHL, widthTexture - tilesWR, heightTexture - tilesHR, tilesWR, tilesHR);
            int color = GuiNpcAnimation.backColor == 0xFF000000 ?
                    new Color(0xFFF080F0).getRGB() :
                    new Color(0xFFF020F0).getRGB();
            drawGradientRect(workU, workV, workU + workS, workV + workS, color, color);
            GlStateManager.popMatrix();
            // lines
            color = new Color(0xF0404040).getRGB();
            GlStateManager.pushMatrix();
            drawVerticalLine(guiLeft + 126, guiTop + 3,guiTop + imageHeight - 4, color);
            drawVerticalLine(workU - 2, guiTop + 3,guiTop + imageHeight - 4, color);
            int y = guiTop + 73;
            drawHorizontalLine(guiLeft + 4, guiLeft + 124, y, color);
            if (typeModel == 1) {
                y += 24;
                if (typePart == 1) { y += 14; }
                y += 2;
                drawHorizontalLine(guiLeft + 4, guiLeft + 124, y, color);
                y += 24;
            }
            else { y += 26; }
            drawHorizontalLine(guiLeft + 4, guiLeft + 124, y, color);
            y = guiTop + 14;
            drawHorizontalLine(guiLeft + 128, workU - 4, y += typePart == 1 ? 67 : 39, color);
            drawHorizontalLine(guiLeft + 128, workU - 4, y += 24, color);
            drawHorizontalLine(guiLeft + 128, workU - 4, y += 52, color);
            drawHorizontalLine(guiLeft + 128, workU - 4, y, color);
            GlStateManager.popMatrix();
            // texture
            GlStateManager.pushMatrix();
            GlStateManager.translate(guiLeft + 22.0f, guiTop + 140.0f, 0.0f);
            Gui.drawRect(-1, -1, 85, 85, SubGuiTextureSelection.dark ?
                    new Color(0xFFE0E0E0).getRGB() :
                    new Color(0xFF202020).getRGB());
            Gui.drawRect(0, 0, 84, 84, SubGuiTextureSelection.dark ?
                    new Color(0xFF000000).getRGB() :
                    new Color(0xFFFFFFFF).getRGB());
            int g = 6;
            for (int u = 0; u < 84 / g; u++) {
                for (int v = 0; v < 84 / g; v++) {
                    if (u % 2 == (v % 2 == 0 ? 1 : 0)) {
                        Gui.drawRect(u * g, v * g, u * g + g,  v * g + g, SubGuiTextureSelection.dark ?
                                new Color(0xFF343434).getRGB() :
                                new Color(0xFFCCCCCC).getRGB());
                    }
                }
            }
            if (addPart.location != null) {
                float scale = 84.0f / 256.0f;
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.enableAlpha();
                GlStateManager.scale(scale, scale, 1.0f);
                GlStateManager.color(2.0f, 2.0f, 2.0f, 1.0f);
                try { GuiNpcUtil.drawTexturedModalRect(addPart.location, 0, 0, 256, 256, 256); }
                catch (Exception ignored) { }
                if (typeModel == 0) {
                    float scaleW = 256.0f / (float) GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
                    float scaleH = 256.0f / (float) GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
                    int u0 = (int) (addPart.textureU * scaleW);
                    int v0 = (int) (addPart.textureV * scaleH);
                    int u1 = (int) ((addPart.textureU + addPart.size[4]) * scaleW);
                    int u2 = (int) ((addPart.textureU + addPart.size[0] + addPart.size[4]) * scaleW);
                    int u3 = (int) ((addPart.textureU + addPart.size[0] + 2.0f * addPart.size[4]) * scaleW);
                    int u4 = (int) ((addPart.textureU + 2.0f * (addPart.size[0] + addPart.size[4])) * scaleW);
                    int v1 = (int) ((addPart.textureV + addPart.size[4]) * scaleW);
                    int v2 = (int) ((addPart.textureV + addPart.size[1] + addPart.size[2] + addPart.size[3] + addPart.size[4]) * scaleW);
                    long ms = System.currentTimeMillis() % 1500L;
                    if (ms >= 750) { ms -= 750; }
                    else {
                        ms -= 750;
                        ms *= -1;
                    }
                    color = new Color(255, 0, 0, 255 - (int) (0.175d * (double) ms)).getRGB();
                    drawHorizontalLine(u0, u4, v0, color);
                    drawHorizontalLine(u0, u4, v1, color);
                    drawHorizontalLine(u0, u4, v2, color);
                    drawHorizontalLine(u0, u4, v0 + 1, color);
                    drawHorizontalLine(u0, u4, v1 + 1, color);
                    drawHorizontalLine(u0, u4, v2 + 1, color);
                    drawVerticalLine(u0, v0, v2, color);
                    drawVerticalLine(u1, v0, v2, color);
                    drawVerticalLine(u2, v0, v2, color);
                    drawVerticalLine(u3, v0, v2, color);
                    drawVerticalLine(u4, v0, v2, color);
                    drawVerticalLine(u0 + 1, v0, v2, color);
                    drawVerticalLine(u1 + 1, v0, v2, color);
                    drawVerticalLine(u2 + 1, v0, v2, color);
                    drawVerticalLine(u3 + 1, v0, v2, color);
                    drawVerticalLine(u4 + 1, v0, v2, color);
                }
                GlStateManager.depthMask(true);
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
            GlStateManager.popMatrix();
            // npc or part
            GlStateManager.pushMatrix();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            int c = w < minecraft.displayWidth ? (int) Math.round((double) minecraft.displayWidth / w) : 1;
            GL11.glScissor((workU + 1) * c, minecraft.displayHeight - (workV + workS - 1) * c, (workS - 2) * c, (workS - 2) * c);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            drawWork();
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GlStateManager.popMatrix();
            // display info
            GlStateManager.pushMatrix();
            GlStateManager.translate(workU, workV, 950.0f);
            color = GuiNpcAnimation.backColor == 0xFF000000 ?
                    new Color(0xFFFFFFFF).getRGB() :
                    new Color(0xFF000000).getRGB();
            String ts = "x" + dispScale;
            fontRenderer.drawString(ts, workS - 11 - fontRenderer.getStringWidth(ts), 1, color, false);
            ts = (int) dispRot[0] + "" + ((char) 176) + "/" + (int) dispRot[1] + ((char) 176) + "/" + (int) dispRot[2] + ((char) 176);
            fontRenderer.drawString(ts, workS - 11 - fontRenderer.getStringWidth(ts), workS - 10, color, false);
            ts = (int) dispPos[0] + "/" + (int) dispPos[1];
            fontRenderer.drawString(ts, 11, workS - 10, color, false);
            GlStateManager.popMatrix();
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawWork() {
        // work place
        GlStateManager.pushMatrix();
            GlStateManager.translate(0.0f, 0.0f, -300.0f);
            Gui.drawRect(workU + 1, workV + 1, workU + workS - 1, workV + workS - 1, GuiNpcAnimation.backColor);
        GlStateManager.popMatrix();
        // draw model
        GlStateManager.pushMatrix();
            postRender();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.disableLighting();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableRescaleNormal();
            GlStateManager.enableColorMaterial();
            if (onlyPart) { // new part
                if (partRender != null) {
                    GlStateManager.translate(-35.0f, -90.0f, 0.0f);
                    float s = 150.0f;
                    float size = Math.max(addPart.size[0], Math.max(addPart.size[1] + addPart.size[2] + addPart.size[3], addPart.size[4]));
                    s *= size / 12.0f;
                    GlStateManager.scale(s, s, s);
                    partRender.render(0.0625f);
                }
            }
            else { // npc
                GlStateManager.translate(0.5f, 12.0f, -0.5f);
                minecraft.getRenderManager().playerViewY = 180.0f;
                ModelNpcAlt.editAnimDataSelect.displayNpc = npcPart;
                ModelNpcAlt.editAnimDataSelect.isNPC = true;
                ModelNpcAlt.editAnimDataSelect.part = part.id;
                GlStateManager.scale(40.0f, -40.0f, 40.0f);
                minecraft.getRenderManager().renderEntity(npcPart, 0.0, 0.0, 0.0, 0.0f, npcPart.rotationYaw != 0.0f ? 1.0f : 0.0f, false);
            }
            GlStateManager.disableRescaleNormal();
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.disableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.enableLighting();

        GlStateManager.popMatrix();
    }

    private void postRender() {
        GlStateManager.translate(workU + workS / 2.0f, workV + workS / 2.0f, 100.0f * dispScale); // center
        GlStateManager.translate(dispPos[0], dispPos[1], 0.0f);
        GlStateManager.rotate(dispRot[0], 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(dispRot[1], 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(dispRot[2], 0.0f, 0.0f, 1.0f);
        GlStateManager.scale(dispScale, dispScale, dispScale);
        GlStateManager.translate(0.0f, 25.0f, 0.0f);
    }

    @Override
    public void initGui() {
        super.initGui();
        ScaledResolution sw = new ScaledResolution(mc);
        w = sw.getScaledWidth();
        h = sw.getScaledHeight();
        workU = guiLeft + imageWidth - workS - 4;
        workV = guiTop + 4;
        int lId = 0;
        int x = guiLeft + 5;
        int y = guiTop + 14;
        // name
        addLabel(lId++, x + 1, y - 10, "gui.name");
        addTextField(0, x, y, 120, 12, part.name)
                .setHoverTexts(Component.translatable("animation.add.part.hover.name", Component.translatable(part.name).getFormattedText()));
        // parent part ID
        dataPartIDs.clear();
        dataPartIDs.put(Component.literal("-1"), -1);
        int j = 0;
        int p = 0;
        for (int i = 0; i < 6; i++, j++) {
            dataPartIDs.put(Component.literal("" + i), i);
            if (i == addPart.parentPart) { p = j + 1; }
        }
        for (int parentID : parent.anim.addParts.keySet()) {
            for (AddedPartConfig ap : parent.anim.addParts.get(parentID)) {
                if (ap.id < 9 || ap.id == addPart.id) { continue; }
                dataPartIDs.put(Component.literal("" + ap.id), ap.id);
                if (ap.id == addPart.parentPart) { p = j; }
                j++;
            }
        }
        if (isNew) {
            // remove old
            List<Integer> founds = new ArrayList<>();
            for (Integer parentID : animation.addParts.keySet()) {
                AddedPartConfig del = null;
                for (AddedPartConfig apc : animation.addParts.get(parentID)) {
                    if (apc.id == -1) {
                        founds.add(parentID);
                        del = apc;
                        break;
                    }
                }
                if (del != null) { animation.addParts.get(parentID).remove(del); }
            }
            if (!founds.isEmpty()) {
                for (Integer id : founds) {
                    if (animation.addParts.get(id).isEmpty()) {  animation.addParts.remove(id); }
                }
            }
            if (!animation.addParts.containsKey(addPart.parentPart)) { animation.addParts.put(addPart.parentPart, new ArrayList<>()); }
            animation.addParts.get(addPart.parentPart).add(addPart);
            for (AnimationFrameConfig frame : animation.frames.values()) {
                if (frame.parts.containsKey(part.id)) { continue; }
                frame.parts.put(part.id, part);
            }
        }
        addLabel(lId++, x + 1, (y += 24) - 10, "animation.add.part.ids");
        String hover = "info.item.cloner.empty.0";
        if (parent.frame.parts.containsKey(addPart.parentPart)) { hover = parent.frame.parts.get(addPart.parentPart).name; }
        addButton(0, x, y, true, p, dataPartIDs.keySet())
                .setSize(83, 12)
                .setHoverTexts(Component.translatable("animation.add.part.hover.part.ids", Component.translatable(hover).getFormattedText()));
        addTextField(1, x + 83 + 2, y, 35, 12, "" + addPart.parentPart)
                .setMinMaxDefault(-1, Integer.MAX_VALUE, addPart.parentPart)
                .setHoverTexts(Component.translatable("animation.add.part.hover.part.ids", Component.translatable(hover).getFormattedText()));
        // model type
        addLabel(lId++, x + 1, (y += 24) - 10, Component.translatable("gui.model").append(":"));
        addButton(3, x, y, false, typeModel, "gui.normal", "gui.obj")
                .setSize(50, 10)
                .setTexture(ANIMATION_BUTTONS)
                .setDefBack(false)
                .setIsAnim(true)
                .setUV(0, 96, 0, 0)
                .setHoverTexts("animation.add.part.hover.type.model");
        // part type
        addLabel(lId++, x + 56, y - 10, Component.translatable("gui.type").append(":"));
        addButton(4, x + 55, y, false, typePart, "gui.normal", "gui.joint")
                .setSize(50, 10)
                .setTexture(ANIMATION_BUTTONS)
                .setDefBack(false)
                .setIsAnim(true)
                .setUV(0, 96, 0, 0)
                .setHoverTexts("animation.add.part.hover.type.part");
        // obj path's
        if (typeModel == 1) {
            addLabel(lId++, x, (y += 24) - 10, Component.translatable("gui.path", ":"));
            addTextField(2, x, y, 120, 12, addPart.objUp == null ? "" : addPart.objUp.toString())
                    .setHoverTexts("animation.add.part.hover.obj.up");
            if (typePart == 1) {
                addTextField(3, x, (y += 14), 120, 12, addPart.objDown == null ? "" : addPart.objDown.toString())
                        .setHoverTexts("animation.add.part.hover.obj.down");
            }
        }
        // texture
        addLabel(lId++, x, (y += 24) - 10, Component.translatable("display.texture").append(":"));
        addTextField(4, x, y, 120, 12, addPart.location.toString())
                .setHoverTexts("animation.add.part.hover.location");
        // settings
        x += 125;
        y = guiTop + 4;
        // X size
        addLabel(lId++, x, y, Component.translatable("gui.settings").append(":"));
        addLabel(lId++, x, y += 12, Component.translatable("gui.scale").append(":"));
        addLabel(lId++, x, (y += 10) + 2, "X:");
        addTextField(5, x + 12, y, 59, 12, "" + addPart.size[0])
                .setMinMaxDefault(0.1d, Float.MAX_VALUE, addPart.size[0])
                .setHoverTexts("animation.add.part.hover.size.x");
        // Z size
        addLabel(lId++, x + 74, y + 2, "Z:");
        addTextField(9, x + 86, y, 59, 12, "" + addPart.size[4])
                .setMinMaxDefault(0.1d, Float.MAX_VALUE, addPart.size[4])
                .setHoverTexts("animation.add.part.hover.size.z");
        // Y / Y0 size
        float h = addPart.size[1] + addPart.size[2] + addPart.size[3];
        float value = typePart == 1 ? addPart.size[1] : h;
        addLabel(lId++, x, (y += 14) + 2, "Y" + (typePart == 1 ? "0:" : ":"));
        addTextField(6, x + 12, y, 59, 12, "" + value)
                .setMinMaxDefault(0.1d, Float.MAX_VALUE, value)
                .setHoverTexts("animation.add.part.hover.size.y"+(typePart == 1 ? "0" : ""), ((char) 167) + "6" + h);
        if (typePart == 1) {
            // Y1 size
            addLabel(lId++, x, (y += 14) + 2, "Y1:");
            addTextField(7, x + 12, y, 59, 12, "" + addPart.size[2])
                    .setMinMaxDefault(0.1d, Float.MAX_VALUE, addPart.size[2])
                    .setHoverTexts("animation.add.part.hover.size.y1", ((char) 167) + "6" + h);
            // Y2 size
            addLabel(lId++, x, (y += 14) + 2, "Y2:");
            addTextField(8, x + 12, y, 59, 12, "" + addPart.size[3])
                    .setMinMaxDefault(0.1d, Float.MAX_VALUE, addPart.size[3])
                    .setHoverTexts("animation.add.part.hover.size.y2", ((char) 167) + "6" + h);
        }
        // uv texture
        addLabel(lId++, x, (y += 24) - 10, Component.literal("UV ").append(Component.translatable("display.texture")).append(":"));
        // texture u
        addLabel(lId++, x, y + 2, "U:");
        int max = 4098;
        addTextField(10, x + 12, y, 59, 12, "" + addPart.textureU)
                .setMinMaxDefault(0, max, addPart.textureU)
                .setHoverTexts("animation.add.part.hover.texture.u", ((char) 167) + "6" + max);
        // texture v
        addLabel(lId++, x + 74, y + 2, "V:");
        addTextField(11, x + 86, y, 59, 12, "" + addPart.textureV)
                .setMinMaxDefault(0, max, addPart.textureV)
                .setHoverTexts("animation.add.part.hover.texture.v", ((char) 167) + "6" + max);
        // pos offset
        addLabel(lId++, x, y += 14, Component.translatable("type.offset").append(":"));
        // X
        addLabel(lId++, x, (y += 10) + 2, "X:");
        addTextField(12, x + 12, y, 59, 12, "" + addPart.pos[0])
                .setMinMaxDefault(Float.MIN_VALUE, Float.MAX_VALUE, addPart.pos[0])
                .setHoverTexts("animation.add.part.hover.pos", "X");
        // Y
        addLabel(lId++, x, (y += 14) + 2, "Y:");
        addTextField(13, x + 12, y, 59, 12, "" + addPart.pos[1])
                .setMinMaxDefault(Float.MIN_VALUE, Float.MAX_VALUE, addPart.pos[1])
                .setHoverTexts("animation.add.part.hover.pos", "Y");
        // Z
        addLabel(lId++, x, (y += 14) + 2, "Z:");
        addTextField(14, x + 12, y, 59, 12, "" + addPart.pos[2])
                .setMinMaxDefault(Float.MIN_VALUE, Float.MAX_VALUE, addPart.pos[2])
                .setHoverTexts("animation.add.part.hover.pos", "Z");
        // rot offset
        addLabel(lId++, x, y += 14, Component.translatable("movement.rotation").append(":"));
        // X
        addLabel(lId++, x, (y += 10) + 2, "X:");
        addTextField(15, x + 12, y, 59, 12, "" + addPart.rot[0])
                .setMinMaxDefault(Float.MIN_VALUE, Float.MAX_VALUE, addPart.rot[0])
                .setHoverTexts("animation.add.part.hover.rot", "X");
        // Y
        addLabel(lId++, x, (y += 14) + 2, "Y:");
        addTextField(16, x + 12, y, 59, 12, "" + addPart.rot[1])
                .setMinMaxDefault(Float.MIN_VALUE, Float.MAX_VALUE, addPart.rot[1])
                .setHoverTexts("animation.add.part.hover.rot", "Y");
        // Z
        addLabel(lId, x, (y += 14) + 2, "Z:");
        addTextField(17, x + 12, y, 59, 12, "" + addPart.rot[2])
                .setMinMaxDefault(Float.MIN_VALUE, Float.MAX_VALUE, addPart.rot[2])
                .setHoverTexts("animation.add.part.hover.rot", "Z");
        // only
        addButton(1, workU + workS / 2 - 25, workV + workS + 1, false, onlyPart ? 1 : 0, "gui.normal", "gui.only")
                .setSize(50, 10)
                .setTexture(ANIMATION_BUTTONS)
                .setDefBack(false)
                .setIsAnim(true)
                .setUV(0, 96, 0, 0)
                .setHoverTexts("animation.add.part.hover.only");
        // back color
        addButton(2, workU + 2, workV + 2, "")
                .setSize(8, 8)
                .setColor(GuiNpcAnimation.backColor == 0xFF000000 ?  new Color(0xFF00FFFF).getRGB() :  new Color(0xFF008080).getRGB())
                .setTexture(ANIMATION_BUTTONS)
                .setDefBack(false)
                .setIsAnim(true)
                .setUV(0, 96, 0, 0)
                .setHoverTexts("animation.hover.color");
        // reset scale
        addButton(18, workU + workS - 10, workV + 2, "")
                .setSize(8, 8)
                .setTexture(ANIMATION_BUTTONS)
                .setDefBack(false)
                .setIsAnim(true)
                .setUV(0, 96, 0, 0)
                .setHoverTexts("animation.hover.reset.scale");
        // reset pos
        addButton(19, workU + 2, workV + workS - 10, "")
                .setSize(8, 8)
                .setTexture(ANIMATION_BUTTONS)
                .setDefBack(false)
                .setIsAnim(true)
                .setUV(0, 96, 0, 0)
                .setHoverTexts("animation.hover.reset.pos");
        // reset rot
        addButton(20, workU + workS - 10, workV + workS - 10, "")
                .setSize(8, 8)
                .setTexture(ANIMATION_BUTTONS)
                .setDefBack(false)
                .setIsAnim(true)
                .setUV(0, 96, 0, 0)
                .setHoverTexts("animation.hover.reset.rot");
        // exit
        addButton(66, guiLeft + 5, guiTop + imageHeight - 15, "gui.back")
                .setSize(50, 10)
                .setTexture(ANIMATION_BUTTONS)
                .setDefBack(false)
                .setIsAnim(true)
                .setUV(0, 96, 0, 0)
                .setHoverTexts("hover.back");
        // done
        addButton(65, guiLeft + imageWidth - 54, guiTop + imageHeight - 15, "gui.done")
                .setSize(50, 10)
                .setTexture(ANIMATION_BUTTONS)
                .setDefBack(false)
                .setIsAnim(true)
                .setUV(0, 96, 0, 0)
                .setHoverTexts("hover.save");
        // reset model render
        if (model != null) {
            partRender = new ModelRendererAlt(model, addPart);
            if (typeModel == 0) {
                partRender.objLocationUp = null;
                partRender.objLocationDown = null;
            }
        }
        ModelNpcAlt.loadAnimationModel(animation);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (!hasSubGui() && (mouseButton == 0 || mouseButton == 1) && hovered) {
            mousePressId = mouseButton;
            mousePressX = mouseX;
            mousePressY = mouseY;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void unFocused(GuiTextFieldNop textField) {
        switch (textField.id) {
            case 0: part.name = textField.getValue(); break; // name
            case 1: {
                Component value = Component.literal("" + textField.getInteger());
                boolean found = false;
                for (Component key : dataPartIDs.keySet()) {
                    if (key.getString().equals(value.getString())) {
                        found = true;
                        value = key;
                        break;
                    }
                }
                if (!found) {
                    textField.setValue("" + textField.def);
                    return;
                }
                addPart.parentPart = dataPartIDs.get(value);
                String hover = Component.translatable("info.item.cloner.empty.0").getFormattedText();
                if (parent.frame.parts.containsKey(addPart.parentPart)) { hover = Component.translatable(parent.frame.parts.get(addPart.parentPart).name).getFormattedText(); }
                if (getButton(0) != null) {
                    Component[] variants = getButton(0).getVariants();
                    for (int i = 0; i < variants.length; i++) {
                        if (variants[i].getFormattedText().equals("" + addPart.parentPart)) {
                            getButton(0).setDisplay(i);
                            break;
                        }
                    }
                    getButton(0).setHoverTexts(Component.translatable("animation.add.part.hover.part.ids", hover));
                }
                textField.setHoverTexts(Component.translatable("animation.add.part.hover.part.ids", hover));
                initGui();
                break;
            } // parent part ID
            case 2: {
                String text = textField.getValue();
                if (text.isEmpty()) { addPart.objUp = null; }
                else { addPart.objUp = new ResourceLocation(text); }
                initGui();
                break;
            } // obj up
            case 3: {
                String text = textField.getValue();
                if (text.isEmpty()) { addPart.objDown = null; }
                else { addPart.objDown = new ResourceLocation(text); }
                initGui();
                break;
            } // obj up
            case 4: {
                String text = textField.getValue();
                if (text.isEmpty()) {
                    addPart.location = new ResourceLocation(CustomNpcs.MODID, "textures/entity/humanmale/steve.png");
                    textField.setValue(addPart.location.toString());
                }
                else { addPart.location = new ResourceLocation(text); }
                initGui();
                break;
            } // texture
            case 5: {
                addPart.size[0] = (float) textField.getDouble();
                initGui();
                break;
            } // size x
            case 6: {
                if (typePart == 1) {
                    addPart.size[1] = (float) textField.getDouble();
                } else {
                    float value = (float) textField.getDouble();
                    addPart.size[2] = Math.round(291.666f * value) / 1000.0f;
                    addPart.size[3] = Math.round(250.0f * value) / 1000.0f;
                    addPart.size[1] = value - (addPart.size[2] + addPart.size[3]);
                }
                initGui();
                break;
            } // size y / y0
            case 7: {
                addPart.size[2] = (float) textField.getDouble();
                initGui();
                break;
            } // size y1
            case 8: {
                addPart.size[3] = (float) textField.getDouble();
                initGui();
                break;
            } // size y2
            case 9: {
                addPart.size[4] = (float) textField.getDouble();
                initGui();
                break;
            } // size z
            case 10: {
                addPart.textureU = textField.getInteger();
                initGui();
                break;
            } // texture u
            case 11: {
                addPart.textureV = textField.getInteger();
                initGui();
                break;
            } // texture v
            case 12: {
                addPart.pos[0] = (float) textField.getDouble();
                initGui();
                break;
            } // pos x
            case 13: {
                addPart.pos[1] = (float) textField.getDouble();
                initGui();
                break;
            } // pos y
            case 14: {
                addPart.pos[2] = (float) textField.getDouble();
                initGui();
                break;
            } // pos z
            case 15: {
                addPart.rot[0] = (float) textField.getDouble();
                initGui();
                break;
            } // pos x
            case 16: {
                addPart.rot[1] = (float) textField.getDouble();
                initGui();
                break;
            } // pos y
            case 17: {
                addPart.rot[2] = (float) textField.getDouble();
                initGui();
                break;
            } // pos z
        }
    }

}
