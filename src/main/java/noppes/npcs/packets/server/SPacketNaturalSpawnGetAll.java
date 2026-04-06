package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.SpawnController;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketNaturalSpawnGetAll extends PacketServerBasic {

   protected static int channelId;
   public static void encode(SPacketNaturalSpawnGetAll ignoredMsg, FriendlyByteBuf ignoredBuf) { }

   public static SPacketNaturalSpawnGetAll decode(FriendlyByteBuf ignoredBuf) { return new SPacketNaturalSpawnGetAll(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      NoppesUtilServer.sendScrollData(player, SpawnController.instance.getScroll());
      CustomNpcs.debugData.end("Packets");
   }

}
