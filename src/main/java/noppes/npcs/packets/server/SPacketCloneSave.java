package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketCloneSave extends PacketServerBasic {

   protected static int channelId;
   private final String name;
   private final int tab;

   public SPacketCloneSave(String nameIn, int tabIn) {
      name = nameIn;
      tab = tabIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.cloner; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.NPC_CLONE; }

   public static void encode(SPacketCloneSave msg, FriendlyByteBuf buf) {
      buf.writeUtf(msg.name);
      buf.writeInt(msg.tab);
   }

   public static SPacketCloneSave decode(FriendlyByteBuf buf) { return new SPacketCloneSave(buf.readUtf(), buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      PlayerData data = PlayerData.get(player);
      if (data.cloned != null) {
         ServerCloneController.Instance.addClone(data.cloned, name, tab);
      }
      CustomNpcs.debugData.end("Packets");
   }

}
