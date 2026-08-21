package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketNpcTransform extends PacketServerBasic {

   protected static int channelId;
   private boolean isActive;

   public SPacketNpcTransform() { }

   public SPacketNpcTransform(boolean isActiveIn) { isActive = isActiveIn; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_ADVANCED); }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeBoolean(isActive); }

   @Override
   public void decode(FriendlyByteBuf buf) { isActive = buf.readBoolean(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (npc.transform.isValid()) { npc.transform.transform(isActive); }
      CustomNpcs.debugData.end("Packets");
   }

}
