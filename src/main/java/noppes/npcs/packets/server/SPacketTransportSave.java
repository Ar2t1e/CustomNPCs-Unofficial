package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.roles.RoleTransporter;

import java.util.Collections;
import java.util.List;

public class SPacketTransportSave extends PacketServerBasic {

   protected static int channelId;
   private final int category;
   private final CompoundTag data;

   public SPacketTransportSave(int categoryIn, CompoundTag dataIn) {
      data = dataIn;
      category = categoryIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public boolean requiresNpc() { return true; }

   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_ADVANCED); }

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
      TransportLocation location = TransportController.getInstance().saveLocation(category, data, npc);
      if (location != null && npc.role.getType() == 4) {
         RoleTransporter role = (RoleTransporter) npc.role;
         role.setTransport(location);
      }
      CustomNpcs.debugData.end("Packets");
   }
}
