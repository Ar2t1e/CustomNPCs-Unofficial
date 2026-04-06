package noppes.npcs.packets.client;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.mixin.entity.player.IPlayerMixin;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

import java.util.UUID;

public class PacketCustomEmotionStop extends PacketBasic {

    protected static int channelId;
    private final boolean isPlayer;
    private final ResourceKey<Level> dimension;
    private final int id;
    private final UUID uuid;

    public PacketCustomEmotionStop(boolean isPlayerIn, ResourceKey<Level> dimensionIn, int idIn, UUID uuidIn) {
        isPlayer = isPlayerIn;
        dimension = dimensionIn;
        id = idIn;
        uuid = uuidIn;
    }

    public static void encode(PacketCustomEmotionStop msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.isPlayer);
        buf.writeResourceKey(msg.dimension);
        buf.writeInt(msg.id);
        buf.writeUUID(msg.uuid);
    }

    public static PacketCustomEmotionStop decode(FriendlyByteBuf buf) {
        return new PacketCustomEmotionStop(buf.readBoolean(), buf.readResourceKey(Registries.DIMENSION), buf.readInt(), buf.readUUID());
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.level().dimension().equals(dimension)) {
            if (isPlayer) { // is Player
                IPlayerMixin pl = (IPlayerMixin) player.level().getPlayerByUUID(uuid);
                if (pl != null) { pl.npcs$getAnimation().stopEmotion(); }
            } else { // is NPC
                if (player.level().getEntity(id) instanceof EntityNPCInterface npc) { npc.animation.stopEmotion(); }
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}