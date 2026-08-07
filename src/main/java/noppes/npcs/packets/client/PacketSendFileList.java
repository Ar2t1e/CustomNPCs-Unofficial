package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSendFileList extends PacketBasic {

    protected static int channelId;
    public NBTTagCompound compound;

    public PacketSendFileList() { }

    public PacketSendFileList(NBTTagCompound compoundIn) { compound = compoundIn; }

    @Override
    public void decode(FriendlyByteBuf buf) { compound = buf.readNbt(); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeNbt(compound); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}
