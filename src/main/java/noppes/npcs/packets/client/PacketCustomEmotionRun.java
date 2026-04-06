package noppes.npcs.packets.client;

import net.minecraft.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.mixin.entity.player.IEntityPlayerMixin;
import noppes.npcs.client.model.animation.EmotionConfig;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

import java.util.UUID;

public class PacketCustomEmotionRun extends PacketBasic {

    protected static int channelId;
    private boolean isPlayer;
    private int dimension;
    private int id;
    private UUID uuid;
    private int emtnId;

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
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.world.provider.getDimension() == dimension) {
            EmotionConfig ec = AnimationController.getInstance().getEmotion(emtnId);
            if (ec != null) {
                if (isPlayer) { // is Player
                    IEntityPlayerMixin pl = (IEntityPlayerMixin) player.world.getPlayerEntityByUUID(uuid);
                    if (pl != null) { pl.npcs$getAnimation().tryRunEmotion(ec); }
                } else { // is NPC
                    Entity entity = player.world.getEntityByID(id);
                    if (entity instanceof EntityNPCInterface) { ((EntityNPCInterface) entity).animation.tryRunEmotion(ec); }
                }
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}