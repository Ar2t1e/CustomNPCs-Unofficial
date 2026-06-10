package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketNpcDelete;

import java.util.Collections;
import java.util.List;

public class SPacketRemoteNpcDelete extends PacketServerBasic {

   protected static int channelId;
   private int entityId;
   private boolean isAll;

   public SPacketRemoteNpcDelete() { }

   public SPacketRemoteNpcDelete(int entity, boolean all) {
      entityId = entity;
      isAll = all;
   }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_DELETE); }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(entityId);
      buf.writeBoolean(isAll);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      entityId = buf.readInt();
      isAll = buf.readBoolean();
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Entity entity = player.world.getEntityByID(entityId);
      if (entity != null) {
         if (entity instanceof EntityNPCInterface) { ((EntityNPCInterface) entity).delete(); }
         else { entity.setDead(); }
         Packets.sendNearby(entity, new PacketNpcDelete(entityId));
         SPacketRemoteNpcsGet.sendNearbyNpcs(player, isAll);
      }
      CustomNpcs.debugData.end("Packets");
   }

}
