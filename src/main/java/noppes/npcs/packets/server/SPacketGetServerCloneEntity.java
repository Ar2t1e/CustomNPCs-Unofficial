package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
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

import java.util.Collections;
import java.util.List;

public class SPacketGetServerCloneEntity extends PacketServerBasic {

    protected static int channelId;
    private boolean inJob;
    private boolean isDead;
    private int tab;
    private String name;

    public SPacketGetServerCloneEntity() { }

    public SPacketGetServerCloneEntity(boolean inJobIn, boolean isDeadIn, int tabIn, String nameIn) {
        inJob = inJobIn;
        isDead = isDeadIn;
        tab = tabIn;
        name = nameIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_ADVANCED); }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(inJob);
        buf.writeBoolean(isDead);
        buf.writeInt(tab);
        buf.writeUtf(name);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        inJob = buf.readBoolean();
        isDead = buf.readBoolean();
        tab = buf.readInt();
        name = buf.readUtf();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    public void handle() {
        CustomNpcs.debugData.start("Packets");
        NBTTagCompound npcNbt = null;
        if (inJob && npc.job instanceof JobSpawner) {
            IJobSpawner.IJobSpawnerData sd = ((JobSpawner) npc.job).get(isDead).dataEntitys.get(tab);
            if (sd instanceof JobSpawnerCloneData) {
                npcNbt = ServerCloneController.Instance.getCloneData(null, ((JobSpawnerCloneData) sd).getName(), ((JobSpawnerCloneData) sd).getTab());
            }
            else if (sd instanceof JobSpawnerNbtData) { npcNbt = ((JobSpawnerNbtData) sd).save(); }
        }
        else { npcNbt = ServerCloneController.Instance.getCloneData(player, name, tab); }
        if (npcNbt != null) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag("NPCData", npcNbt);
            Packets.send(player, new PacketGuiData(compound));
        }
        CustomNpcs.debugData.end("Packets");
    }

}