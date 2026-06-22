package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketNpcInitData;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketNpcInitData extends PacketServerBasic {

    protected static int channelId;
    private final int npcId;

    public SPacketNpcInitData(int npcIdIn) { npcId = npcIdIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketNpcInitData msg, FriendlyByteBuf buf) { buf.writeInt(msg.npcId); }

    public static SPacketNpcInitData decode(FriendlyByteBuf buf) { return new SPacketNpcInitData(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Entity e = player.level().getEntity(npcId);
        if (e instanceof EntityNPCInterface) {
            CompoundTag compound = new CompoundTag();
            e.save(compound);
            Packets.send(player, new PacketNpcInitData(npcId, compound));
        }
        CustomNpcs.debugData.end("Packets");
    }

}
