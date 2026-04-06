package noppes.npcs.packets.client;

import net.minecraft.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.AnimationKind;
import noppes.npcs.api.mixin.entity.player.IEntityPlayerMixin;
import noppes.npcs.client.model.animation.AnimationConfig;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

import java.util.UUID;

public class PacketCustomAnimationRun extends PacketBasic {

    protected static int channelId;
    private boolean isPlayer;
    private int dimension;
    private int id;
    private UUID uuid;
    private int animId;
    private AnimationKind animType;

    public PacketCustomAnimationRun() { }

    public PacketCustomAnimationRun(boolean isPlayerIn, int dimensionIn, int idIn, UUID uuidIn, int animIdIn, AnimationKind animTypeIn) {
        isPlayer = isPlayerIn;
        dimension = dimensionIn;
        id = idIn;
        uuid = uuidIn;
        animId = animIdIn;
        animType = animTypeIn;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isPlayer);
        buf.writeInt(dimension);
        buf.writeInt(id);
        buf.writeUUID(uuid);
        buf.writeInt(animId);
        buf.writeEnum(animType);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        isPlayer = buf.readBoolean();
        dimension = buf.readInt();
        id = buf.readInt();
        uuid = buf.readUUID();
        animId = buf.readInt();
        animType = buf.readEnum(AnimationKind.class);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.world.provider.getDimension() == dimension) {
            AnimationConfig ac = AnimationController.getInstance().getAnimation(animId);
            if (ac != null) {
                if (isPlayer) { // is Player
                    IEntityPlayerMixin pl = (IEntityPlayerMixin) player.world.getPlayerEntityByUUID(uuid);
                    if (pl != null) { pl.npcs$getAnimation().tryRunAnimation(ac, animType); }
                } else { // is NPC
                    Entity entity = player.world.getEntityByID(id);
                    if (entity instanceof EntityNPCInterface) { ((EntityNPCInterface) entity).animation.tryRunAnimation(ac, animType); }
                }
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}