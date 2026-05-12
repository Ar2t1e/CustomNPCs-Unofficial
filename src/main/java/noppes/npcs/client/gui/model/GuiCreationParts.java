package noppes.npcs.client.gui.model;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.wrapper.gui.CustomGuiEntityDisplayWrapper;
import noppes.npcs.client.parts.MpmPart;
import noppes.npcs.client.parts.MpmPartReader;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.mixin.client.IMouseHandlerMixin;
import noppes.npcs.shared.client.gui.GuiModelPart;
import noppes.npcs.shared.client.gui.components.*;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.common.util.NaturalOrderComparator;
import noppes.npcs.util.ValueUtil;

import java.util.*;

public class GuiCreationParts extends GuiCreationScreenInterface implements ICustomScrollListener {

    protected static final ResourceLocation background = new ResourceLocation(CustomNpcs.MODID, "textures/gui/bgfilled.png");
    public static final ResourceLocation resource = new ResourceLocation(CustomNpcs.MODID, "textures/gui/misc.png");
    protected static final List<Component> partData = new ArrayList<>();
    public static Component selectedPart = Component.empty();

    protected GuiCustomScrollNop scroll;

    protected final Map<Component, String> dataParts = new HashMap<>();
    protected final Map<Integer, GuiModelPart> guiParts = new HashMap<>();

    protected final int w = 158;
    protected boolean isScrolling = false;
    protected boolean mouseInList = false;
    protected int scrollY = 0;
    protected int maxScrollY;
    protected int scrollHeight = 0;
    protected int listHeight = 0;
    protected int h;
    protected int hover;

    public GuiCreationParts(EntityCustomNpc npc) {
        super(npc);
        active = 2;
        if (minecraft == null) { minecraft = Minecraft.getInstance(); }
        CustomGuiEntityDisplayWrapper wrapper = new CustomGuiEntityDisplayWrapper(-2, npc.wrappedNPC, 106, 90);
        wrapper.setSize(68, 90);

        partData.clear();
        String[] menus = MpmPartReader.PARTS.values().stream().map((p) -> p.menu).sorted(new NaturalOrderComparator()).distinct().toArray(String[]::new);
        for (String part : menus) {
            Component component = switch (part) {
                case "gui.headwear" -> Component.translatable("part.head");
                case "part.buildin" -> Component.translatable("part.eyes");
                default -> Component.translatable(part);
            };
            dataParts.put(component, part);
            partData.add(component);
        }
        if (selectedPart.getString().isEmpty()) { selectedPart = partData.get(0); }
    }

    @Override
    public void buttonEvent(GuiButtonNop button) {
        // un Focus
        for (int id : guiParts.keySet()) {
            if (id == hover) { continue; }
            for (int i = 0; i < 4; i++) {
                if (guiParts.get(id).getButton(i) == null) { continue; }
                guiParts.get(id).getButton(i).setFocused(false);
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (minecraft == null) { minecraft = Minecraft.getInstance(); }
        for (int id : guiParts.keySet()) {
            boolean bo = ((GuiCheckBoxNop) guiParts.get(id).getButton(0)).selected();
            for (int i = 1; i < 4; i++) {
                if (guiParts.get(id).getButton(0) == null || guiParts.get(id).getButton(i) == null) { continue; }
                guiParts.get(id).getButton(i).setIsVisible(bo);
            }
        }
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (hasSubGui()) { return; }
        // background
        int w0 = w / 2;
        int w1 = w - w0;
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(guiLeft + 123.0f, 3.0f, 0.0f);
        matrixStack.scale(bgScale, bgScale, bgScale);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int maxRow = ValueUtil.correctInt((int) Math.ceil((float) h / 248.0f), 2, 10);
        int tileHeight = h / maxRow;
        int lastTileHeight = h - tileHeight * (maxRow - 1);
        int vOffset = (248 - tileHeight) / 2;
        int vMax = 256 - lastTileHeight;
        for (int col = 0; col < 2; ++col) {
            for (int row = 0; row < maxRow; ++row) {
                graphics.blit(background,
                        col * w0,
                        row * tileHeight,
                        col == 0 ? 0 : 256 - w1,
                        row == 0 ? 0 : row == maxRow - 1 ? vMax : vOffset,
                        col == 0 ? w0 : w1,
                        row == maxRow - 1 ? lastTileHeight : tileHeight);
            }
        }
        matrixStack.popPose();
        // Scroll
        mouseInList = mouseX >= guiLeft + 127 && mouseX <= guiLeft + 118 + w && mouseY >= 7 && mouseY <= h - 2;
        hover = getMouseOver(mouseX, mouseY);
        if (scrollHeight < h - 2) {
            if (isScrolling) {
                isScrolling = ((IMouseHandlerMixin) Minecraft.getInstance().mouseHandler).getActiveButton() == 0;
                if (isScrolling) {
                    scrollY = (mouseY - 5) * listHeight / (height - 2) - scrollHeight;
                    if (scrollY < 0) { scrollY = 0; }
                    if (scrollY > maxScrollY) { scrollY = maxScrollY; }
                }
            }
            double x = mouseX - guiLeft;
            double y = mouseY - guiTop;
            float color = isScrolling ? 0.5f : x >= width - 10 && x < width - 1 && y >= 1 && y < height - 2 ? 0.75f : 1.0f;
            drawScrollBar(graphics, color);
        }
        // Parts:
        graphics.enableScissor(guiLeft + 127, 7, guiLeft + 119 + w0 + w1, h - 1);
        for (GuiModelPart guiPart : guiParts.values()) {
            guiPart.height = height;
            guiPart.render(scrollY, hover, graphics, mouseX, mouseY, partialTicks);
        }
        graphics.disableScissor();
    }

    private void drawScrollBar(GuiGraphics graphics, float color) {
        RenderSystem.setShaderTexture(0, resource);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0F);
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(guiLeft + 267, 7, 0.0f);
        int h0 = (h - 8) / 2;
        int h1 = (h - 8) - h0;
        graphics.blit(resource, 0, 0, 0, 0, 10, h0);
        graphics.blit(resource, 0, h0, 0, 256 - h1, 10, h1);
        matrixStack.popPose();

        h0 = (scrollHeight - 2) / 2;
        h1 = scrollHeight - h0;

        matrixStack.pushPose();
        matrixStack.translate(guiLeft + 268, 7 + (int) ((float) scrollY / (float) listHeight * (float)(h - 10)) + 1.0f, 0.0f);
        RenderSystem.setShaderColor(color, color, color, 1.0F);
        graphics.blit(resource, 0, 0, 10, 0, 8, h0);
        graphics.blit(resource, 0, h0, 10, 256 - h1, 8, h1);
        matrixStack.popPose();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0F);
    }

