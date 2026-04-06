package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketNpcDialogSet extends PacketServerBasic {

   protected static int channelId;
   private final int slot;
   private final int id;

   public SPacketNpcDialogSet(int slotIn, int idIn) {
      slot = slotIn;
      id = idIn;
   }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.NPC_ADVANCED; }

   public static void encode(SPacketNpcDialogSet msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.slot);
      buf.writeInt(msg.id);
   }

   public static SPacketNpcDialogSet decode(FriendlyByteBuf buf) {
      return new SPacketNpcDialogSet(buf.readInt(), buf.readInt());
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Packets.send(player, new PacketGuiData(NoppesUtilServer.setNpcDialog(slot, id, player)));
      CustomNpcs.debugData.end("Packets");
   }

}
