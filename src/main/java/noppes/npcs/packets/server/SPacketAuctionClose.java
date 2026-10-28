package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.AuctionController;
import noppes.npcs.controllers.data.AuctionHouse;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketAuctionClose extends PacketServerBasic {

    protected static int channelId;
    private int auctionId;

    public SPacketAuctionClose() { }

    public SPacketAuctionClose(int auctionIdIn) { auctionId = auctionIdIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(auctionId); }

    @Override
    public void decode(FriendlyByteBuf buf) { auctionId = buf.readInt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        AuctionHouse auction = AuctionController.getInstance().getAuction(auctionId);
        if (auction != null) { auction.removePlayer(player); }
        CustomNpcs.debugData.end("Packets");
    }

}