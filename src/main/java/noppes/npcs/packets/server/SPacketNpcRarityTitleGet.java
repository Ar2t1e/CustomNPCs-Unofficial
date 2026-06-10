package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketNpcRarityTitleSet;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketNpcRarityTitleGet extends PacketServerBasic {

    protected static int channelId;
    private final int npcId;

    public SPacketNpcRarityTitleGet(int npcIdIn) { npcId = npcIdIn; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return null; }

    public static void encode(SPacketNpcRarityTitleGet msg, FriendlyByteBuf buf) { buf.writeInt(msg.npcId); }

    public static SPacketNpcRarityTitleGet decode(FriendlyByteBuf buf) { return new SPacketNpcRarityTitleGet(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        Entity entity = player.level().getEntity(npcId);
        if (entity instanceof EntityNPCInterface cnpc) {
            CompoundTag compound = new CompoundTag();
            compound.putInt("NPCLevel", cnpc.stats.getLevel());
            compound.putInt("NPCRarity", cnpc.stats.getRarity());
            compound.putString("RarityTitle", cnpc.stats.getRarityTitle());
            Packets.send(player, new PacketNpcRarityTitleSet(npcId, compound));
        }
        CustomNpcs.debugData.end("Packets");
    }

}