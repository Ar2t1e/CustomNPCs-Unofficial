package noppes.npcs.packets.client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.pathfinding.PathPoint;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcTarget extends PacketBasic {

    protected static int channelId;
    private int entityId;
    private int targetId;

    public PacketNpcTarget() { }

    public PacketNpcTarget(int entityIdIn, int targetIdIn) {
        entityId = entityIdIn;
        targetId = targetIdIn;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        entityId = buf.readInt();
        targetId = buf.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(targetId);
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Entity entity = player.world.getEntityByID(entityId);
        if (entity instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface) entity;
            if (targetId > -1) {
                Entity target = npc.world.getEntityByID(targetId);
                if (target instanceof EntityLivingBase) { npc.setAttackTarget((EntityLivingBase) target); }
                else { npc.setAttackTarget(null); }
            }
            else { npc.setAttackTarget(null); }
        }
        CustomNpcs.debugData.end("Packets");
    }

    private void writePathPoint(FriendlyByteBuf buf, PathPoint point) {
        buf.writeInt(point.x);
        buf.writeInt(point.y);
        buf.writeInt(point.z);
        buf.writeFloat(point.distanceFromOrigin);
        buf.writeFloat(point.cost);
        buf.writeFloat(point.costMalus);
        buf.writeBoolean(point.visited);
        buf.writeInt(point.nodeType.ordinal());
        buf.writeFloat(point.distanceToTarget);
    }

}