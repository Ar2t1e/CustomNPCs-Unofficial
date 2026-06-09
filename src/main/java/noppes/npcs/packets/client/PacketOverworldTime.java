package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketBasic;

public class PacketOverworldTime extends PacketBasic {

    protected static int channelId;
    private long overworldTime;

    public PacketOverworldTime() { }

    public PacketOverworldTime(long overworldTimeIn) { overworldTime = overworldTimeIn; }

    @Override
    public void decode(FriendlyByteBuf buf) { buf.writeLong(overworldTime); }

    @Override
    public void encode(FriendlyByteBuf buf) { overworldTime = buf.readLong(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        CustomNpcs.proxy.getPlayerData(player).questData.overworldTime = overworldTime;
        CustomNpcs.debugData.end("Packets");
    }

}
