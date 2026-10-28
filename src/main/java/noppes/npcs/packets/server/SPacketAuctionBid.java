package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.AuctionController;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketAuctionBid extends PacketServerBasic {

    protected static int channelId;
    private int auctionId;
    private long lotId;

    public SPacketAuctionBid() { }

    public SPacketAuctionBid(int auctionIdIn, long lotIdIn) {
        auctionId = auctionIdIn;
        lotId = lotIdIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(auctionId);
        buf.writeLong(lotId);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        auctionId = buf.readInt();
        lotId = buf.readLong();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        AuctionController controller = AuctionController.getInstance();
        if (controller.getAuction(auctionId) != null) { controller.bid(player, auctionId, lotId); }
        CustomNpcs.debugData.end("Packets");
    }

}
