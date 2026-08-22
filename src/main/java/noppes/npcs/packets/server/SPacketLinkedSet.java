package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketLinkedSet extends PacketServerBasic {

   protected static int channelId;
   private String name;

   public SPacketLinkedSet() { }

   public SPacketLinkedSet(String nameIn) { name = nameIn; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_ADVANCED); }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeUtf(name); }

   @Override
   public void decode(FriendlyByteBuf buf) { name = buf.readUtf(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      npc.linkedName = name;
      LinkedNpcController.Instance.loadNpcData(npc);
      CustomNpcs.debugData.end("Packets");
   }

}
