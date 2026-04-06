package noppes.npcs.packets.client;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcLookPos extends PacketBasic {

    protected static int channelId;
    private final ResourceKey<Level> dimensionId;
    private final int npcId;
    private final int lookId;

    public PacketNpcLookPos(ResourceKey<Level> dimensionIdIn, int npcIdIn, int lookIdIn) {
        dimensionId = dimensionIdIn;
        npcId = npcIdIn;
        lookId = lookIdIn;
    }

    public static void encode(PacketNpcLookPos msg, FriendlyByteBuf buf) {
        buf.writeResourceKey(msg.dimensionId);
        buf.writeInt(msg.npcId);
        buf.writeInt(msg.lookId);
    }

    public static PacketNpcLookPos decode(FriendlyByteBuf buf) {
        return new PacketNpcLookPos(buf.readResourceKey(Registries.DIMENSION), buf.readInt(), buf.readInt());
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.level().dimension().equals(dimensionId)) {
            Entity e = player.level().getEntity(npcId);
            if (e instanceof EntityNPCInterface cnpc) {
                if (lookId < 0) { cnpc.lookAt = null; }
                else { cnpc.lookAt = player.level().getEntity(lookId); }
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}