package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

import java.util.HashMap;
import java.util.Map;

public class PacketScriptConsole extends PacketBasic {

    protected static int channelId;
    public static final Map<Integer, Map<Long, String[]>> data = new HashMap<>();

    public int tab;
    public long time;
    public int id;
    public int maxIDs;
    public String part;
    public boolean isSetClient;

    public PacketScriptConsole() { }

    public PacketScriptConsole(int tabIn, long timeIn, int idIn, int maxIDsIn, String partIn, boolean isSetClientIn) {
        tab = tabIn;
        time = timeIn;
        id = idIn;
        maxIDs = maxIDsIn;
        part = partIn;
        isSetClient = isSetClientIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        tab = buf.readInt();
        time = buf.readLong();
        id = buf.readInt();
        maxIDs = buf.readInt();
        part = buf.readUtf();
        isSetClient = buf.readBoolean();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(tab);
        buf.writeLong(time);
        buf.writeInt(id);
        buf.writeInt(maxIDs);
        buf.writeUtf(part);
        buf.writeBoolean(isSetClient);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}