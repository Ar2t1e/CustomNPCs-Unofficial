package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.client.PacketStopSound;
import noppes.npcs.roles.JobBard;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketNpcDelete;

import java.util.Collections;
import java.util.List;

public class SPacketNpcDelete extends PacketServerBasic {

   protected static int channelId;

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_DELETE); }

   public static void encode(SPacketNpcDelete ignoredMsg, FriendlyByteBuf ignoredBuf) { }

   public static SPacketNpcDelete decode(FriendlyByteBuf ignoredBuf) { return new SPacketNpcDelete(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (npc.job instanceof JobBard job) {
         Packets.sendNearby(npc, new PacketStopSound(job.song, (job.isStreamer ? SoundSource.AMBIENT : SoundSource.MUSIC).ordinal()));
      }
      Packets.sendNearby(npc, new PacketNpcDelete(npc.getId()));
      npc.delete();
      CustomNpcs.debugData.end("Packets");
   }

}
