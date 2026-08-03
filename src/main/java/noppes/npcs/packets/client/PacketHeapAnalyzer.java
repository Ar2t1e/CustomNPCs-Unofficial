package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.command.CmdHeapAnalyzer;
import noppes.npcs.shared.common.PacketBasic;

public class PacketHeapAnalyzer extends PacketBasic {

    protected static int channelId;

    public enum State { START, STOP, MANUAL }

    private State type;
    private int count;

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
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        switch (type) {
            case START: CmdHeapAnalyzer.startTracking(null, count); break;
            case STOP: CmdHeapAnalyzer.stopTracking(null, count); break;
            case MANUAL: CmdHeapAnalyzer.doManual(null, count); break;
        }
        CustomNpcs.debugData.end("Packets");
    }

}