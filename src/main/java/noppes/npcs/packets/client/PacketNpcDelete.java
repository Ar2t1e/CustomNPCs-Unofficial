package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcDelete extends PacketBasic {

    protected static int channelId;
    private int id;

    public PacketNpcDelete() { }

    public PacketNpcDelete(int idIn) { id = idIn; }

    @Override
    public void decode(FriendlyByteBuf buf) { id = buf.readInt(); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(id); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        WorldClient world = Minecraft.getMinecraft().world;
        if (world != null) {
            Entity entity = world.getEntityByID(id);
            if (entity instanceof EntityNPCInterface) { ((EntityNPCInterface) entity).delete(); }
            else if (entity != null) { entity.setDead(); }
        }
        CustomNpcs.debugData.end("Packets");
    }

}