package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketAchievement extends PacketBasic {

   protected static int channelId;
   public ITextComponent title;
   public ITextComponent message;
   public NBTTagCompound compound; // quest progress data:
   public int type;

   public PacketAchievement() {}

   public PacketAchievement(Component titleIn, Component messageIn, int typeIn, NBTTagCompound compoundIn) {
      title = titleIn.getParent();
      message = messageIn.getParent();
      type = typeIn;
      compound = compoundIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      title = buf.readComponent();
      message = buf.readComponent();
      type = buf.readInt();
      compound = buf.readNbt();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeComponent(title);
      buf.writeComponent(message);
      buf.writeInt(type);
      buf.writeNbt(compound);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() { Client.processPacket(this); }

}
