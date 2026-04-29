package noppes.npcs.client.gui.util;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.Container;
import net.minecraft.network.chat.Component;
import noppes.npcs.shared.client.gui.GuiBasicContainer;

import noppes.npcs.entity.EntityNPCInterface;

public class GuiContainerNPCInterface<T extends Container> extends GuiBasicContainer<T> {

	public EntityNPCInterface npc;

	public GuiContainerNPCInterface(EntityNPCInterface npcIn, T cont, Component title) {
		super(cont, title);
		npc = npcIn;
	}

	public void drawNpc(int x, int y) {
		if (npc == null) { return; }
		wrapper.drawNpc(npc, x, y, 1.0F, 0, 0, 0, guiLeft, guiTop);
	}

	public void drawNpc(Entity entity, int x, int y, float zoomed, int rotation, int vertical, int followCursor) {
		if (entity == null) { return; }
		wrapper.drawNpc(entity, x, y, zoomed, rotation, vertical, followCursor, guiLeft, guiTop);
	}

}
