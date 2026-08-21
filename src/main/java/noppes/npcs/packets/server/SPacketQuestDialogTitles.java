package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
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
   private int dialogId;

   public SPacketQuestDialogTitles() { }

   public SPacketQuestDialogTitles(int dialogIdIn) { dialogId = dialogIdIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_QUEST); }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeInt(dialogId); }

   @Override
   public void decode(FriendlyByteBuf buf) { dialogId = buf.readInt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Dialog dialog = DialogController.instance.dialogs.get(dialogId);
      if (dialog == null) {
         CustomNpcs.debugData.end("Packets");
         return;
      }
      NBTTagCompound compound = new NBTTagCompound();
      compound.setString("Title", dialog.title);
      compound.setInteger("DialogID", dialog.id);
      Packets.send(player, new PacketGuiData(compound));
      CustomNpcs.debugData.end("Packets");
   }

}
