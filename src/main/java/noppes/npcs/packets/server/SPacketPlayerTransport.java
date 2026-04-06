package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.roles.RoleTransporter;

public class SPacketPlayerTransport extends PacketServerBasic {

   protected static int channelId;
   private final int id;

   public SPacketPlayerTransport(int idIn) { id = idIn; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public boolean requiresNpc() { return true; }

   public static void encode(SPacketPlayerTransport msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

   public static SPacketPlayerTransport decode(FriendlyByteBuf buf) { return new SPacketPlayerTransport(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (npc.role instanceof RoleTransporter role) { role.transport(player, id); }
      CustomNpcs.debugData.end("Packets");
   }

}
