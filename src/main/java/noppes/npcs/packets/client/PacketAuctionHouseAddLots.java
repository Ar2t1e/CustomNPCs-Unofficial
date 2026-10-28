package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketAuctionHouseAddLots extends PacketBasic {

    protected static int channelId;
    public int auctionId;
    public NBTTagCompound compound;

    public PacketAuctionHouseAddLots() { }

    public PacketAuctionHouseAddLots(int auctionIdIn, NBTTagCompound compoundIn) {
        auctionId = auctionIdIn;
        compound = compoundIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        auctionId = buf.readInt();
        compound = buf.readNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(auctionId);
        buf.writeNbt(compound);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}
