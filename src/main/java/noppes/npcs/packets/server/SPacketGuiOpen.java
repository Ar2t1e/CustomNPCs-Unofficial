package noppes.npcs.packets.server;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.item.INPCToolItem;
import noppes.npcs.api.item.ISpecBuilder;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerTransportData;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiOpen;
import noppes.npcs.roles.RoleTransporter;
import noppes.npcs.util.CustomNPCsScheduler;

public class SPacketGuiOpen extends PacketServerBasic {

   protected static int channelId;
   private final EnumGuiType type;
   private final BlockPos pos;

   public SPacketGuiOpen(EnumGuiType typeIn, BlockPos posIn) {
      type = typeIn;
      pos = posIn;
   }

   @Override
   public boolean requiresNpc() { return false; }


   @Override
   public List<PermissionNode<Boolean>> getPermission() { return null; }

   @Override
   public boolean toolAllowed(ItemStack item) {
      return item.getItem() instanceof INPCToolItem || (item.getItem() instanceof ISpecBuilder && player.isCreative());
   }

   public static void encode(SPacketGuiOpen msg, FriendlyByteBuf buf) {
      buf.writeEnum(msg.type);
      buf.writeBlockPos(msg.pos);
   }

   public static SPacketGuiOpen decode(FriendlyByteBuf buf) {
      return new SPacketGuiOpen(buf.readEnum(EnumGuiType.class), buf.readBlockPos());
   }

   @Override
   public int getChannelId() { return channelId; }

   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      sendOpenGui(player, type, npc, pos);
      CustomNpcs.debugData.end("Packets");
   }

   public static void sendOpenGui(ServerPlayer player, EnumGuiType gui, EntityNPCInterface npc, BlockPos pos) {
      NoppesUtilServer.setEditingNpc(player, npc);
      NoppesUtilServer.sendExtraData(player, npc, gui);
      CustomNPCsScheduler.runTack(() -> {
         if (player.getServer() != null) {
            player.getServer().submit(() -> {
               if (!gui.hasContainer) { Packets.send(player, new PacketGuiOpen(gui, pos)); }
               else {
                  NoppesUtilServer.openContainerGui(player, gui, (buffer) -> {
                     buffer.writeInt(npc != null ? npc.getId() : -1);
                     buffer.writeBlockPos(pos);
                  });
                  ArrayList<String> list = getScrollData(player, gui, npc);
                  if (list != null && !list.isEmpty()) { NoppesUtilServer.sendScrollData(player, list); }
               }
            });
         }
      }, 200);
   }

   private static ArrayList<String> getScrollData(Player player, EnumGuiType gui, EntityNPCInterface npc) {
      if (gui == EnumGuiType.PlayerTransporter) {
         RoleTransporter role = (RoleTransporter)npc.role;
         ArrayList<String> list = new ArrayList<>();
         TransportLocation location = role.getLocation();
         if (location != null) {
            String name = location.name;
            for (TransportLocation loc : location.category.getDefaultLocations()) {
               if (!list.contains(loc.name)) { list.add(loc.name); }
            }
            PlayerTransportData playerdata = PlayerData.get(player).transportData;
            for (int i : playerdata.transports) {
               TransportLocation loc = TransportController.getInstance().getTransport(i);
               if (loc != null && location.category.locations.containsKey(loc.id) && !list.contains(loc.name)) { list.add(loc.name); }
            }
            list.remove(name);
         }
         return list;
      }
      return null;
   }

}
