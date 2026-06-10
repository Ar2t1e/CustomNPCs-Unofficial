package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.roles.JobSpawner;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketNpcJobSpawnerRemove extends PacketServerBasic {

    protected static int channelId;
    private boolean isDead;
    private int slot;

    public SPacketNpcJobSpawnerRemove() { }

    public SPacketNpcJobSpawnerRemove(int slotIn, boolean isDeadIn) {
        slot = slotIn;
        isDead = isDeadIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slot);
        buf.writeBoolean(isDead);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        slot = buf.readInt();
        isDead = buf.readBoolean();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    public void handle() {
        CustomNpcs.debugData.start("Packets");
        if (npc.job instanceof JobSpawner) {
            JobSpawner job = (JobSpawner) npc.job;
            if (slot < 0) { job.get(isDead).clear(); }
            else { job.removeSpawned(slot, isDead); }
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setBoolean("JobData", true);
            job.save(nbt);
            job.cleanCompound(nbt);
            nbt.setInteger("SetPos", slot - 1);
            nbt.setBoolean("SetDead", isDead);
            Packets.send(player, new PacketGuiData(nbt));
        }
        CustomNpcs.debugData.end("Packets");
    }

}