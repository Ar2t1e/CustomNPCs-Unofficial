package noppes.npcs.packets.server;

import java.util.Vector;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiScrollList;

public class SPacketLinkedAdd extends PacketServerBasic {

   protected static int channelId;
   private final String name;

   public SPacketLinkedAdd(String nameIn) { name = nameIn; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_LINKED; }

   public static void encode(SPacketLinkedAdd msg, FriendlyByteBuf buf) { buf.writeUtf(msg.name); }

   public static SPacketLinkedAdd decode(FriendlyByteBuf buf) { return new SPacketLinkedAdd(buf.readUtf()); }

   @Override
   public int getChannelId() { return channelId; }

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
