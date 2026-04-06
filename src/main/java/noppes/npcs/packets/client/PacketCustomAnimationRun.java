package noppes.npcs.packets.client;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.AnimationKind;
import noppes.npcs.api.mixin.entity.player.IPlayerMixin;
import noppes.npcs.client.model.animation.AnimationConfig;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

import java.util.UUID;

public class PacketCustomAnimationRun extends PacketBasic {

    protected static int channelId;
    private final boolean isPlayer;
    private final ResourceKey<Level> dimension;
    private final int id;
    private final UUID uuid;
    private final int animId;
    private final AnimationKind animType;

    public PacketCustomAnimationRun(boolean isPlayerIn, ResourceKey<Level> dimensionIn, int idIn, UUID uuidIn, int animIdIn, AnimationKind animTypeIn) {
        isPlayer = isPlayerIn;
        dimension = dimensionIn;
        id = idIn;
        uuid = uuidIn;
        animId = animIdIn;
        animType = animTypeIn;
    }

    public static void encode(PacketCustomAnimationRun msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.isPlayer);
        buf.writeResourceKey(msg.dimension);
        buf.writeInt(msg.id);
        buf.writeUUID(msg.uuid);
        buf.writeInt(msg.animId);
        buf.writeEnum(msg.animType);
    }

    public static PacketCustomAnimationRun decode(FriendlyByteBuf buf) {
        return new PacketCustomAnimationRun(buf.readBoolean(), buf.readResourceKey(Registries.DIMENSION),
                buf.readInt(), buf.readUUID(), buf.readInt(), buf.readEnum(AnimationKind.class));
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.level().dimension().equals(dimension)) {
            AnimationConfig ac = AnimationController.getInstance().getAnimation(animId);
            if (ac != null) {
                if (isPlayer) { // is Player
                    IPlayerMixin pl = (IPlayerMixin) player.level().getPlayerByUUID(uuid);
                    if (pl != null) { pl.npcs$getAnimation().tryRunAnimation(ac, animType); }
                } else { // is NPC
                    if (player.level().getEntity(id) instanceof EntityNPCInterface npc) { npc.animation.tryRunAnimation(ac, animType); }
                }
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}