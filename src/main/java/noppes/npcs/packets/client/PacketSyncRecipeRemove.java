package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSyncRecipeRemove extends PacketBasic {

    protected static int channelId;
    public ResourceLocation id;
    public int type;

    public PacketSyncRecipeRemove() { }

    @SuppressWarnings("unused")
    public PacketSyncRecipeRemove(ResourceLocation idIn, int typeIn) {
        id = idIn;
        type = typeIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        id = buf.readResourceLocation();
        type = buf.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(id);
        buf.writeInt(type);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() { Client.processPacket(this); }

}
