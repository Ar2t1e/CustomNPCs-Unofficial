package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.roles.JobSpawner;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketNpcJobSpawnerMove extends PacketServerBasic {

    protected static int channelId;
    private boolean isUp;
    private int slot;
    private boolean isDead;

    public SPacketNpcJobSpawnerMove() { }

    public SPacketNpcJobSpawnerMove(int slotIn, boolean isUpIn, boolean isDeadIn) {
        isUp = isUpIn;
        slot = slotIn;
        isDead = isDeadIn;
    }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slot);
        buf.writeBoolean(isUp);
        buf.writeBoolean(isDead);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        slot = buf.readInt();
        isUp = buf.readBoolean();
        isDead = buf.readBoolean();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    public void handle() {
        CustomNpcs.debugData.start("Packets");
        if (npc.job instanceof JobSpawner) {
            JobSpawner job = (JobSpawner) npc.job;
            int slotPos = slot;
            if (isUp && job.get(isDead).up(slot)) { slotPos--; }
            else if (!isUp && job.get(isDead).down(slot)) { slotPos++; }
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setBoolean("JobData", true);
            job.save(nbt);
            job.cleanCompound(nbt);
            nbt.setInteger("SetPos", slotPos);
            nbt.setBoolean("SetDead", isDead);
            Packets.send(player, new PacketGuiData(nbt));
        }
        CustomNpcs.debugData.end("Packets");
    }

}