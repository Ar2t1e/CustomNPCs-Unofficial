package noppes.npcs.packets.server;

import java.util.Collections;
import java.util.List;
import java.util.Vector;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketLinkedRemove extends PacketServerBasic {

   protected static int channelId;
   private final String name;

   public SPacketLinkedRemove(String nameIn) { name = nameIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_LINKED); }

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
