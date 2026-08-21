package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketMarcetRemove extends PacketBasic {

    protected static int channelId;
    public int marcetID;

    public PacketMarcetRemove() { }

    public PacketMarcetRemove(int marcetIDIn) { marcetID = marcetIDIn; }

    @Override
    public void decode(FriendlyByteBuf buf) { marcetID = buf.readInt(); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(marcetID); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}
