package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketAuctionLotRemoved extends PacketBasic {

    protected static int channelId;
    public int auctionId;
    public int lotId;

    public PacketAuctionLotRemoved() { }

    public PacketAuctionLotRemoved(int auctionIdIn, int lotIdIn) {
        auctionId = auctionIdIn;
        lotId = lotIdIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        auctionId = buf.readInt();
        lotId = buf.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(auctionId);
        buf.writeInt(lotId);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}
