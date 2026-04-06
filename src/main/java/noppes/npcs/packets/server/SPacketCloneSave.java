package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketCloneSave extends PacketServerBasic {

   protected static int channelId;
   private String name;
   private int tab;

   public SPacketCloneSave() { }

   public SPacketCloneSave(String nameIn, int tabIn) {
      name = nameIn;
      tab = tabIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.cloner; }

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.NPC_CLONE; }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeUtf(name);
      buf.writeInt(tab);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      name = buf.readUtf();
      tab = buf.readInt();
   }

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
