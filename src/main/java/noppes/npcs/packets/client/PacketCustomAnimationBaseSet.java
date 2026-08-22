package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PacketCustomAnimationBaseSet extends PacketBasic {

    protected static int channelId;
    public boolean isPlayer;
    public int dimension;
    public int id;
    public UUID uuid;
    public Map<Integer, Integer> map;

    public PacketCustomAnimationBaseSet() { }

    public PacketCustomAnimationBaseSet(boolean isPlayerIn, int dimensionIn, int idIn, UUID uuidIn, Map<Integer, Integer> mapIn) {
        isPlayer = isPlayerIn;
        dimension = dimensionIn;
        id = idIn;
        uuid = uuidIn;
        map = mapIn;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isPlayer);
        buf.writeInt(dimension);
        buf.writeInt(id);
        buf.writeUUID(uuid);
        buf.writeVarInt(map.size());
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            buf.writeVarInt(entry.getKey());
            buf.writeVarInt(entry.getValue());
        }
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        isPlayer = buf.readBoolean();
        dimension = buf.readInt();
        id = buf.readInt();
        uuid = buf.readUUID();
        int size = buf.readVarInt();
        map = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            int key = buf.readVarInt();
            int value = buf.readVarInt();
            map.put(key, value);
        }
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}