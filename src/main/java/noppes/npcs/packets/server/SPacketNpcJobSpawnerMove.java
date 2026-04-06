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

public class SPacketNpcJobSpawnerMove extends PacketServerBasic {

    protected static int channelId;
    private final boolean isUp;
    private final int slot;
    private final boolean isDead;

    public SPacketNpcJobSpawnerMove(int slotIn, boolean isUpIn, boolean isDeadIn) {
        slot = slotIn;
        isUp = isUpIn;
        isDead = isDeadIn;
    }

    public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.NPC_ADVANCED; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketNpcJobSpawnerMove msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.slot);
        buf.writeBoolean(msg.isUp);
        buf.writeBoolean(msg.isDead);
    }

    public static SPacketNpcJobSpawnerMove decode(FriendlyByteBuf buf) { return new SPacketNpcJobSpawnerMove(buf.readInt(), buf.readBoolean(), buf.readBoolean()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (npc.job instanceof JobSpawner job) {
            int slotPos = slot;
            if (isUp && job.get(isDead).up(slot)) { slotPos--; }
            else if (!isUp && job.get(isDead).down(slot)) { slotPos++; }
            CompoundTag nbt = new CompoundTag();
            nbt.putBoolean("JobData", true);
            job.save(nbt);
            job.cleanCompound(nbt);
            nbt.putInt("SetPos", slotPos);
            nbt.putBoolean("SetDead", isDead);
            Packets.send(player, new PacketGuiData(nbt));
        }
        CustomNpcs.debugData.end("Packets");
    }

}