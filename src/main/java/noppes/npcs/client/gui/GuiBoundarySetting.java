package noppes.npcs.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.IPos;
import noppes.npcs.client.gui.availability.SubGuiNpcAvailability;
import noppes.npcs.client.gui.select.SubGuiColorSelector;
import noppes.npcs.client.gui.select.SubGuiQuestSelection;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.controllers.BorderController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.Zone3D;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketRegionRemove;
import noppes.npcs.packets.server.SPacketRegionSave;
import noppes.npcs.packets.server.SPacketRegionSetOnItem;
import noppes.npcs.packets.server.SPacketTeleportTo;
import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.shared.client.gui.listeners.GuiSelectionListener;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;
import noppes.npcs.util.Util;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.TreeMap;

public class GuiBoundarySetting extends GuiNPCInterface
        implements ICustomScrollListener, ITextfieldListener, GuiSelectionListener {

    protected final TreeMap<Integer, Component> dataRegions = new TreeMap<>();
    protected final TreeMap<Integer, Component> dataPoints = new TreeMap<>();
    protected GuiCustomScrollNop regions;
    protected GuiCustomScrollNop points;
    protected Point point;
    protected Zone3D region;
    protected int regID;

    public GuiBoundarySetting(BlockPos pos) {
        super();
        setBackground("bgfilled.png");
        imageWidth = 405;
        imageHeight = 216;
        closeOnEsc = true;

        regID = pos.getX();
        region = BorderController.getInstance().getRegion(pos.getX());
        if (region != null && region.points.containsKey(pos.getY())) { point = region.points.get(pos.getY()); }
    }

    @Override
    public void buttonEvent(@Nonnull GuiButtonNop button) {
        switch (button.id) {
            case 0: {
                if (region == null) { return; }
                setSubGui(new SubGuiColorSelector(region.color));
                return;
            } // color
            case 1: {
                if (region == null) { return; }
                setSubGui(new SubGuiNpcAvailability(region.availability, this));
                return;
            } // availability
            case 2: {
                if (region == null) { return; }
                Packets.sendServer(new SPacketRegionRemove(region.getId()));
                region = null;
                point = null;
                break;
            } // del
            case 3: region.offset(-1, 0, 0); break;
            case 4: region.offset(1, 0, 0); break;
            case 5: region.offset(0, 0, -1); break;
            case 6: region.offset(0, 0, 1); break;
            case 7: region.offset(0, -1, 0); break;
            case 8: region.offset(0, 1, 0); break;
            case 9: {
                if (region == null || point == null || dataPoints.size() < 2 || !points.hasSelected()) { return; }
                region.removePoint(region.points.get(points.getSelectedIndex()));
                init();
                break;
            } // remove point
            case 10: {
                if (region == null || point == null) { return; }
                TreeMap<Integer, Point> map = new TreeMap<>();
                int i = 0;
                for (int pos : region.points.keySet()) {
                    Point p = region.points.get(pos);
                    if (p == point || (p.x == point.x && p.y == point.y)) {
                        i = pos;
                        break;
                    }
                }
                int j = 0;
                for (int pos : region.points.keySet()) {
                    if (pos == i) { continue; }
                    if (pos + 1 == i) { map.put(j++, point); }
                    Point p = region.points.get(pos);
                    map.put(j++, p);
                }
                region.points.clear();
                region.points.putAll(map);
                break;
            } // Up Point Pos
            case 11: {
                if (region == null || point == null) { return; }
                TreeMap<Integer, Point> map = new TreeMap<>();
                int i = 0;
                for (int pos : region.points.keySet()) {
                    Point p = region.points.get(pos);
                    if (p == point || (p.x == point.x && p.y == point.y)) {
                        i = pos;
                        break;
                    }
                }
                int j = 0;
                for (int pos : region.points.keySet()) {
                    if (pos == i) { continue; }
                    Point p = region.points.get(pos);
                    map.put(j++, p);
                    if (pos - 1 == i) { map.put(j++, point); }
                }
                region.points.clear();
                region.points.putAll(map);
                break;
            } // Down Point Pos
            case 12: {
                if (point == null) { return; }
                point.x--;
                break;
            } // OffSet Point -X
            case 13: {
                if (point == null) { return; }
                point.x++;
                break;
            } // OffSet Point +X
            case 14: {
                if (point == null) { return; }
                point.y--;
                break;
            } // OffSet Point -Z
            case 15: {
                if (point == null) { return; }
                point.y++;
                break;
            } // OffSet Point +Z
            case 18: {
                if (region == null) { return; }
                region.y[1]++;
                break;
            } // Max Y Up +
            case 19: {
                if (region == null) { return; }
                region.y[1]--;
                break;
            } // Max Y Up -
            case 20: {
                if (region == null) { return; }
                region.y[0]++;
                break;
            } // Max Y Down +
            case 21: {
                if (region == null) { return; }
                region.y[0]--;
                break;
            } // Max Y Down -
            case 24: {
                if (region == null) { return; }
                ResourceKey<Level> dim = ResourceKey.create(Registries.DIMENSION, region.dimension);
                BlockPos pos = region.getCenter().getMCBlockPos();
                if (point != null) { pos = new BlockPos(point.x, region.y[0] + (region.y[1] - region.y[0]) / 2, point.y); }
                Packets.sendServer(new SPacketTeleportTo(dim, pos));
                return;
            } // Teleport to Center
            case 25: {
                if (region == null) { return; }
                region.keepOut = ((GuiCheckBoxNop) button).selected();
                break;
            } // Keep Out Type
            case 26: {
                if (region == null) { return; }
                region.showInClient = ((GuiCheckBoxNop) button).selected();
                break;
            } // Show In Client
            case 27: {
                if (region == null) { return; }
                setSubGui(new SubGuiQuestSelection(region.questID));
                break;
            } // select quest
            case 28: {
                if (region == null) { return; }
                region.questID = 0;
                init();
                break;
            } // remove quest
            case 29: {
                if (region == null) { return; }
                region.questWhenEnter = ((GuiCheckBoxNop) button).selected();
                break;
            } // quest when enter
            case 30: {
                if (region == null) { return; }
                setSubGui(new SubGuiEditText(0, region.message)
                        .setHoverTexts(Component.translatable("region.hover.enter.message")));
                break;
            } // message
        }
        init();
    }

    public static void drawLine(GuiGraphics graphics, float left, float top, float right, float bottom, int color, float wLine) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(wLine);

        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        float alpha = (float) (color >> 24 & 255) / 255.0F;

        Matrix4f matrix4f = graphics.pose().last().pose();
        Matrix3f matrix3f = graphics.pose().last().normal();
        VertexConsumer vertices = graphics.bufferSource().getBuffer(RenderType.lines());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        vertices.vertex(matrix4f, left, top, 0.0f)
                .color(red, green, blue, alpha)
                .normal(matrix3f, 1.0F, 1.0F, 0.0F)
                .endVertex();
        vertices.vertex(matrix4f, right, bottom, 0.0f)
                .color(red, green, blue, alpha)
                .normal(matrix3f, -1.0F, -1.0F, 0.0F)
                .endVertex();

        RenderSystem.disableBlend();
    }

    private void drawRegion(GuiGraphics graphics, int work, float mu, float mv, float su, float sv, float sy) {
        if (region == null) { return; }
        float u0;
        float u1;
        float v0;
        float v1;
        for (int i = -1; i < region.points.size() - 1; i++) {
            Point p0;
            if (i == -1) { p0 = region.points.get(region.points.size() - 1); }
            else { p0 = region.points.get(i); }
            Point p1 = region.points.get(i + 1);
            if (p0 == null || p1 == null) { continue; }
            u0 = (p0.x - mu) * su;
            v0 = (p0.y - mv) * sv;
            u1 = (p1.x - mu) * su;
            v1 = (p1.y - mv) * sv;
            drawLine(graphics, u0, v0, u1, v1, 0xFF0000FF, 2.0f);
        }
        v0 = (Math.min(region.y[1], 255)) * sy;
        v1 = (Math.max(region.y[0], 0)) * sy;
        drawLine(graphics, work + 12, v0, work + 13, v1, 0xFF0000FF, 2.0f);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (hasSubGui()) { return; }
        int side = 160;
        int work = side - 12;
        int wu = guiLeft + 131, wv = guiTop + 18;
        String ht = "";
        if (isMouseHover(mouseX, mouseY, wu, wv, side - 2, side)) { ht = "region.hover.work.0"; }
        else if (isMouseHover(mouseX, mouseY, wu + side - 2, wv, 12, side)) { ht = "region.hover.work.1"; }

        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(wu, wv, 0.5f);
        int color = new Color(0xA0000000).getRGB();
        graphics.fill(0, 0, side + 12, side, color); // Main Place
        graphics.fill(6, 6, side - 6, side - 6, color); // Work Place
        graphics.fill(side - 2, 6, side + 9, side - 6, color); // Height place
        matrixStack.translate(6, 6, 0.0d);
        if (region == null) { matrixStack.popPose(); return; }
        float mu = region.getMinX();
        float mv = region.getMinZ();
        float nu = region.getMaxX();
        float nv = region.getMaxZ();
        float su = (float) (work) / (nu - mu);
        float sv = (float) (work) / (nv - mv);
        float sy = (float) (work) / 255.0f;
        // Selected InSide Blue
        if (region.size() >= 3) { drawRegion(graphics, work, mu, mv, su, sv, sy); }
        int hx, hz;
        Point p = null;
        if (region.dimension.equals(player.level().dimension().location())) { p = region.points.get(region.getIdNearestPoint(player.blockPosition())); }
        // Nearest Point Green
        if (p != null) {
            hx = (int) (((double) p.x - mu) * su);
            hz = (int) (((double) p.y - mv) * sv);
            int d = ((point != null && point.x == p.x && point.y == p.y) ? 1 : 0);
            graphics.fill(hx - d - 1, hz - d - 1, hx + d + 1, hz + d + 1, new Color(0xFF00FF00).getRGB());
        }
        // Current Point
        if (point != null) {
            hx = (int) ((point.x - mu) * su);
            hz = (int) ((point.y - mv) * sv);
            graphics.fill(hx - 1, hz - 1, hx + 1, hz + 1, new Color(0xFFFFFF00).getRGB());
        }
        if (region.getHomePos() != null) {
            hx = (int) ((region.getHomePos().getX() - mu) * su);
            hz = (int) ((region.getHomePos().getZ() - mv) * sv);
            if (hx < 0) { hx = 0; }
            else if (hx > work) { hx = work; }
            if (hz < 0) { hz = 0; }
            else if (hz > work) { hz = work; }
            graphics.fill(hx - 1, hz - 1, hx, hz, new Color(0xFFFF0000).getRGB());
        }
        // Center
        IPos c = region.getCenter();
        hx = (int) ((c.getX() - mu) * su);
        hz = (int) ((c.getZ() - mv) * sv);
        graphics.fill(hx - 1, hz - 1, hx + 1, hz + 1, new Color(0xFF0000FF).getRGB());
        // Player Position
        color = new Color(0xFFFFFFFF).getRGB();
        int xp = player.blockPosition().getX();
        if (xp < mu) {
            xp = (int) mu;
            color = new Color(0xA0FFFFFF).getRGB();
        }
        else if (xp > nu) {
            xp = (int) nu;
            color = new Color(0xA0FFFFFF).getRGB();
        }
        hx = (int) ((xp - mu) * su);
        if (hx < 0) {
            hx = 0;
            color = new Color(0xF0FFFFFF).getRGB();
        }
        else if (hx > work) {
            hx = work;
            color = new Color(0xF0FFFFFF).getRGB();
        }
        int zp = player.blockPosition().getZ();
        if (zp < mv) {
            zp = (int) mv;
            color = new Color(0xF0FFFFFF).getRGB();
        }
        else if (zp > nv) {
            zp = (int) nv;
            color = new Color(0xF0FFFFFF).getRGB();
        }
        hz = (int) ((zp - mv) * sv);
        int d = (point != null && point.x == player.blockPosition().getX() && point.y == player.blockPosition().getZ() ? 1 : 0) + 1;
        if (p != null && p.x == player.blockPosition().getX() && p.y == player.blockPosition().getZ()) { d++; }
        if (c.getX() == player.blockPosition().getX() && c.getZ() == player.blockPosition().getZ()) { d++; }
        graphics.fill(hx - d - 1, hz - d - 1, hx + d + 1, hz + d + 1, color); // XZ
        int hy = (int) (player.blockPosition().getY() * sy);
        graphics.fill(side - 6, side - hy - 13, side - 4, side - hy - 11, new Color(0xFFFFFFFF).getRGB()); // Y
        matrixStack.popPose();
        if (!CustomNpcs.ShowDescriptions) { return; }
        if (!ht.isEmpty()) { drawHoverText(ht); }
    }

    @Override
    public void init() {
        super.init();
        dataRegions.clear();
        dataPoints.clear();
        Component selectReg = Component.empty();
        Component selectP = Component.empty();
        int side = 186;
        int r0 = guiLeft + 118;
        int r1 = guiLeft + side + 119;
        int h0 = guiTop + 109;
        int lId = 0;
        if (region != null && !BorderController.getInstance().regions.containsKey(region.getId())) {
            region = null;
            point = null;
        }
        if (region != null && point != null && !region.contains(point.x, point.y)) {
            point = null;
            if (!region.points.isEmpty()) { point = region.points.get(0); }
        }
        for (Zone3D reg : BorderController.getInstance().regions.values()) {
            dataRegions.put(reg.getId(), Component.literal(reg.toString()));
            if (regID == reg.getId()) {
                region = reg;
                selectReg = dataRegions.get(reg.getId());
                for (int id : reg.points.keySet()) {
                    dataPoints.put(id, Component.literal("ID: " + id + " [" + reg.points.get(id).x + ", " + reg.points.get(id).y + "]"));
                    if (point != null && (point == reg.points.get(id) || (point.x == reg.points.get(id).x && point.y == reg.points.get(id).y))) {
                        selectP = dataPoints.get(id);
                    }
                }
            }
        }
        if (regions == null) { regions = addScroll(0).setSize(110, 130); }
        regions.setUnsortedList(new ArrayList<>(dataRegions.values()));
        if (!selectReg.getString().isEmpty()) { regions.setSelected(selectReg); }
        add(regions.setPos(guiLeft + 5, guiTop + 14));
        // regions
        addLabel(lId++, guiLeft + 6, guiTop + 4, "gui.regions")
                .setSize(108, 12)
                .setHoverTexts(Component.translatable("region.hover.regions.list", Component.translatable("item.customnpcs.npcboundary").getString()));
        if (points == null) { points = addScroll(1).setSize(imageWidth - side - 124, side / 2); }
        points.setUnsortedList(new ArrayList<>(dataPoints.values()));
        if (!selectP.getString().isEmpty()) { points.setSelected(selectP); }
        add(points.setPos(r1, guiTop + 14));
        // points
        addLabel(lId++, r1, guiTop + 4, "gui.points")
                .setSize(imageWidth - side - 126, 12)
                .setHoverTexts(Component.translatable("region.hover.points.list", Component.translatable("item.customnpcs.npcboundary").getString()));
        // ID 0 - color
        String color = "gui.color";
        if (region != null) {
            StringBuilder c = new StringBuilder(Integer.toHexString(region.color));
            while (c.length() < 6) { c.insert(0, "0"); }
            color = c.toString();
        }
        addButton(0, guiLeft + 5, guiTop + 162, color)
                .setSize(60, 13)
                .setIsEnabled(region != null)
                .setColor(region != null ? region.color : 0)
                .setHoverTexts("region.hover.color");
        // ID 1 - Available
        addButton(1, guiLeft + 5, guiTop + 147, "availability.available")
                .setSize(110, 13)
                .setIsEnabled(region != null)
                .setHoverTexts("availability.hover");
        addButton(2, guiLeft + 67, guiTop + 162, "gui.remove")
                .setSize(48, 13)
                .setIsEnabled(region != null)
                .setHoverTexts("hover.delete");
        // ID 3 - OffSet -X
        String trRegion = Component.translatable("gui.region").getString();
        addButton(3, r0 + 13, guiTop + 3, "←")
                .setSize(13, 13)
                .setIsEnabled(region != null)
                .setHoverTexts(Component.translatable("region.hover.offset.-x", trRegion));
        // ID 4 - OffSet +X
        addButton(4, r0 + 27, guiTop + 3, "→")
                .setSize(13, 13)
                .setIsEnabled(region != null)
                .setHoverTexts(Component.translatable("region.hover.offset.+x", trRegion));
        // ID 5 - OffSet -Z
        addButton(5, r0 - 1, guiTop + 18, "↑")
                .setSize(13, 13)
                .setIsEnabled(region != null)
                .setHoverTexts(Component.translatable("region.hover.offset.-z", trRegion));
        // ID 6 - OffSet +Z
        addButton(6, r0 - 1, guiTop + 32, "↓")
                .setSize(13, 13)
                .setIsEnabled(region != null)
                .setHoverTexts(Component.translatable("region.hover.offset.+z", trRegion));
        // ID 7 - OffSet -Y
        addButton(7, r0 - 1, guiTop + side - 21, "↓")
                .setSize(13, 13)
                .setIsEnabled(region != null)
                .setHoverTexts(Component.translatable("region.hover.offset.-y", trRegion));
        // ID 8 - OffSet +Y
        addButton(8, r0 - 1, guiTop + side - 35, "↑")
                .setSize(13, 13)
                .setIsEnabled(region != null)
                .setHoverTexts(Component.translatable("region.hover.offset.+y", trRegion));
        // ID 10 - Up Point Pos
        addButton(10, r1, h0, "˄")
                .setSize(39, 13)
                .setIsEnabled(region != null)
                .setHoverTexts(Component.translatable("region.hover.point.offset.up", trRegion));
        // ID 11 - Down Point Pos
        addButton(11, r1 + 41, h0, "˅")
                .setSize(39, 13)
                .setIsEnabled(region != null && point != null)
                .setHoverTexts(Component.translatable("region.hover.point.offset.down", trRegion));
        // ID 9 - remove point
        addButton(9, r1 + 82, h0, "X")
                .setSize(13, 13)
                .setIsEnabled(region != null && point != null && dataPoints.size() > 1 && points.hasSelected())
                .setHoverTexts(Component.translatable("region.hover.point.remove", trRegion));

        // ID 12 - OffSet Point -X
        String trPoint = Component.translatable("gui.point").getString();
        addButton(12, r1, h0 + 25, "←")
                .setSize(12, 12)
                .setIsEnabled(region != null && point != null)
                .setHoverTexts(Component.translatable("region.hover.offset.-x", trPoint));
        // ID 13 - OffSet Point +X
        addButton(13, r1 + 22, h0 + 25, "→")
                .setSize(12, 12)
                .setIsEnabled(region != null && point != null)
                .setHoverTexts(Component.translatable("region.hover.offset.+x", trPoint));
        // ID 14 - OffSet Point -Z
        addButton(14, r1 + 11, h0 + 14, "↑")
                .setSize(12, 12)
                .setIsEnabled(region != null && point != null)
                .setHoverTexts(Component.translatable("region.hover.offset.-z", trPoint));
        // ID 15 - OffSet Point +Z
        addButton(15, r1 + 11, h0 + 36, "↓")
                .setSize(12, 12)
                .setIsEnabled(region != null && point != null)
                .setHoverTexts(Component.translatable("region.hover.offset.+z", trPoint));

        // ID 18 - Max Y Up -
        addButton(18, r1, h0 + 50, "↑")
                .setSize(12, 12)
                .setIsEnabled(region != null && point != null)
                .setHoverTexts(Component.translatable("region.hover.offset.up.-y", trPoint));
        // ID 19 - Max Y Up +
        addButton(19, r1, h0 + 62, "↓")
                .setSize(12, 12)
                .setIsEnabled(region != null && point != null)
                .setHoverTexts(Component.translatable("region.hover.offset.up.+y", trPoint));
        // ID 20 - Max Y Down -
        addButton(20, r1 + 49, h0 + 50, "↑")
                .setSize(12, 12)
                .setIsEnabled(region != null && point != null)
                .setHoverTexts(Component.translatable("region.hover.offset.down.-y", trPoint));
        // ID 21 - Max Y Down +
        addButton(21, r1 + 49, h0 + 62, "↓")
                .setSize(12, 12)
                .setIsEnabled(region != null && point != null)
                .setHoverTexts(Component.translatable("region.hover.offset.down.+y", trPoint));

        // ID 24 - Teleport to Center
        addButton(24, r1 + 74, h0 + 23, "TP")
                .setSize(16, 16)
                .setIsEnabled(region != null && point != null)
                .setHoverTexts("hover.teleport", trPoint);
        // ID 25 - Keep Out Type
        addCheckBox(25, guiLeft + 5, guiTop + side + 5, "region.keepout.true", "region.keepout.false" , region != null && region.keepOut)
                .setSize(110, 10)
                .setIsEnabled(region != null)
                .setHoverTexts("region.hover.keepout");
        // ID 26 - Keep Out Type
        addCheckBox(26, guiLeft + 5, guiTop + side + 16, "region.show.in.client.true", "region.show.in.client.false", region != null && region.showInClient)
                .setSize(110, 10)
                .setIsEnabled(region != null)
                .setHoverTexts("region.hover.show.in.client");
        // ID 27 - quest id
        Component q = Component.translatable("quest.next");
        if (region != null && region.questID > 0) {
            Quest quest = QuestController.instance.quests.get(region.questID);
            q = Component.translatable("gui.quest", ": " + (quest != null ? Util.instance.getOldFormattedText(Component.translatable(quest.getName())) : ""));
        }
        addButton(27, r1, guiTop + side - 1, q)
                .setSize(79, 14)
                .setIsEnabled(region != null)
                .setHoverTexts("region.hover.quest.id");
        // ID 28 - remove quest
        addButton(28, r1 + 81, guiTop + side - 1, "X")
                .setSize(14, 14)
                .setIsEnabled(region != null && region.questID > 0)
                .setHoverTexts(Component.translatable("region.hover.quest.remove", trRegion));
        // ID 29 - quest when enter
        addCheckBox(29, r1, guiTop + side + 16, "region.quest.when.enter.true", "region.quest.when.enter.false", region != null && region.questWhenEnter)
                .setSize(95, 10)
                .setIsEnabled(region != null)
                .setHoverTexts("region.hover.quest.when.enter");
        // ID 30 - message
        addButton(30, r0 + 139, guiTop + side + 11, "gui.message")
                .setSize(46, 13)
                .setIsEnabled(region != null)
                .setHoverTexts(Component.translatable("region.hover.enter.message"),
                        region != null ? Component.literal("\"" + region.message + "\":") : null,
                        region != null ? Component.translatable(region.message) : null);
        // TextFields
        // X Point pos
        addTextField(16, r1 + 38, h0 + 16, 31, 12, "" + (point != null ? point.x : 0))
                .setMinMaxDefault(Integer.MIN_VALUE, Integer.MAX_VALUE, point != null ? point.x : 0)
                .setEditableIn(region != null && point != null)
                .setHoverTexts("X pos");
        // ID 17 - Z Point pos
        addTextField(1, r1 + 38, h0 + 34, 31, 12, "" + (point != null ? point.y : 0))
                .setMinMaxDefault(Integer.MIN_VALUE, Integer.MAX_VALUE, point != null ? point.y : 0)
                .setEditableIn(region != null && point != null)
                .setHoverTexts("Z pos");
        // ID 22 - Max Y
        addTextField(22, r1 + 14, h0 + 56, 31, 12, "" + (region != null ? region.y[1] : 0))
                .setMinMaxDefault(Integer.MIN_VALUE, Integer.MAX_VALUE, region != null ? region.y[1] : 0)
                .setEditableIn(region != null && point != null)
                .setHoverTexts("max Y");
        // ID 23 - Min Y
        addTextField(23, r1 + 63, h0 + 56, 31, 12, "" + (region != null ? region.y[0] : 0))
                .setMinMaxDefault(Integer.MIN_VALUE, Integer.MAX_VALUE, region != null ? region.y[0] : 0)
                .setEditableIn(region != null && point != null)
                .setHoverTexts("min Y");
        // ID 24 - Name
        addTextField(24, guiLeft + 5, guiTop + 177, 110, 13, region != null ? region.name : "")
                .setEditableIn(region != null && point != null)
                .setHoverTexts("region.hover.name");
        // ID 25 - Home X
        addTextField(25, r0 + 30, guiTop + side + 11, 35, 13, "" + (region != null ? region.getHomePos().getX() : ""))
                .setEditableIn(region != null && point != null)
                .setHoverTexts(Component.translatable("region.hover.home.axis", "X"));
        // ID 26 - Home Y
        addTextField(26, r0 + 66, guiTop + side + 11, 35, 13, "" + (region != null ? region.getHomePos().getY() : ""))
                .setEditableIn(region != null && point != null)
                .setHoverTexts(Component.translatable("region.hover.home.axis", "Y"));
        // ID 27 - Home Z
        addTextField(27, r0 + 102, guiTop + side + 11, 35, 13, "" + (region != null ? region.getHomePos().getZ() : ""))
                .setEditableIn(region != null && point != null)
                .setHoverTexts(Component.translatable("region.hover.home.axis", "Z"));
        // Labels
        // ID 99 - Home Pos
        addLabel(lId++, r0, guiTop + side + 12, "Home:").setSize(28, 12);
        // ID 100 - Min XZ Pos
        addLabel(lId++, r0 + 42, guiTop + 6, "MinXZ: [" + (region == null ? "0, 0" : region.getMinX() + "," + region.getMinZ()) + "]").setSize(70, 12);
        // ID 101 - Max XZ Pos
        addLabel(lId++, r0, guiTop + side - 3, "MaxXZ: [" + (region == null ? "0, 0" : region.getMaxX() + "," + region.getMaxZ()) + "]")
                .setSize(92, 12);
        // ID 102 - Min/Max Y Pos
        String text = "Min/Max Y: [" + (region == null ? "0, 0" : region.y[0] + "," + region.y[1]) + "]";
        addLabel(lId++, r0 + 92, guiTop + side - 3, text).setSize(92, 12);
        text = "(worldID: " + (region == null ? "N/A" : region.dimension.toString()) + ")";
        addLabel(lId, r0 + 115, guiTop + 6, text).setSize(70, 12);
    }

    @Override
    public void save() {
        if (region != null) {
            CompoundTag regionNbt = new CompoundTag();
            region.save(regionNbt);
            Packets.sendServer(new SPacketRegionSave(regionNbt));
        }
    }

    @Override
    public void scrollClicked(GuiCustomScrollNop scroll) {
        switch (scroll.id) {
            case 0: { // Region List
                if (!dataRegions.containsValue(scroll.getNormalSelected())) { return; }
                for (int id : dataRegions.keySet()) {
                    if (region != null && region.getId() == id) { continue; }
                    if (dataRegions.get(id).getString().equals(scroll.getSelected()) && BorderController.getInstance().regions.containsKey(id)) {
                        region = BorderController.getInstance().getRegion(id);
                        regID = id;
                        point = null;
                        if (!region.points.isEmpty()) { point = region.points.get(0); }
                        Packets.sendServer(new SPacketRegionSetOnItem(id));
                        init();
                        break;
                    }
                }
                break;
            }
            case 1: { // Point List
                if (region == null || !dataPoints.containsValue(scroll.getNormalSelected())) { return; }
                for (int id : dataPoints.keySet()) {
                    if (dataPoints.get(id).getString().equals(scroll.getSelected()) && region.points.containsKey(id)) {
                        point = region.points.get(id);
                        init();
                        break;
                    }
                }
                break;
            }
        }
    }

    @Override
    public void scrollDoubleClicked(GuiCustomScrollNop scroll) {
        switch (scroll.id) {
            case 0: {
                if (region != null) {
                    IPos pos = region.getCenter();
                    Packets.sendServer(new SPacketTeleportTo(ResourceKey.create(Registries.DIMENSION, region.dimension), pos.getMCBlockPos()));
                    Packets.sendServer(new SPacketRegionSetOnItem(region.getId()));
                    onClose();
                }
                break;
            } // Region List
            case 1: {
                if (region != null && point != null) {
                    Packets.sendServer(new SPacketTeleportTo(ResourceKey.create(Registries.DIMENSION, region.dimension),
                            new BlockPos(point.x, region.y[0] + (region.y[1] - region.y[0]) / 2, point.y)));
                    Packets.sendServer(new SPacketRegionSetOnItem(region.getId()));
                    onClose();
                }
                break;
            } // Point List
        }
    }

    @Override
    public void subGuiClosed(Screen subgui) {
        if (region != null) {
            if (subgui instanceof SubGuiColorSelector gui) { region.color = gui.color; }
            if (subgui instanceof SubGuiEditText gui) { region.message = gui.text[0]; }
        }
        init();
    }

    @Override
    public void selected(int id, String name) {
        region.questID = id;
        init();
    }

    @Override
    public void unFocused(GuiTextFieldNop textField) {
        if (textField.getValue().isEmpty()) { return; }
        switch (textField.id) {
            case 16: {
                if (point == null || !textField.isInteger()) { return; }
                point.x = textField.getInteger();
                init();
                break;
            } // X Point pos
            case 17: {
                if (point == null || !textField.isInteger()) { return; }
                point.y = textField.getInteger();
                init();
                break;
            } // Z Point pos
            case 22: {
                if (region == null || !textField.isInteger()) { return; }
                region.y[1] = textField.getInteger();
                init();
                break;
            } // Y max
            case 23: {
                if (region == null || !textField.isInteger()) { return; }
                region.y[0] = textField.getInteger();
                init();
                break;
            } // Y min
            case 24: {
                if (region == null) { return; }
                region.name = textField.getValue();
                break;
            } // Name
            case 25: {
                if (region == null || !textField.isInteger()) { return; }
                IPos pos = region.getHomePos();
                region.setHomePos(textField.getInteger(), (int) pos.getY(), (int) pos.getZ());
                break;
            } // Home X
            case 26: {
                if (region == null || !textField.isInteger()) { return; }
                IPos pos = region.getHomePos();
                region.setHomePos((int) pos.getX(), textField.getInteger(), (int) pos.getZ());
                break;
            } // Home Y
            case 27: {
                if (region == null || !textField.isInteger()) { return; }
                IPos pos = region.getHomePos();
                region.setHomePos((int) pos.getX(), (int) pos.getY(), textField.getInteger());
                break;
            } // Home Z
        }
    }

}
