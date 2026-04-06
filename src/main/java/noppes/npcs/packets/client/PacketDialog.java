package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.player.GuiDialogInteract;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketDialog extends PacketBasic {

   protected static int channelId;
   private int entityId;
   private int dialogId;

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
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      WorldClient world = Minecraft.getMinecraft().world;
      if (world == null) { return; }
      Entity entity = world.getEntityByID(entityId);
      if (entity instanceof EntityNPCInterface) {
         Dialog dialog = DialogController.instance.dialogs.get(dialogId);
         openDialog(dialog, (EntityNPCInterface) entity, player);
      }
      CustomNpcs.debugData.end("Packets");
   }

   public static void openDialog(Dialog dialog, EntityNPCInterface npc, EntityPlayer player) {
      GuiScreen gui = Minecraft.getMinecraft().currentScreen;
      if (!(gui instanceof GuiDialogInteract)) { CustomNpcs.proxy.openGui(player, new GuiDialogInteract(npc, dialog)); }
      else { ((GuiDialogInteract) gui).appendDialog(dialog); }
   }

}
