package noppes.npcs.packets.server;

import java.util.Vector;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketLinkedRemove extends PacketServerBasic {

   protected static int channelId;
   private String name;

   public SPacketLinkedRemove() { }

   public SPacketLinkedRemove(String nameIn) { name = nameIn; }

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.GLOBAL_LINKED; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeUtf(name); }

   @Override
   public void decode(FriendlyByteBuf buf) { name = buf.readUtf(); }

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
