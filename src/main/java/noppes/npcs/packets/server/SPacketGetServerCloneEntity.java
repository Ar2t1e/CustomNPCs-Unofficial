package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.entity.data.role.IJobSpawner;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.roles.JobSpawner;
import noppes.npcs.roles.data.JobSpawnerCloneData;
import noppes.npcs.roles.data.JobSpawnerNbtData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketGetServerCloneEntity extends PacketServerBasic {

    protected static int channelId;
    private final boolean inJob;
    private final boolean isDead;
    private final int tab;
    private final String name;

    public SPacketGetServerCloneEntity(boolean inJobIn, boolean isDeadIn, int tabIn, String nameIn) {
        inJob = inJobIn;
        isDead = isDeadIn;
        tab = tabIn;
        name = nameIn;
    }

    @Override
    public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.NPC_ADVANCED; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    public static void encode(SPacketGetServerCloneEntity msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.inJob);
        buf.writeBoolean(msg.isDead);
        buf.writeInt(msg.tab);
        buf.writeUtf(msg.name);
    }

    public static SPacketGetServerCloneEntity decode(FriendlyByteBuf buf) {
        return new SPacketGetServerCloneEntity(buf.readBoolean(), buf.readBoolean(), buf.readInt(), buf.readUtf());
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        CompoundTag npcNbt = null;
        if (inJob && npc.job instanceof JobSpawner job) {
            IJobSpawner.IJobSpawnerData sd = job.get(isDead).dataEntitys.get(tab);
            if (sd instanceof JobSpawnerCloneData jobData) {
                npcNbt = ServerCloneController.Instance.getCloneData(null, jobData.getName(), jobData.getTab());
            }
            else if (sd instanceof JobSpawnerNbtData jobData) { npcNbt = jobData.save(); }
        }
        else { npcNbt = ServerCloneController.Instance.getCloneData(player.createCommandSourceStack(), name, tab); }
        if (npcNbt != null) {
            CompoundTag compound = new CompoundTag();
            compound.put("NPCData", npcNbt);
            Packets.send(player, new PacketGuiData(compound));
        }
        CustomNpcs.debugData.end("Packets");
    }

}