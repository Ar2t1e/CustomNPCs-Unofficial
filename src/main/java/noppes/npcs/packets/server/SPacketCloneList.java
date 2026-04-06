package noppes.npcs.packets.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketCloneList extends PacketServerBasic {

   protected static int channelId;
   private int tab;

   public SPacketCloneList() { }

   public SPacketCloneList(int tabIn) { tab = tabIn; }

   @Override
   public boolean toolAllowed(ItemStack item) {
      return item.getItem() == CustomItems.wand || item.getItem() == CustomItems.cloner || item.getItem() == CustomItems.mount;
   }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeInt(tab); }

   @Override
   public void decode(FriendlyByteBuf buf) { tab = buf.readInt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      sendList(player, tab);
      CustomNpcs.debugData.end("Packets");
   }

   public static void sendList(EntityPlayerMP player, int tab) {
      NBTTagList list = new NBTTagList();
      for (String name : ServerCloneController.Instance.getClones(tab)) { list.appendTag(new NBTTagString(name)); }
      NBTTagCompound compound = new NBTTagCompound();
      compound.setTag("List", list);
      Packets.send(player, new PacketGuiData(compound));
   }

}
