package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketMenuClose extends PacketServerBasic {

   protected static int channelId;

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_GUI); }

   public static void encode(SPacketMenuClose ignoredMsg, FriendlyByteBuf ignoredBuf) { }

   public static SPacketMenuClose decode(FriendlyByteBuf ignoredBuf) { return new SPacketMenuClose(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      npc.reset();
      if (npc.linkedData != null) { LinkedNpcController.Instance.saveNpcData(npc); }
      NoppesUtilServer.setEditingNpc(player, null);
      CustomNpcs.debugData.end("Packets");
   }

}
