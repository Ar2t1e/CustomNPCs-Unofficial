package noppes.npcs.packets.client;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiOpen extends PacketBasic {

   protected static int channelId;
   private final EnumGuiType gui;
   private FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());

   public PacketGuiOpen(EnumGuiType guiIn, BlockPos pos) {
      gui = guiIn;
      buffer.writeBlockPos(pos);
   }

   public PacketGuiOpen(EnumGuiType guiIn, FriendlyByteBuf bufferIn) {
      gui = guiIn;
      buffer = bufferIn;
   }

   public static void encode(PacketGuiOpen msg, FriendlyByteBuf buf) {
      buf.writeEnum(msg.gui);
      buf.writeBytes(msg.buffer.nioBuffer());
   }

   public static PacketGuiOpen decode(FriendlyByteBuf buf) {
      return new PacketGuiOpen(buf.readEnum(EnumGuiType.class), new FriendlyByteBuf(buf.readBytes(buf.readableBytes())));
   }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      CustomNpcs.proxy.openGui(NoppesUtilServer.getEditingNpc(player), gui, buffer);
      CustomNpcs.debugData.end("Packets");
   }

}
