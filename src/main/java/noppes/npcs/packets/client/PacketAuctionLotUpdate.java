package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketAuctionLotUpdate extends PacketBasic {

    protected static int channelId;
    public int auctionId;
    public long lotId;
    public NBTTagCompound compound;

    public PacketAuctionLotUpdate() { }

    public PacketAuctionLotUpdate(int auctionIdIn, long lotIdIn, NBTTagCompound compoundIn) {
        auctionId = auctionIdIn;
        lotId = lotIdIn;
        compound = compoundIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        auctionId = buf.readInt();
        lotId = buf.readLong();
        compound = buf.readNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(auctionId);
        buf.writeLong(lotId);
        buf.writeNbt(compound);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}