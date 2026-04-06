package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.entity.data.role.IJobSpawner;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.roles.JobSpawner;
import noppes.npcs.roles.data.JobSpawnerCloneData;
import noppes.npcs.roles.data.JobSpawnerNbtData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketNpcJobSpawnerAdd extends PacketServerBasic {

    protected static int channelId;
    private boolean isDead;
    private int tab;
    private String name;
    private NBTTagCompound spawnerData;

    public SPacketNpcJobSpawnerAdd() { }

    public SPacketNpcJobSpawnerAdd(boolean isDeadIn, String nameIn, int tabIn, NBTTagCompound compoundIn) {
        isDead = isDeadIn;
        tab = tabIn;
        name = nameIn;
        spawnerData = compoundIn;
    }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isDead);
        buf.writeInt(tab);
        buf.writeUtf(name);
        buf.writeNbt(spawnerData);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        isDead = buf.readBoolean();
        tab = buf.readInt();
        spawnerData = buf.readAnySizeNbt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    public void handle() {
        CustomNpcs.debugData.start("Packets");
        if (npc.job instanceof JobSpawner) {
            JobSpawner job = (JobSpawner) npc.job;
            IJobSpawner.IJobSpawnerData sd;
            if (spawnerData.hasNoTags() && !name.isEmpty() && tab > 0 && tab < 10) {
                sd = job.get(isDead).add(true);
                if (sd instanceof JobSpawnerCloneData) {
                    ((JobSpawnerCloneData) sd).setName(name);
                    ((JobSpawnerCloneData) sd).setTab(tab);
                }
            }
            else if (!spawnerData.hasNoTags()) {
                sd = job.get(isDead).add(false);
                if (sd instanceof JobSpawnerNbtData) { ((JobSpawnerNbtData) sd).load(spawnerData); }
            }
            NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean("JobData", true);
            job.save(compound);
            job.cleanCompound(compound);
            compound.setBoolean("SetDead", isDead);
            compound.setInteger("SetPos", job.get(isDead).dataEntitys.size() - 1);
            Packets.send(player, new PacketGuiData(compound));
        }
        CustomNpcs.debugData.end("Packets");
    }

}