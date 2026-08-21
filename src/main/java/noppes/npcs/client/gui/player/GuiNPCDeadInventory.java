package noppes.npcs.client.gui.player;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.containers.ContainerDead;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketDeadLootsGet;
import noppes.npcs.packets.server.SPacketDeadLootsOpen;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IScrollData;

import java.util.Map;
import java.util.Vector;

public class GuiNPCDeadInventory extends GuiContainerNPCInterface<ContainerDead>
        implements ICustomScrollListener, IScrollData {

    protected final ContainerDead menu;
    protected GuiCustomScrollNop scroll;
    protected boolean wait = false;

    public GuiNPCDeadInventory(EntityNPCInterface npc, ContainerDead container) {
        super(npc, container, Component.empty());
        setBackground("largebg.png");
        drawDefaultBackground = false;
        xSize = 177;
        ySize = container.size + 152;

        menu = container;
        Packets.sendServer(new SPacketDeadLootsGet());
    }

    @Override
    public void drawDefaultBackground() {
        if (npc.isEntityAlive()) { onClose(); }
        int size = menu.size - 1;
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiLeft, guiTop + 20, 0.0f);
        GlStateManager.scale(bgScale, bgScale, bgScale);
        // background
        minecraft.getTextureManager().bindTexture(background);
        int w = xSize - 4;
        // up
        drawTexturedModalRect(0, 0, 0, 0, w, ySize - 34);
        int sh = 227;
        int h = 4;
        if (size > 0) {
            sh -= size * 18 - size;
            h += size * 18 - size;
        }
        // down
        drawTexturedModalRect(0, ySize - 34, 0, sh, w, h);
        // left
        if (player.isCreative()) {
            GlStateManager.translate(xSize - 5, 0.0f, 0.0f);
            drawTexturedModalRect(0, 0, 84, 0, 108, ySize - 34);
            drawTexturedModalRect(0, ySize - 34, 84, sh, 108, h);
            GlStateManager.translate(5 - xSize, 0.0f, 0.0f);
        } else {
            drawTexturedModalRect(w, 0, 189, 0, 3, ySize - 34);
            drawTexturedModalRect(w, ySize - 34, 189, sh, 3, h);
        }
        // inventory slots
        minecraft.getTextureManager().bindTexture(GuiBasic.RESOURCE_SLOT);
        GlStateManager.translate(-1.0f, -21.0f, 0.0f);
        if (size > 0) { GlStateManager.translate(0.0f, size * 9.0f, 0.0f); }
        for (Slot slot : menu.inventorySlots) { drawTexturedModalRect(slot.xPos, slot.yPos, 0, 0, 18, 18); }
        GlStateManager.popMatrix();
        // title
        GuiLabel label = getLabel(0);
        if (label != null) {
            Component textComponent = Component.translatable("inv.loot.0", npc.getName());
            if (menu.pos > -1) { textComponent.append(Component.translatable("inv.loot.1", menu.playerParent)); }
            label.setMessage(textComponent);
            label.setSize(xSize - 16, 10);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (wait) { drawWait(); }
        else { super.drawScreen(mouseX, mouseY, partialTicks); }
    }

    @Override
    public void initGui() {
        super.initGui();
        int size = menu.size - 1;
        if (size > 0) { guiTop -= size * 9; }
        addLabel(0, guiLeft + 8, guiTop + 24, "inv.loot.players")
                .setSize(xSize - 16, 10)
                .setColor(CustomNpcResourceListener.DefaultTextColor);
        if (player.isCreative()) {
            if (scroll == null) { scroll = addScroll(0).setSize(100, ySize - 50); }
            add(scroll.setPos(guiLeft + xSize - 1, guiTop + 35));
            addLabel(1, guiLeft + xSize, guiTop + 24, "inv.loot.players");
        }
    }

    @Override
    public void setData(Vector<String> dataList, Map<String, Integer> dataMap) {
        scroll.setList(dataList);
        scroll.setSelected(menu.playerParent);
    }

    @Override
    public void scrollClicked(GuiCustomScrollNop scroll) {
        if (menu.playerParent.equals(scroll.getSelected())) { return; }
        wait = true;
        Packets.sendServer(new SPacketDeadLootsOpen(scroll.getSelected()));
    }

    @Override
    public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

    @Override
    public void setSelected(String select) { }

}
