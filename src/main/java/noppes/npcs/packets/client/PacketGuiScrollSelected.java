package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiScrollSelected extends PacketBasic {

    protected static int channelId;
    public String selected;

    public PacketGuiScrollSelected() { }

    public PacketGuiScrollSelected(String selectedIn) { selected = selectedIn; }

    @Override
    public void decode(FriendlyByteBuf buf) { selected = buf.readUtf(); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeUtf(selected); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}
