package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketDialogDummy extends PacketBasic {

   protected static int channelId;
   public String name;
   public NBTTagCompound data;

   public PacketDialogDummy() { }

   public PacketDialogDummy(String nameIn, NBTTagCompound dataIn) {
      name = nameIn;
      data = dataIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      name = buf.readUtf();
      data = buf.readNbt();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeUtf(name);
      buf.writeNbt(data);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() { Client.processPacket(this); }

}
