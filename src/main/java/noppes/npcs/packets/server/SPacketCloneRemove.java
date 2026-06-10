package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketCloneRemove extends PacketServerBasic {

   protected static int channelId;
   private String name;
   private int tab;

   public SPacketCloneRemove() { }

   public SPacketCloneRemove(String nameIn, int tabIn) {
      name = nameIn;
      tab = tabIn;
   }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.cloner; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_CLONE); }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeUtf(name);
      buf.writeInt(tab);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      name = buf.readUtf();
      tab = buf.readInt();
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      ServerCloneController.Instance.removeClone(name, tab);
      SPacketCloneList.sendList(player, tab);
      CustomNpcs.debugData.end("Packets");
   }

}
