package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMailData;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketPlayerMailGet extends PacketServerBasic {

   protected static int channelId;

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   public static void encode(SPacketPlayerMailGet ignoredMsg, FriendlyByteBuf ignoredBuf) { }

   public static SPacketPlayerMailGet decode(FriendlyByteBuf ignoredBuf) { return new SPacketPlayerMailGet(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Packets.send(player, new PacketGuiData(PlayerData.get(player).mailData.save(new CompoundTag())));
      CustomNpcs.debugData.end("Packets");
   }

}
