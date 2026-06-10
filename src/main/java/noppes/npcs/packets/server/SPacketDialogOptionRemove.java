package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketDialogOptionRemove extends PacketServerBasic {

    protected static int channelId;
    private final int slot;

    public SPacketDialogOptionRemove(int slotIn) { slot = slotIn; }

    @Override
    public boolean requiresNpc() { return false; }

    @Override
    public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

    @Override
    public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_DIALOG); }

    public static void encode(SPacketDialogOptionRemove msg, FriendlyByteBuf buf) { buf.writeInt(msg.slot); }

    public static SPacketDialogOptionRemove decode(FriendlyByteBuf buf) { return new SPacketDialogOptionRemove(buf.readInt()); }

    @Override
    public int getChannelId() { return channelId; }

    @Override
    protected void handle() {
        CustomNpcs.debugData.start("Packets");
        if (slot >= 0 && slot < npc.dialogs.length) {
            Dialog dialog = DialogController.instance.get(npc.dialogs[slot]);
            if (dialog != null) { dialog.removeNpc(slot, npc); }
            int[] newIDs = new int[npc.dialogs.length - 1];
            for (int i = 0, j = 0; i < npc.dialogs.length; i++) {
                if (i == slot) { continue; }
                newIDs[j] = npc.dialogs[i];
                j++;
            }
            npc.dialogs = newIDs;
        }
        NoppesUtilServer.sendNpcDialogs(player);
        CustomNpcs.debugData.end("Packets");
    }

}
