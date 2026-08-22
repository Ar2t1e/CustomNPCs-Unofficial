package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiScrollSelected;

import java.util.Collections;
import java.util.List;

public class SPacketRemoteFreeze extends PacketServerBasic {

   protected static int channelId;

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_FREEZE); }

   @Override
   public void encode(FriendlyByteBuf buf) { }

   @Override
   public void decode(FriendlyByteBuf buf) { }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      CustomNpcs.FreezeNPCs = !CustomNpcs.FreezeNPCs;
      Packets.send(player, new PacketGuiScrollSelected(CustomNpcs.FreezeNPCs ? "Unfreeze Npcs" : "Freeze Npcs"));
      CustomNpcs.debugData.end("Packets");
   }

}
