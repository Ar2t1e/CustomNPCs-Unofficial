package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketNpcTransform extends PacketServerBasic {

   protected static int channelId;
   private boolean isActive;

   public SPacketNpcTransform() { }

   public SPacketNpcTransform(boolean isActiveIn) { isActive = isActiveIn; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.NPC_ADVANCED; }

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
