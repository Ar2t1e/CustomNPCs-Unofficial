package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.entity.EntityDialogNpc;
import noppes.npcs.shared.common.PacketBasic;

public class PacketDialogDummy extends PacketBasic {

   protected static int channelId;
   private String name;
   private NBTTagCompound data;

   public PacketDialogDummy() { }

   public PacketDialogDummy(String nameIn, NBTTagCompound dataIn) {
      name = nameIn;
      data = dataIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      name = buf.readUtf();
      data = buf.readNbt();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeUtf(name);
      buf.writeNbt(data);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      EntityDialogNpc npc = new EntityDialogNpc(player.world);
      npc.display.setName(Component.translatable(name).toString());
      EntityUtil.Copy(player, npc);
      Dialog dialog = new Dialog(null);
      dialog.load(data);
      PacketDialog.openDialog(dialog, npc, player);
      CustomNpcs.debugData.end("Packets");
   }

}
