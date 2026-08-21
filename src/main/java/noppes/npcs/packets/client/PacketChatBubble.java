package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

import javax.annotation.Nonnull;

public class PacketChatBubble extends PacketBasic {

   protected static int channelId;
   public int id;
   public ITextComponent message;
   public boolean showMessage;

   public PacketChatBubble() { }

   public PacketChatBubble(int idIn, @Nonnull Component messageIn, boolean showMessageIn) {
      id = idIn;
      message = messageIn.getParent();
      showMessage = showMessageIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      id = buf.readInt();
      message = buf.readComponent();
      showMessage = buf.readBoolean();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(id);
      buf.writeComponent(message);
      buf.writeBoolean(showMessage);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() { Client.processPacket(this); }

}
