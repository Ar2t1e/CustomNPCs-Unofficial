package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiClose extends PacketBasic {

   protected static int channelId;
   public NBTTagCompound data;

   public PacketGuiClose(NBTTagCompound dataIn) { data = dataIn; }

   public PacketGuiClose() { this(new NBTTagCompound()); }

   @Override
   public void decode(FriendlyByteBuf buf) { data = buf.readNbt(); }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeNbt(data); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() { Client.processPacket(this); }

}
