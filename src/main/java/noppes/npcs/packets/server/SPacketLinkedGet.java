package noppes.npcs.packets.server;

import java.util.Vector;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiScrollList;
import noppes.npcs.packets.client.PacketGuiScrollSelected;

public class SPacketLinkedGet extends PacketServerBasic {

   protected static int channelId;

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
