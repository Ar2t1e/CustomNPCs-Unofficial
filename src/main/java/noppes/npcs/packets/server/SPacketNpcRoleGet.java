package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketNpcRoleGet extends PacketServerBasic {

   protected static int channelId;

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public boolean requiresNpc() { return true; }

   public static void encode(SPacketNpcRoleGet ignoredMsg, FriendlyByteBuf ignoredBuf) { }

   public static SPacketNpcRoleGet decode(FriendlyByteBuf ignoredBuf) { return new SPacketNpcRoleGet(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (npc.role.getEnumType() != RoleType.NONE) {
         CompoundTag compound = new CompoundTag();
         compound.putBoolean("RoleData", true);
         Packets.send(player, new PacketGuiData(npc.role.save(compound)));
      }
      CustomNpcs.debugData.end("Packets");
   }

}
