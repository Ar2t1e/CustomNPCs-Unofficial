package noppes.npcs.packets.server;

import java.util.Vector;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiScrollList;

public class SPacketLinkedRemove extends PacketServerBasic {

   protected static int channelId;
   private final String name;

   public SPacketLinkedRemove(String nameIn) { name = nameIn; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_LINKED; }

   public static void encode(SPacketLinkedRemove msg, FriendlyByteBuf buf) { buf.writeUtf(msg.name); }

   public static SPacketLinkedRemove decode(FriendlyByteBuf buf) { return new SPacketLinkedRemove(buf.readUtf()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      LinkedNpcController.Instance.removeData(name);
      Vector<String> list = new Vector<>();
      for (LinkedNpcController.LinkedData data : LinkedNpcController.Instance.list) { list.add(data.name); }
      NoppesUtilServer.sendScrollData(player, list);
      CustomNpcs.debugData.end("Packets");
   }

}
