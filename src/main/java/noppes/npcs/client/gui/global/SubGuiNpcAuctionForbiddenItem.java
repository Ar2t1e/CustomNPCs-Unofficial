package noppes.npcs.client.gui.global;

import net.minecraft.network.chat.Component;
import noppes.npcs.containers.ContainerAuctionForbiddenItem;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketAuctionSave;
import noppes.npcs.shared.client.gui.GuiBasicContainer;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

import javax.annotation.Nonnull;

public class SubGuiNpcAuctionForbiddenItem extends GuiBasicContainer<ContainerAuctionForbiddenItem> {

    public SubGuiNpcAuctionForbiddenItem(ContainerAuctionForbiddenItem c) {
        super(c, Component.empty());
    }

    @Override
    public void initGui() {

    }

    @Override
    public void buttonEvent(@Nonnull GuiButtonNop button) {

    }

    @Override
    public void drawDefaultBackground() {

    }

    @Override
    public void save() {
        Packets.sendServer(new SPacketAuctionSave(menu.auction.getId(), menu.auction.save()));
    }

}
