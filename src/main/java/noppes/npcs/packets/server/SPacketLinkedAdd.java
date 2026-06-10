package noppes.npcs.packets.server;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketLinkedAdd extends PacketServerBasic {

   protected static int channelId;
   private String name;

   public SPacketLinkedAdd() { }

   public SPacketLinkedAdd(String nameIn) { name = nameIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_LINKED); }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeUtf(name); }

   @Override
   public void decode(FriendlyByteBuf buf) { name = buf.readUtf(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      LinkedNpcController.Instance.addData(name);
      Vector<String> list = new Vector<>();
      for (LinkedNpcController.LinkedData data : LinkedNpcController.Instance.list) {
         list.add(data.name);
      }
      NoppesUtilServer.sendScrollData(player, list);
      CustomNpcs.debugData.start("Packets");
   }

}
