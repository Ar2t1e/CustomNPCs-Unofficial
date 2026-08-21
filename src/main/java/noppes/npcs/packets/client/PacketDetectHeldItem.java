package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketDetectHeldItem extends PacketBasic {

    protected static int channelId;
    public int slotID;
    public NBTTagCompound data;

    public PacketDetectHeldItem() { }

    public PacketDetectHeldItem(int slotIDIn, NBTTagCompound dataIn) {
        slotID = slotIDIn;
        data = dataIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        slotID = buf.readInt();
        data = buf.readNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slotID);
        buf.writeNbt(data);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}