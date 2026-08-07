package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketChat extends PacketBasic {

   protected static int channelId;
   public ITextComponent message;

   public PacketChat() { }

   public PacketChat(Component messageIn) { message = messageIn.getParent(); }

   @Override
   public void decode(FriendlyByteBuf buf) { message = buf.readComponent(); }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeComponent(message); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() { Client.processPacket(this); }

}
