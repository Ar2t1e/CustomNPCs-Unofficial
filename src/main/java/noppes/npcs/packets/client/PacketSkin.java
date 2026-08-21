package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSkin extends PacketBasic {
    protected static int channelId;

    public int type;
    public NBTTagCompound data;

    public PacketSkin() { }

    public PacketSkin(int typeIn, NBTTagCompound dataIn) {
        type = typeIn;
        data = dataIn;
    }


    @Override
    public void decode(FriendlyByteBuf buf) {
        type = buf.readInt();
        data = buf.readNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(type);
        buf.writeNbt(data);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}
