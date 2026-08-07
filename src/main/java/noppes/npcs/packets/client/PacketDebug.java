package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketDebug extends PacketBasic {

    protected static int channelId;
    public boolean isLogOrClear;

    public PacketDebug() { }

    public PacketDebug(boolean isLogOrClearOn) { isLogOrClear = isLogOrClearOn; }

    @Override
    public void decode(FriendlyByteBuf buf) { isLogOrClear = buf.readBoolean(); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeBoolean(isLogOrClear); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}
