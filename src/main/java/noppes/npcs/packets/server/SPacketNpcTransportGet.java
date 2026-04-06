package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.packets.client.PacketGuiScrollSelected;
import noppes.npcs.roles.RoleTransporter;

public class SPacketNpcTransportGet extends PacketServerBasic {

   protected static int channelId;

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.NPC_GUI; }

   @Override
   public void encode(FriendlyByteBuf buf) { }

   @Override
   public void decode(FriendlyByteBuf buf) { }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (npc.role.getEnumType() == RoleType.TRANSPORTER) {
         RoleTransporter role = (RoleTransporter) npc.role;
         if (role.hasTransport()) {
            Packets.send(player, new PacketGuiData(role.getLocation().save()));
            Packets.send(player, new PacketGuiScrollSelected(role.getLocation().category.title));
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
