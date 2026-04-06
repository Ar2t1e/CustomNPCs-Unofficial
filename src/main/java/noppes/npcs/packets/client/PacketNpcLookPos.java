package noppes.npcs.packets.client;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcLookPos extends PacketBasic {

    protected static int channelId;
    private int dimensionId;
    private int npcId;
    private int lookId;

    public PacketNpcLookPos() { }

    public PacketNpcLookPos(int dimensionIdIn, int npcIdIn, int lookIdIn) {
        dimensionId = dimensionIdIn;
        npcId = npcIdIn;
        lookId = lookIdIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        dimensionId = buf.readInt();
        npcId = buf.readInt();
        lookId = buf.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(dimensionId);
        buf.writeInt(npcId);
        buf.writeInt(lookId);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (player.world.provider.getDimension() == dimensionId) {
            Entity e = player.world.getEntityByID(npcId);
            if (e instanceof EntityNPCInterface) {
                if (lookId < 0) { ((EntityNPCInterface) e).lookAt = null; }
                else { ((EntityNPCInterface) e).lookAt = player.world.getEntityByID(lookId); }
            }
        }
        CustomNpcs.debugData.end("Packets");
    }

}