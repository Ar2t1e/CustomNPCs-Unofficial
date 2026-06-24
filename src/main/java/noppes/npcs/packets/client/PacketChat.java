package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.PacketBasic;

public class PacketChat extends PacketBasic {

   protected static int channelId;
   private ITextComponent message;

   public PacketChat() { }

   public PacketChat(Component messageIn) { message = messageIn.getParent(); }

   @Override
   public void decode(FriendlyByteBuf buf) { message = buf.readComponent(); }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeComponent(message); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      player.sendMessage(message);
      CustomNpcs.debugData.end("Packets");
   }

}
