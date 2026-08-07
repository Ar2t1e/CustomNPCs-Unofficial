package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.api.constants.AnimationKind;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

import java.util.UUID;

public class PacketCustomAnimationRun extends PacketBasic {

    protected static int channelId;
    public boolean isPlayer;
    public int dimension;
    public int id;
    public UUID uuid;
    public int animId;
    public AnimationKind animType;

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
    protected void handle() { Client.processPacket(this); }

}