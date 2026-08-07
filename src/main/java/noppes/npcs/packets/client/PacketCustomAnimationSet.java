package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

import java.util.UUID;

public class PacketCustomAnimationSet extends PacketBasic {

    protected static int channelId;
    public boolean isPlayer;
    public int dimension;
    public int id;
    public UUID uuid;
    public NBTTagCompound data;

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
    protected void handle() { Client.processPacket(this); }

}