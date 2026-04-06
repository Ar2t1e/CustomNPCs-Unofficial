package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

public class SPacketDialogRemove extends PacketServerBasic {

   protected static int channelId;
   private final int id;

   public SPacketDialogRemove(int idIn) { id = idIn; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_DIALOG; }

   public static void encode(SPacketDialogRemove msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

   public static SPacketDialogRemove decode(FriendlyByteBuf buf) { return new SPacketDialogRemove(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Dialog dialog = DialogController.instance.dialogs.get(id);
      if (dialog != null && dialog.category != null) {
         DialogController.instance.removeDialog(dialog);
         Packets.send(player, new PacketGuiUpdate());
      }
      CustomNpcs.debugData.end("Packets");
   }

}
