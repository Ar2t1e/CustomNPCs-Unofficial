package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.SpawnController;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketNaturalSpawnGetAll extends PacketServerBasic {

   protected static int channelId;

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return null; }

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
