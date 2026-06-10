package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.roles.JobSpawner;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketNpcJobSpawnerRemove extends PacketServerBasic {

    protected static int channelId;
    private final boolean isDead;
    private final int slot;

    public SPacketNpcJobSpawnerRemove(int slotIn, boolean isDeadIn) {
        slot = slotIn;
        isDead = isDeadIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_ADVANCED); }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketNpcJobSpawnerRemove msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.slot);
        buf.writeBoolean(msg.isDead);
    }

    public static SPacketNpcJobSpawnerRemove decode(FriendlyByteBuf buf) { return new SPacketNpcJobSpawnerRemove(buf.readInt(), buf.readBoolean()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (npc.job instanceof JobSpawner job) {
            if (slot < 0) { job.get(isDead).clear(); }
            else { job.removeSpawned(slot, isDead); }
            CompoundTag nbt = new CompoundTag();
            nbt.putBoolean("JobData", true);
            job.save(nbt);
            job.cleanCompound(nbt);
            nbt.putInt("SetPos", slot - 1);
            nbt.putBoolean("SetDead", isDead);
            Packets.send(player, new PacketGuiData(nbt));
        }
        CustomNpcs.debugData.end("Packets");
    }

}
