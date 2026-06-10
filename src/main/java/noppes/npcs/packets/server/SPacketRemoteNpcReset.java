package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketRemoteNpcReset extends PacketServerBasic {

   protected static int channelId;
   private final int entityId;

   public SPacketRemoteNpcReset(int entityIdIn) { entityId = entityIdIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_RESET); }

   public static void encode(SPacketRemoteNpcReset msg, FriendlyByteBuf buf) { buf.writeInt(msg.entityId); }

   public static SPacketRemoteNpcReset decode(FriendlyByteBuf buf) { return new SPacketRemoteNpcReset(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Entity entity = player.level().getEntity(entityId);
      if (entity instanceof EntityNPCInterface) {
         npc = (EntityNPCInterface) entity;
         npc.reset();
      }
      CustomNpcs.debugData.end("Packets");
   }

}
