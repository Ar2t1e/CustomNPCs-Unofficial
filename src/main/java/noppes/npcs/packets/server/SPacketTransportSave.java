package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.roles.RoleTransporter;

public class SPacketTransportSave extends PacketServerBasic {

   protected static int channelId;
   private final int category;
   private final CompoundTag data;

   public SPacketTransportSave(int categoryIn, CompoundTag dataIn) {
      data = dataIn;
      category = categoryIn;
   }

   @Override
   public boolean requiresNpc() { return true; }

   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.NPC_ADVANCED; }

   public static void encode(SPacketTransportSave msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.category);
      buf.writeNbt(msg.data);
   }

   public static SPacketTransportSave decode(FriendlyByteBuf buf) { return new SPacketTransportSave(buf.readInt(), buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      TransportLocation location = TransportController.getInstance().saveLocation(category, data, player, npc);
      if (location != null && npc.role.getType() == 4) {
         RoleTransporter role = (RoleTransporter) npc.role;
         role.setTransport(location);
      }
      CustomNpcs.debugData.end("Packets");
   }
}
