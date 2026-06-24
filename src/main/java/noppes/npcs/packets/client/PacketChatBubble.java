package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.ClientEventHandler;
import noppes.npcs.client.RenderChatMessages;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.gui.util.NoppesStringUtils;
import noppes.npcs.shared.common.PacketBasic;

import javax.annotation.Nonnull;

public class PacketChatBubble extends PacketBasic {

   protected static int channelId;
   private int id;
   private ITextComponent message;
   private boolean showMessage;

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
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Entity entity = Minecraft.getMinecraft().world.getEntityByID(id);
      if (CustomNpcs.EnableChatBubbles && entity instanceof EntityNPCInterface) {
         EntityNPCInterface npc = (EntityNPCInterface) entity;
         if (npc.messages == null) { npc.messages = new RenderChatMessages(); }
         String text = NoppesStringUtils.formatText(message.getFormattedText(), player, npc);
         npc.messages.addMessage(text, npc);
         if (showMessage) { player.sendMessage(new TextComponentString(npc.getName() + ": " + text)); }
      }
      else if (CustomNpcs.EnablePlayerChatBubbles && entity instanceof EntityPlayer) {
         EntityPlayer pl = (EntityPlayer) entity;
         if (!ClientEventHandler.chatMessages.containsKey(pl)) { ClientEventHandler.chatMessages.put(pl, new RenderChatMessages()); }
         ClientEventHandler.chatMessages.get(pl).addMessage(message.getFormattedText(), pl);
      }
      CustomNpcs.debugData.end("Packets");
   }

}
