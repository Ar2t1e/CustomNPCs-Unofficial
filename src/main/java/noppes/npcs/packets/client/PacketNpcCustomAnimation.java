package noppes.npcs.packets.client;

import net.minecraft.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcCustomAnimation extends PacketBasic {

    protected static int channelId;
    private int dimension;
    private int npcId;
    private int animation;

    public PacketNpcCustomAnimation() { }

    public PacketNpcCustomAnimation(int dimensionIn, int npcIdIn, int animationIn) {
        dimension = dimensionIn;
        npcId = npcIdIn;
        animation = animationIn;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(dimension);
        buf.writeInt(npcId);
        buf.writeInt(animation);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        dimension = buf.readInt();
        npcId = buf.readInt();
        animation = buf.readInt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        World world = player.world;
        if (world.provider.getDimension() != dimension) {
            world = null;
            MinecraftServer server = player.getServer();
            if (server != null) { world = server.getWorld(dimension); }
        }
        if (world != null) {
            Entity entity = world.getEntityByID(npcId);
            if (entity instanceof EntityNPCInterface) {
                ((EntityNPCInterface) entity).currentAnimation = animation;
                ((EntityNPCInterface) entity).updateHitbox();
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}