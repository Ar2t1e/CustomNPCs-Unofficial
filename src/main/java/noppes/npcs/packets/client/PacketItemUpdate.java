package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketItemUpdate extends PacketBasic {

   protected static int channelId;
   public int id;
   public NBTTagCompound data;

   public PacketItemUpdate() { }

   public PacketItemUpdate(int idIn, NBTTagCompound dataIn) {
      id = idIn;
      data = dataIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      id = buf.readInt();
      data = buf.readNbt();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(id);
      buf.writeNbt(data);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() { Client.processPacket(this); }

}
