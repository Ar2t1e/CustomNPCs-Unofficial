package noppes.npcs.client.gui.global;

import net.minecraft.network.chat.Component;
import noppes.npcs.api.handler.data.IForbiddenItem;
import noppes.npcs.containers.ContainerAuctionForbiddenItem;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketAuctionSave;
import noppes.npcs.shared.client.gui.GuiBasicContainer;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

public class SubGuiAuctionForbiddenItem extends GuiBasicContainer<ContainerAuctionForbiddenItem> {

	public SubGuiAuctionForbiddenItem(ContainerAuctionForbiddenItem container, Component ignoreTitle) {
		super(container, Component.empty());
		xSize = 200;
		ySize = 166;
	}

	@Override
	public void initGui() {
		super.initGui();
		int x = guiLeft;
		int y = guiTop;
		addLabel(0, x + 5, y + 5, Component.translatable("auction.forbidden.title"));
		y += 20;
		IForbiddenItem[] forbidden = menu.auction.getForbiddenItems();
		for (int i = 0; i < forbidden.length; i++) {
			if (!forbidden[i].getItem().isEmpty()) {
				addLabel(10 + i, x + 5, y + i * 14, forbidden[i].getItem().getDisplayName());
			}
		}
		addButton(66, x + 5, y + ySize - 30, "gui.done").setSize(55, 20);
	}

	@Override
	public void buttonEvent(GuiButtonNop button) {
        if (button.id == 66) { onClose(); }
	}

	@Override
	public void drawDefaultBackground() {
		super.drawDefaultBackground();
	}

	@Override
	public void save() {
		Packets.sendServer(new SPacketAuctionSave(menu.auction.getId(), menu.auction.save()));
	}

}
