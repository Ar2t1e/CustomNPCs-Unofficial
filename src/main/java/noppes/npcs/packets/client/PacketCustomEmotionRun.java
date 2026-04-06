package noppes.npcs.packets.client;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.mixin.entity.player.IPlayerMixin;
import noppes.npcs.client.model.animation.EmotionConfig;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

import java.util.UUID;

public class PacketCustomEmotionRun extends PacketBasic {

    protected static int channelId;
    private final boolean isPlayer;
    private final ResourceKey<Level> dimension;
    private final int id;
    private final UUID uuid;
    private final int emtnId;

    public PacketCustomEmotionRun(boolean isPlayerIn, ResourceKey<Level> dimensionIn, int idIn, UUID uuidIn, int animIdIn) {
        isPlayer = isPlayerIn;
        dimension = dimensionIn;
        id = idIn;
        uuid = uuidIn;
        emtnId = animIdIn;
    }

    public static void encode(PacketCustomEmotionRun msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.isPlayer);
        buf.writeResourceKey(msg.dimension);
        buf.writeInt(msg.id);
        buf.writeUUID(msg.uuid);
        buf.writeInt(msg.emtnId);
    }

    public static PacketCustomEmotionRun decode(FriendlyByteBuf buf) {
        return new PacketCustomEmotionRun(buf.readBoolean(), buf.readResourceKey(Registries.DIMENSION),
                buf.readInt(), buf.readUUID(), buf.readInt());
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.level().dimension().equals(dimension)) {
            EmotionConfig ec = AnimationController.getInstance().getEmotion(emtnId);
            if (ec != null) {
                if (isPlayer) { // is Player
                    IPlayerMixin pl = (IPlayerMixin) player.level().getPlayerByUUID(uuid);
                    if (pl != null) { pl.npcs$getAnimation().tryRunEmotion(ec); }
                } else { // is NPC
                    if (player.level().getEntity(id) instanceof EntityNPCInterface npc) { npc.animation.tryRunEmotion(ec); }
                }
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}