package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcDelete extends PacketBasic {

    protected static int channelId;
    public int id;

    public PacketNpcDelete() { }

    public PacketNpcDelete(int idIn) { id = idIn; }

    @Override
    public void decode(FriendlyByteBuf buf) { id = buf.readInt(); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(id); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}