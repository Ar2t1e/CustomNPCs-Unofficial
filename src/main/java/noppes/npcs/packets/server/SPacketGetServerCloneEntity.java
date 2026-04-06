package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.roles.JobSpawner;
import noppes.npcs.roles.data.SpawnerNPCData;
import noppes.npcs.shared.common.PacketServerBasic;

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
    public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.NPC_ADVANCED; }

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
        if (inJob) {
            if (npc.job instanceof JobSpawner) {
                SpawnerNPCData sd = ((JobSpawner) npc.job).get(isDead).dataEntitys.get(tab);
                if (sd != null && sd.compound != null) {
                    npcNbt = sd.compound;
                    if (sd.typeClones == 2) {
                        npcNbt = ServerCloneController.Instance.getCloneData(player,
                                sd.compound.getString("ClonedName"), sd.compound.getInteger("ClonedTab"));
                    }
                }
            }
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