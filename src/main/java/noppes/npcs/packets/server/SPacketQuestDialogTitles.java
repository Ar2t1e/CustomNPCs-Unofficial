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

public class SPacketQuestDialogTitles extends PacketServerBasic {

   protected static int channelId;
   private final int dialogId;

   public SPacketQuestDialogTitles(int dialogIdIn) { dialogId = dialogIdIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_QUEST); }

   public static void encode(SPacketQuestDialogTitles msg, FriendlyByteBuf buf) { buf.writeInt(msg.dialogId); }

   public static SPacketQuestDialogTitles decode(FriendlyByteBuf buf) { return new SPacketQuestDialogTitles(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Dialog dialog = DialogController.instance.dialogs.get(dialogId);
      if (dialog != null) {
         CompoundTag compound = new CompoundTag();
         compound.putString("Title", dialog.title);
         compound.putInt("DialogID", dialog.id);
         Packets.send(player, new PacketGuiData(compound));
      }
      CustomNpcs.debugData.end("Packets");
   }

}
