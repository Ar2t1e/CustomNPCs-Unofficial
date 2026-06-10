package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.packets.client.PacketGuiScrollSelected;
import noppes.npcs.roles.RoleTransporter;

import java.util.Collections;
import java.util.List;

public class SPacketNpcTransportGet extends PacketServerBasic {

   protected static int channelId;

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_GUI); }

   public static void encode(SPacketNpcTransportGet ignoredMsg, FriendlyByteBuf ignoredBuf) { }

   public static SPacketNpcTransportGet decode(FriendlyByteBuf ignoredBuf) { return new SPacketNpcTransportGet(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (npc.role instanceof RoleTransporter role && role.hasTransport()) {
         TransportLocation loc = role.getLocation();
         if (loc != null) {
            Packets.send(player, new PacketGuiData(loc.save()));
            Packets.send(player, new PacketGuiScrollSelected(loc.category.title));
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
