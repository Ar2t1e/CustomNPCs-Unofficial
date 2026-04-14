package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.containers.ContainerDead;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketDeadLootsGet;
import noppes.npcs.packets.server.SPacketDeadLootsOpen;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Vector;

public class GuiNPCDeadInventory extends GuiContainerNPCInterface<ContainerDead>
        implements ICustomScrollListener, IScrollData {

    protected final ContainerDead menu;
    protected GuiCustomScrollNop scroll;
    protected boolean wait = false;

    public GuiNPCDeadInventory(ContainerDead container, Inventory inv, Component ignoredTitle) {
        super(NoppesUtilServer.getEditingNpc(Minecraft.getInstance().player), container, inv, Component.empty());
        setBackground("largebg.png");
        drawDefaultBackground = false;
        imageWidth = 177;
        imageHeight = container.size + 152;

        menu = container;
        Packets.sendServer(new SPacketDeadLootsGet());
    }

    @Override
    public void renderBackground(@Nonnull GuiGraphics graphics) {
        super.renderBackground(graphics);
        if (npc.isAlive()) { onClose(); }
        else {
            PoseStack matrixPose = graphics.pose();
            int size = menu.size - 1;
            matrixPose.pushPose();
            matrixPose.translate(guiLeft, guiTop + 20, 0.0f);
            matrixPose.scale(bgScale, bgScale, bgScale);
            // background
            int w = imageWidth - 4;
            // up
            graphics.blit(background, 0, 0, 0, 0, w, imageHeight - 34);
            int sh = 227;
            int h = 4;
            if (size > 0) {
                sh -= size * 18 - size;
                h += size * 18 - size;
            }
            // down
            graphics.blit(background, 0, imageHeight - 34, 0, sh, w, h);
            // left
            if (player.isCreative()) {
                matrixPose.translate(imageWidth - 5, 0.0f, 0.0f);
                graphics.blit(background, 0, 0, 84, 0, 108, imageHeight - 34);
                graphics.blit(background, 0, imageHeight - 34, 84, sh, 108, h);
                matrixPose.translate(5 - imageWidth, 0.0f, 0.0f);
            } else {
                graphics.blit(background, w, 0, 189, 0, 3, imageHeight - 34);
                graphics.blit(background, w, imageHeight - 34, 189, sh, 3, h);
            }
            // inventory slots
            matrixPose.translate(-1.0f, -21.0f, 0.0f);
            if (size > 0) { matrixPose.translate(0.0f, size * 9.0f, 0.0f); }
            for (Slot slot : menu.slots) { graphics.blit(GuiBasic.RESOURCE_SLOT, slot.x, slot.y, 0, 0, 18, 18); }
            matrixPose.popPose();
            // title
            MutableComponent textComponent = Component.translatable("inv.loot.0", npc.getName());
            if (menu.pos > -1) { textComponent.append(Component.translatable("inv.loot.1", menu.playerParent)); }
            String customTitle = Util.instance.getOldFormattedText(textComponent);
            graphics.drawString(font, customTitle, (width - font.width(customTitle)) / 2, guiTop + 24, CustomNpcResourceListener.DefaultTextColor);
        }
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (wait) { drawWait(graphics); }
        else { super.render(graphics, mouseX, mouseY, partialTicks); }
    }

    @Override
    public void init() {
        super.init();
        int size = menu.size - 1;
        if (size > 0) { guiTop -= size * 9; }
        if (player.isCreative()) {
            if (scroll == null) { scroll = addScroll(0).setSize(100, imageHeight - 50); }
            add(scroll.setPos(guiLeft + imageWidth - 1, guiTop + 35));
            addLabel(0, guiLeft + imageWidth, guiTop + 25, "inv.loot.players");
        }
    }

    @Override
    public void setData(Vector<String> dataList, Map<String, Integer> dataMap) {
        scroll.setList(dataList);
        scroll.setSelectedIndex(menu.playerParent);
    }

    @Override
    public void scrollClicked(GuiCustomScrollNop scroll) {
        if (menu.playerParent.getString().equals(scroll.getSelected())) { return; }
        wait = true;
        Packets.sendServer(new SPacketDeadLootsOpen(scroll.getSelected()));
    }

    @Override
    public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

    @Override
    public void setSelected(String select) { }

}
