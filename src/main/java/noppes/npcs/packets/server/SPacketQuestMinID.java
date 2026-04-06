package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SPacketQuestMinID extends PacketServerBasic {

    protected static int channelId;
    private int questId;

    public SPacketQuestMinID() { }

    public SPacketQuestMinID(int questIdIn) { questId = questIdIn; }

    @Override
    public boolean toolAllowed(ItemStack item){ return true; }

    @Override
    public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.GLOBAL_QUEST; }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(questId); }

    @Override
    public void decode(FriendlyByteBuf buf) { questId = buf.readInt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        List<Integer> ids = new ArrayList<>(QuestController.instance.quests.keySet());
        Collections.sort(ids);
        int id = 1;
        for (int i : ids) {
            if (id == i && id != questId) {
                id++;
                continue;
            }
            break;
        }
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("MinimumID", id);
        Packets.sendServer(new PacketGuiData(compound));
        CustomNpcs.debugData.end("Packets");
    }

}