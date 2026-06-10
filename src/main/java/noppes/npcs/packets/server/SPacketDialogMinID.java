package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SPacketDialogMinID extends PacketServerBasic {

    protected static int channelId;
    private int id;

    public SPacketDialogMinID() { }

    public SPacketDialogMinID(int idIn) { id = idIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return true; }

    @Override
    public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_DIALOG); }

    @Override
    public void encode(FriendlyByteBuf buf) { buf.writeInt(id); }

    @Override
    public void decode(FriendlyByteBuf buf) { id = buf.readInt(); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        List<Integer> ids = new ArrayList<>(DialogController.instance.dialogs.keySet());
        Collections.sort(ids);
        int idNow = 1;
        for (int i : ids) {
            if (idNow == i && idNow != id) {
                idNow++;
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
