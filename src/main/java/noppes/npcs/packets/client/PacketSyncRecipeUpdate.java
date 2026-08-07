package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSyncRecipeUpdate extends PacketBasic {

    protected static int channelId;
    public ResourceLocation id;
    public int type;
    public NBTTagCompound data;

    public PacketSyncRecipeUpdate() { }

    @SuppressWarnings("unused")
    public PacketSyncRecipeUpdate(ResourceLocation idIn, int typeIn, NBTTagCompound dataIn) {
        id = idIn;
        type = typeIn;
        data = dataIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        id = buf.readResourceLocation();
        type = buf.readInt();
        data = buf.readNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(id);
        buf.writeInt(type);
        buf.writeNbt(data);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}
