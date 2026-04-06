package noppes.npcs.packets.client;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiOpen extends PacketBasic {

   protected static int channelId;
   private EnumGuiType gui;
   private FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());

   public PacketGuiOpen() {}

   public PacketGuiOpen(EnumGuiType guiIn, BlockPos pos) {
      gui = guiIn;
      buffer.writeBlockPos(pos);
   }

   public PacketGuiOpen(EnumGuiType guiIn, FriendlyByteBuf bufferIn) {
      gui = guiIn;
      buffer = bufferIn;
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeEnum(gui);
      buf.writeBytes(buffer.nioBuffer());
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      gui = buf.readEnum(EnumGuiType.class);
      buffer = new FriendlyByteBuf(buf.readBytes(buf.readableBytes()));
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      CustomNpcs.proxy.openGui(NoppesUtilServer.getEditingNpc(player), gui, buffer);
      CustomNpcs.debugData.end("Packets");
   }

}
