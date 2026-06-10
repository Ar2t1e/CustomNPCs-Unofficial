package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

import java.util.Collections;
import java.util.List;

public class SPacketDialogOptionMove extends PacketServerBasic {

    protected static int channelId;
    private final int slot;
    private final boolean isUp;

    public SPacketDialogOptionMove(int slotIn, boolean isUpIn) {
        slot = slotIn;
        isUp = isUpIn;
    }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_DIALOG); }

    public static void encode(SPacketDialogOptionMove msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.slot);
        buf.writeBoolean(msg.isUp);
    }

    public static SPacketDialogOptionMove decode(FriendlyByteBuf buf) { return new SPacketDialogOptionMove(buf.readInt(), buf.readBoolean()); }

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
            CompoundTag compound = new CompoundTag();
            compound.putInt("Id", newIDs[s]);
            compound.putInt("Slot", s);
            compound.putString("Category", d != null ? d.category.title : "");
            compound.putString("Title", d != null ? d.title : "null");
            Packets.send(player, new PacketGuiData(compound));
        }
        npc.dialogs = newIDs;
        CustomNpcs.debugData.end("Packets");
    }

}
