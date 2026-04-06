package noppes.npcs.packets.client;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.mixin.entity.player.IPlayerMixin;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PacketCustomAnimationBaseSet extends PacketBasic {

    protected static int channelId;
    private final boolean isPlayer;
    private final ResourceKey<Level> dimension;
    private final int id;
    private final UUID uuid;
    private final Map<Integer, Integer> map;

    public PacketCustomAnimationBaseSet(boolean isPlayerIn, ResourceKey<Level> dimensionIn, int idIn, UUID uuidIn, Map<Integer, Integer> mapIn) {
        isPlayer = isPlayerIn;
        dimension = dimensionIn;
        id = idIn;
        uuid = uuidIn;
        map = mapIn;
    }

    public static void encode(PacketCustomAnimationBaseSet msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.isPlayer);
        buf.writeResourceKey(msg.dimension);
        buf.writeInt(msg.id);
        buf.writeUUID(msg.uuid);
        buf.writeVarInt(msg.map.size());
        for (Map.Entry<Integer, Integer> entry : msg.map.entrySet()) {
            buf.writeVarInt(entry.getKey());
            buf.writeVarInt(entry.getValue());
        }
    }

    public static PacketCustomAnimationBaseSet decode(FriendlyByteBuf buf) {
        boolean isPlayer = buf.readBoolean();
        ResourceKey<Level> dimension = buf.readResourceKey(Registries.DIMENSION);
        int id = buf.readInt();
        UUID uuid = buf.readUUID();
        int size = buf.readVarInt();
        Map<Integer, Integer> map = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            int key = buf.readVarInt();
            int value = buf.readVarInt();
            map.put(key, value);
        }
        return new PacketCustomAnimationBaseSet(isPlayer, dimension, id, uuid, map);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.level().dimension().equals(dimension)) {
            if (isPlayer) { // is Player
                IPlayerMixin pl = (IPlayerMixin) player.level().getPlayerByUUID(uuid);
                if (pl != null) { pl.npcs$getAnimation().loadBaseAnimations(map); }
            } else { // is NPC
                if (player.level().getEntity(id) instanceof EntityNPCInterface npc) { npc.animation.loadBaseAnimations(map); }
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}