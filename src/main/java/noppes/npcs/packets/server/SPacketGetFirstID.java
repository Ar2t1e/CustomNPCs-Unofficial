package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SPacketGetFirstID extends PacketServerBasic {

    protected static int channelId;
    private int type;
    private int excludedId;

    public SPacketGetFirstID() { }

    public SPacketGetFirstID(int typeIn, int excludedIdIn) {
        type = typeIn;
        excludedId = excludedIdIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_FACTION); }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(type);
        buf.writeInt(excludedId);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        type = buf.readInt();
        excludedId = buf.readInt();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    public void handle() {
        CustomNpcs.debugData.start("Packets");
        List<Integer> ids;
        switch (type) {
            case 0: ids = new ArrayList<>(FactionController.instance.factions.keySet()); break;
            case 1: ids = new ArrayList<>(DialogController.instance.dialogs.keySet()); break;
            default: ids = new ArrayList<>(QuestController.instance.quests.keySet()); break;
        }
        Collections.sort(ids);
        int id = 1;
        for (int i : ids) {
            if (id == i && id != excludedId) {
                id++;
                continue;
            }
            break;
        }
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("MinimumID", id);
        Packets.send(player, new PacketGuiData(compound));
        CustomNpcs.debugData.end("Packets");
    }

}
