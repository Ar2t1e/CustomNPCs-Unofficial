package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

import java.util.Collections;
import java.util.List;

public class SPacketNpcDialogSet extends PacketServerBasic {

   protected static int channelId;
   private int slot;
   private int id;

   public SPacketNpcDialogSet() { }

   public SPacketNpcDialogSet(int slotIn, int idIn) {
      slot = slotIn;
      id = idIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_ADVANCED); }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(slot);
      buf.writeInt(id);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      slot = buf.readInt();
      id = buf.readInt();
   }

   @Override
   public int getChannelId() { return channelId; }

   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Packets.send(player, new PacketGuiData(NoppesUtilServer.setNpcDialog(slot, id, player)));
      CustomNpcs.debugData.end("Packets");
   }

}
