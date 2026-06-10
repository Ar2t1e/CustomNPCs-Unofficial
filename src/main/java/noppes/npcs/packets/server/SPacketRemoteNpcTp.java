package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketRemoteNpcTp extends PacketServerBasic {

   protected static int channelId;
   private final int entityId;

   public SPacketRemoteNpcTp(int entityIdIn) { entityId = entityIdIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return null; }

   public static void encode(SPacketRemoteNpcTp msg, FriendlyByteBuf buf) { buf.writeInt(msg.entityId); }

   public static SPacketRemoteNpcTp decode(FriendlyByteBuf buf) { return new SPacketRemoteNpcTp(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Entity entity = player.level().getEntity(entityId);
      if (entity != null) {
         if (entity instanceof EntityNPCInterface) { npc = (EntityNPCInterface) entity; }
         SPacketDimensionTeleport.teleportPlayer(player, player.level().dimension(), entity.getX(), entity.getY(), entity.getZ(), player.getYRot(), player.getXRot());
      }
      CustomNpcs.debugData.end("Packets");
   }

}