    @Override
    public void init() {
        super.init();
        if (scroll == null) { scroll = addScroll(0); }
        add(scroll.setUnsortedList(partData)
                .setSelected(selectedPart)
                .setPos(guiLeft, guiTop + 46)
                .setSize(120, imageHeight - 50)
                .disabledSearch());
        if (!dataParts.containsKey(selectedPart)) { return; }
        // mini gui parts
        guiParts.clear();
        String category = dataParts.get(selectedPart);
        List<MpmPart> list = MpmPartReader.PARTS.values().stream().sorted(Comparator.comparing((mpmPart) -> mpmPart.id)).filter((mpmPart) -> mpmPart.menu.equals(category) && mpmPart.parentId == null).toList();
        for (int i = 0; i < list.size(); i++) {
            GuiModelPart gui = new GuiModelPart(this, i, guiLeft + 127.0f, list.get(i));
            gui.init();
            guiParts.put(i, gui);
        }
        // Scrolled
        listHeight = (int) (70.0 * Math.ceil((double) list.size() / 2.0));
        h = height - 14;
        if (listHeight > 0) { scrollHeight = (int)(((double) h - 7.5) / (double) listHeight * ((double) h - 7.5)); }
        else { scrollHeight = Integer.MAX_VALUE; }
        maxScrollY = listHeight - h + 6;
        if (maxScrollY > 0 && scrollY > maxScrollY || maxScrollY <= 0 && scrollY > scrollHeight) { scrollY = 0; }
    }

    @Override
    public void scrollClicked(GuiCustomScrollNop scroll) {
        if (!partData.contains(scroll.getNormalSelected()) || selectedPart.getString().equals(scroll.getSelected())) { return; }
        selectedPart = scroll.getNormalSelected();
        scrollY = 0;
        init();
    }

    @Override
    public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

    private int getMouseOver(int mouseX, int mouseY) {
        mouseX -= guiLeft + 127;
        mouseY -= 7;
        if (mouseInList) {
            for(int index = 0; index < guiParts.size(); ++index) {
                if (mouseInOption(mouseX, mouseY, index)) { return index; }
            }
        }
        return -1;
    }

    public boolean mouseInOption(int mouseX, int mouseY, int index) {
        int posX = index % 2 == 0 ? 0 : 70;
        int posY = 70 * (int) Math.floor((double) index / 2.0) - scrollY;
        return mouseX >= posX && mouseX < posX + 68 && mouseY >= posY && mouseY < posY + 68;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (scrollHeight < h - 2) {
            double x = mouseX - guiLeft - 110 - w;
            double y = mouseY - 8;
            isScrolling = x >= 0 && x < 9 && y >= 0 && y < h - 10;
            if (isScrolling) { return true; }
        }
        if (mouseButton != 0) { return false; }
        if (!hasSubGui() && guiParts.containsKey(hover) && guiParts.get(hover).mouseClicked(mouseX, mouseY, mouseButton)) { return true; }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double mouseScrolled) {
        if (!hasSubGui() && mouseScrolled != 0.0D && mouseInList) {
            scrollY += mouseScrolled > 0.0D ? -14 : 14;
            if (scrollY > maxScrollY) { scrollY = maxScrollY; }
            if (scrollY < 0) { scrollY = 0; }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, mouseScrolled);
    }

}
