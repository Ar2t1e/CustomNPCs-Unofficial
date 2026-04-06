package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

public class SPacketDialogSave extends PacketServerBasic {

   protected static int channelId;
   private final int id;
   private final CompoundTag data;

   public SPacketDialogSave(int idIn, CompoundTag dataIn) {
      id = idIn;
      data = dataIn;
   }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_DIALOG; }

   public static void encode(SPacketDialogSave msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.id);
      buf.writeNbt(msg.data);
   }

   public static SPacketDialogSave decode(FriendlyByteBuf buf) { return new SPacketDialogSave(buf.readInt(), buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   public void handle() {
      CustomNpcs.debugData.start("Packets");
      DialogCategory category = DialogController.instance.categories.get(id);
      if (category != null) {
         Dialog dialog = new Dialog(category);
         dialog.load(data);
         DialogController.instance.saveDialog(category, dialog);
         Packets.send(player, new PacketGuiUpdate());
      }
      CustomNpcs.debugData.end("Packets");
   }

}
