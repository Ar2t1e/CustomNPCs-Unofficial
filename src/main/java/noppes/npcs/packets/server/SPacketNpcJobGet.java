package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketNpcJobGet extends PacketServerBasic {

   protected static int channelId;

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.NPC_GUI; }

   public static void encode(SPacketNpcJobGet ignoredMsg, FriendlyByteBuf ignoredBuf) { }

   public static SPacketNpcJobGet decode(FriendlyByteBuf ignoredBuf) { return new SPacketNpcJobGet(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (npc.job.getType() != 0) {
         CompoundTag compound = new CompoundTag();
         compound.putBoolean("JobData", true);
         npc.job.save(compound);
         Packets.send(player, new PacketGuiData(compound));
      }
      CustomNpcs.debugData.end("Packets");
   }

}
