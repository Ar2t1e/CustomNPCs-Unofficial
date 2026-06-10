package noppes.npcs.packets.server;

import java.util.List;
import java.util.Vector;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiScrollSelected;

public class SPacketLinkedGet extends PacketServerBasic {

   protected static int channelId;

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return null; }

   public static void encode(SPacketLinkedGet ignoredMsg, FriendlyByteBuf ignoredBuf) { }

   public static SPacketLinkedGet decode(FriendlyByteBuf ignoredBuf) { return new SPacketLinkedGet(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Vector<String> list = new Vector<>();
      for (LinkedNpcController.LinkedData data : LinkedNpcController.Instance.list) { list.add(data.name); }
      NoppesUtilServer.sendScrollData(player, list);
      if (npc != null) { Packets.send(player, new PacketGuiScrollSelected(npc.linkedName)); }
      CustomNpcs.debugData.end("Packets");
   }

}
