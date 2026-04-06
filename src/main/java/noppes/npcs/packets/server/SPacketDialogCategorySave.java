package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

public class SPacketDialogCategorySave extends PacketServerBasic {

   protected static int channelId;
   private final CompoundTag data;

   public SPacketDialogCategorySave(CompoundTag dataIn) { data = dataIn; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_DIALOG; }

   public static void encode(SPacketDialogCategorySave msg, FriendlyByteBuf buf) { buf.writeNbt(msg.data); }

   public static SPacketDialogCategorySave decode(FriendlyByteBuf buf) { return new SPacketDialogCategorySave(buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   public void handle() {
      CustomNpcs.debugData.start("Packets");
      DialogCategory category = new DialogCategory();
      category.load(data);
      DialogController.instance.saveCategory(category);
      Packets.send(player, new PacketGuiUpdate());
      CustomNpcs.debugData.end("Packets");
   }

}
