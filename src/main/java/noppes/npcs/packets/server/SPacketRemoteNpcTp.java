package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketRemoteNpcTp extends PacketServerBasic {

   protected static int channelId;
   private int entityId;

   public SPacketRemoteNpcTp() { }

   public SPacketRemoteNpcTp(int entityIdIn) { entityId = entityIdIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeInt(entityId); }

   @Override
   public void decode(FriendlyByteBuf buf) { entityId = buf.readInt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Entity entity = player.world.getEntityByID(entityId);
      if (entity != null) {
         if (entity instanceof EntityNPCInterface) { npc = (EntityNPCInterface) entity; }
         SPacketDimensionTeleport.teleportPlayer(player, player.world.provider.getDimension(),
                 entity.posX, entity.posY, entity.posZ, player.rotationYaw, player.rotationPitch);
      }
      CustomNpcs.debugData.end("Packets");
   }

}
