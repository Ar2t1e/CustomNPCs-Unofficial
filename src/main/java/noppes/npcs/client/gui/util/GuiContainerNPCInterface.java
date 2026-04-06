package noppes.npcs.client.gui.util;

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
		drawNpc(npc, x, y, 1.0F, 0, 0, 0);
	}

}
