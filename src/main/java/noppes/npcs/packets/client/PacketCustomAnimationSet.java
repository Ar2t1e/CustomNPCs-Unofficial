package noppes.npcs.packets.client;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.mixin.entity.player.IEntityPlayerMixin;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

import java.util.UUID;

public class PacketCustomAnimationSet extends PacketBasic {

    protected static int channelId;
    private boolean isPlayer;
    private int dimension;
    private int id;
    private UUID uuid;
    private NBTTagCompound data;

    public PacketCustomAnimationSet() { }

    public PacketCustomAnimationSet(boolean isPlayerIn, int dimensionIn, int idIn, UUID uuidIn, NBTTagCompound dataIn) {
        isPlayer = isPlayerIn;
        dimension = dimensionIn;
        id = idIn;
        uuid = uuidIn;
        data = dataIn;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isPlayer);
        buf.writeInt(dimension);
        buf.writeInt(id);
        buf.writeUUID(uuid);
        buf.writeNbt(data);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        isPlayer = buf.readBoolean();
        dimension = buf.readInt();
        id = buf.readInt();
        uuid = buf.readUUID();
        data = buf.readNbt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.world.provider.getDimension() == dimension) {
            if (isPlayer) { // is Player
                IEntityPlayerMixin pl = (IEntityPlayerMixin) player.world.getPlayerEntityByUUID(uuid);
                if (pl != null) { pl.npcs$getAnimation().load(data); }
            }
            else { // is NPC
                Entity entity = player.world.getEntityByID(id);
                if (entity instanceof EntityNPCInterface) { ((EntityNPCInterface) entity).animation.load(data); }
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}