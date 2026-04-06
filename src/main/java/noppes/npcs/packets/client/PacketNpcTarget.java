package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcTarget extends PacketBasic {

    protected static int channelId;
    private final int entityId;
    private final int targetId;

    public PacketNpcTarget(int entityIdIn, int targetIdIn) {
        entityId = entityIdIn;
        targetId = targetIdIn;
    }

    public static void encode(PacketNpcTarget msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeInt(msg.targetId);
    }

    public static PacketNpcTarget decode(FriendlyByteBuf buf) { return new PacketNpcTarget(buf.readInt(), buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Entity entity = player.level().getEntity(entityId);
        if (entity instanceof EntityNPCInterface npc) {
            if (targetId > -1) {
                Entity targetIn = npc.level().getEntity(targetId);
                if (targetIn instanceof LivingEntity target) { npc.setTarget(target); }
                else { npc.setTarget(null); }
            }
            else { npc.setTarget(null); }
        }
        CustomNpcs.debugData.end("Packets");
    }

}
