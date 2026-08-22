package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketDealUpdate extends PacketBasic {

    protected static int channelId;
    public int marcetID;
    public NBTTagCompound dealData;

    public PacketDealUpdate() { }

    public PacketDealUpdate(int marcetIDIn, NBTTagCompound dealDataIn) {
        marcetID = marcetIDIn;
        dealData = dealDataIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        marcetID = buf.readInt();
        dealData = buf.readNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(marcetID);
        buf.writeNbt(dealData);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}