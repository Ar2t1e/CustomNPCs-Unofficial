package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketMarcetClose extends PacketBasic {

    protected static int channelId;
    public int marcetID;

    public PacketMarcetClose() { }

    public PacketMarcetClose(int marcetIDIn) { marcetID = marcetIDIn; }

    @Override
    public void decode(FriendlyByteBuf buf) { marcetID = buf.readInt(); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(marcetID); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}