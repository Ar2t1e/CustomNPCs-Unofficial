package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketRemoteNpcReset extends PacketServerBasic {

   protected static int channelId;
   private int entityId;

   public SPacketRemoteNpcReset() { }

   public SPacketRemoteNpcReset(int entityIdIn) { entityId = entityIdIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_RESET); }

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
      if (entity instanceof EntityNPCInterface) {
         npc = (EntityNPCInterface) entity;
         npc.reset();
      }
      CustomNpcs.debugData.end("Packets");
   }

}
