package noppes.npcs.packets.client;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcCustomAnimation extends PacketBasic {

    protected static int channelId;
    private final ResourceKey<Level> dimension;
    private final int npcId;
    private final int animation;

    public PacketNpcCustomAnimation(ResourceKey<Level> dimensionIn, int npcIdIn, int animationIn) {
        dimension = dimensionIn;
        npcId = npcIdIn;
        animation = animationIn;
    }

    public static void encode(PacketNpcCustomAnimation msg, FriendlyByteBuf buf) {
        buf.writeResourceKey(msg.dimension);
        buf.writeInt(msg.npcId);
        buf.writeInt(msg.animation);
    }

    public static PacketNpcCustomAnimation decode(FriendlyByteBuf buf) {
        return new PacketNpcCustomAnimation(buf.readResourceKey(Registries.DIMENSION), buf.readInt(), buf.readInt());
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Level level = player.level();
        if (!level.dimension().location().equals(dimension.location())) {
            level = null;
            MinecraftServer server = player.getServer();
            if (server != null) { level = server.getLevel(dimension); }
        }
        if (level != null) {
            Entity entity = level.getEntity(npcId);
            if (entity instanceof EntityNPCInterface cnpc) { cnpc.currentAnimation = animation; }
        }
        CustomNpcs.debugData.end("Packets");
    }

}
