package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketRemoteNpcsEntity;

public class SPacketRemoteNpcsEntity extends PacketServerBasic {

    protected static int channelId;
    private final int id;

    public SPacketRemoteNpcsEntity(int entityId) { id = entityId; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketRemoteNpcsEntity msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

    public static SPacketRemoteNpcsEntity decode(FriendlyByteBuf buf) { return new SPacketRemoteNpcsEntity(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Entity entity = player.level().getEntity(id);
        if (entity != null) {
            CompoundTag compound = new CompoundTag();
            entity.save(compound);
            Packets.send(player, new PacketRemoteNpcsEntity(compound));
        }
        CustomNpcs.debugData.start("Packets");
    }

}