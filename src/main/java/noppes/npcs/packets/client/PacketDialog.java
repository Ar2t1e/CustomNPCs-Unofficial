package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.Client;
import noppes.npcs.client.gui.player.GuiDialogInteract;
import noppes.npcs.client.gui.player.moderngui.GuiDialogModern;
import noppes.npcs.client.gui.player.moderngui.GuiQuestModern;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketDialog extends PacketBasic {

   protected static int channelId;
   public int entityId;
   public int dialogId;

   public PacketDialog() { }

   public PacketDialog(int entityIdIn, int dialogIdIn) {
      entityId = entityIdIn;
      dialogId = dialogIdIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      entityId = buf.readInt();
      dialogId = buf.readInt();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(entityId);
      buf.writeInt(dialogId);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() { Client.processPacket(this); }

   public static void openDialog(Dialog dialog, EntityNPCInterface npc, EntityPlayer player) {
      GuiScreen gui = Minecraft.getMinecraft().currentScreen;
      if (gui instanceof GuiDialogInteract) { ((GuiDialogInteract) gui).appendDialog(dialog); }
      else if (CustomNpcs.EnableNewDialogSystem) {
         if (!(gui instanceof GuiQuestModern) && dialog.hasQuest()) { CustomNpcs.proxy.openGui(player, new GuiQuestModern(npc, dialog.getQuest(), dialog, -2)); }
         else { CustomNpcs.proxy.openGui(player, new GuiDialogModern(npc, dialog)); }
      }
      else { CustomNpcs.proxy.openGui(player, new GuiDialogInteract(npc, dialog)); }
   }

}
