package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketHeapAnalyzer extends PacketBasic {

    protected static int channelId;

    public enum State { START, STOP, MANUAL }

    public State type;
    public int count;

    public PacketHeapAnalyzer() { }

    public PacketHeapAnalyzer(State typeIn, int countIn) {
        type = typeIn;
        count = countIn;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(type);
        buf.writeInt(count);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        type = buf.readEnum(State.class);
        count = buf.readInt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}