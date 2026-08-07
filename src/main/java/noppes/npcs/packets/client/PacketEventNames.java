package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

import java.util.*;

public class PacketEventNames extends PacketBasic {

    protected static int channelId;
    public byte type; // 0: client; 1: forge; 2: api
    public Map<String, String> names;

    public PacketEventNames() { }

    public PacketEventNames(Map<String, String> namesIn, byte typeIn) {
        type = typeIn;
        names = namesIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        names = new HashMap<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) { names.put(buf.readUtf(), buf.readUtf()); }
        type = buf.readByte();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(names.size());
        for (Map.Entry<String, String> entry : names.entrySet()) {
            if (entry.getKey() == null) { buf.writeUtf(""); }
            else { buf.writeUtf(entry.getKey()); }
            buf.writeUtf(entry.getValue());
        }
        buf.writeByte(type);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}
