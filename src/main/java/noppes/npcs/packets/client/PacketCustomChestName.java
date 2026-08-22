package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketCustomChestName extends PacketBasic {

    protected static int channelId;
    public String name;

    public PacketCustomChestName() { }

    public PacketCustomChestName(String nameIn) { name = nameIn; }

    @Override
    public void decode(FriendlyByteBuf buf) { name = buf.readUtf(); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeUtf(name); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}