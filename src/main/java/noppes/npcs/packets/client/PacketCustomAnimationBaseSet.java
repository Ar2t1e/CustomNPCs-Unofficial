package noppes.npcs.packets.client;

import net.minecraft.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.mixin.entity.player.IEntityPlayerMixin;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PacketCustomAnimationBaseSet extends PacketBasic {

    protected static int channelId;
    private boolean isPlayer;
    private int dimension;
    private int id;
    private UUID uuid;
    private Map<Integer, Integer> map;

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
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.world.provider.getDimension() == dimension) {
            if (isPlayer) { // is Player
                IEntityPlayerMixin pl = (IEntityPlayerMixin) player.world.getPlayerEntityByUUID(uuid);
                if (pl != null) { pl.npcs$getAnimation().loadBaseAnimations(map); }
            }
            else { // is NPC
                Entity entity = player.world.getEntityByID(id);
                if (entity instanceof EntityNPCInterface) { ((EntityNPCInterface) entity).animation.loadBaseAnimations(map); }
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}