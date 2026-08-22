package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

import java.util.UUID;

public class PacketCustomEmotionRun extends PacketBasic {

    protected static int channelId;
    public boolean isPlayer;
    public int dimension;
    public int id;
    public UUID uuid;
    public int emtnId;

    public PacketCustomEmotionRun() { }

    public PacketCustomEmotionRun(boolean isPlayerIn, int dimensionIn, int idIn, UUID uuidIn, int emtnIdIn) {
        isPlayer = isPlayerIn;
        dimension = dimensionIn;
        id = idIn;
        uuid = uuidIn;
        emtnId = emtnIdIn;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isPlayer);
        buf.writeInt(dimension);
        buf.writeInt(id);
        buf.writeUUID(uuid);
        buf.writeInt(emtnId);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        isPlayer = buf.readBoolean();
        dimension = buf.readInt();
        id = buf.readInt();
        uuid = buf.readUUID();
        emtnId = buf.readInt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}