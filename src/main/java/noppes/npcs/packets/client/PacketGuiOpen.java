package noppes.npcs.packets.client;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.client.Client;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiOpen extends PacketBasic {

   protected static int channelId;
   public EnumGuiType gui;
   public int windowId;
   public FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());

   public PacketGuiOpen() {}

   public PacketGuiOpen(EnumGuiType guiIn, BlockPos pos) {
      gui = guiIn;
      buffer.writeBlockPos(pos);
   }

   public PacketGuiOpen(EnumGuiType guiIn, FriendlyByteBuf bufferIn) {
      gui = guiIn;
      buffer = bufferIn;
   }

   public PacketGuiOpen(EnumGuiType guiIn, FriendlyByteBuf bufferIn, int windowIdIn) {
      gui = guiIn;
      buffer = bufferIn;
      windowId = windowIdIn;
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeEnum(gui);
      buf.writeInt(windowId);
      buf.writeBytes(buffer.nioBuffer());
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      gui = buf.readEnum(EnumGuiType.class);
      windowId = buf.readInt();
      buffer = new FriendlyByteBuf(buf.readBytes(buf.readableBytes()));
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() { Client.processPacket(this); }

}
