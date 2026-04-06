package noppes.npcs.packets.server;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketDialogOptionMove extends PacketServerBasic {

    protected static int channelId;
    private int slot;
    private boolean isUp;

    public SPacketDialogOptionMove() { }

    public SPacketDialogOptionMove(int slotIn, boolean isUpIn) {
        slot = slotIn;
        isUp = isUpIn;
    }

    @Override
    public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.GLOBAL_DIALOG; }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(slot);
        buf.writeBoolean(isUp);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        slot = buf.readInt();
        isUp = buf.readBoolean();
    }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (npc == null || (isUp && slot <= 0) || (!isUp && slot >= (npc.dialogs.length - 1))) { return; }
        int[] newIDs = new int[npc.dialogs.length];
        for (int s = 0; s < npc.dialogs.length; s++) {
            if ((s + (isUp ? 1 : -1)) == slot) { newIDs[s] = npc.dialogs[s + (isUp ? 1 : -1)]; }
            else if (s == slot) { newIDs[s] = npc.dialogs[s + (isUp ? -1 : 1)]; }
            else { newIDs[s] = npc.dialogs[s]; }
            Dialog d = DialogController.instance.get(newIDs[s]);
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger("Id", newIDs[s]);
            compound.setInteger("Slot", s);
            compound.setString("Category", d != null ? d.category.title : "");
            compound.setString("Title", d != null ? d.title : "null");
            Packets.send(player, new PacketGuiData(compound));
        }
        npc.dialogs = newIDs;
        CustomNpcs.debugData.end("Packets");
    }

}
