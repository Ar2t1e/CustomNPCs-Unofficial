package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.SoundCategory;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.client.PacketStopSound;
import noppes.npcs.roles.JobBard;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketNpcDelete;

public class SPacketNpcDelete extends PacketServerBasic {

   protected static int channelId;

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.NPC_DELETE; }

   @Override
   public void encode(FriendlyByteBuf buf) { }

   @Override
   public void decode(FriendlyByteBuf buf) { }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (npc.job instanceof JobBard) {
         Packets.sendNearby(npc, new PacketStopSound(((JobBard) npc.job).song,
                 (((JobBard) npc.job).isStreamer ? SoundCategory.AMBIENT : SoundCategory.MUSIC).ordinal()));
      }
      Packets.sendNearby(npc, new PacketNpcDelete(npc.getEntityId()));
      npc.delete();
      CustomNpcs.debugData.end("Packets");
   }

}
