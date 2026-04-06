package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

public class SPacketDialogCategoryRemove extends PacketServerBasic {

   protected static int channelId;
   private final int id;

   public SPacketDialogCategoryRemove(int idIn) { id = idIn; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_DIALOG; }

   public static void encode(SPacketDialogCategoryRemove msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

   public static SPacketDialogCategoryRemove decode(FriendlyByteBuf buf) { return new SPacketDialogCategoryRemove(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      DialogController.instance.removeCategory(id);
      Packets.send(player, new PacketGuiUpdate());
      CustomNpcs.debugData.end("Packets");
   }

}
