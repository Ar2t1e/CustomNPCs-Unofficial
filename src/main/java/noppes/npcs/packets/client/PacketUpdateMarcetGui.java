package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketUpdateMarcetGui extends PacketBasic {

    protected static int channelId;

    @Override
    public void decode(FriendlyByteBuf buf) { }

    @Override
    public void encode(FriendlyByteBuf buf) { }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}