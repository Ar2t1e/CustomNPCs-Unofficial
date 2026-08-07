package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSyncUpdate extends PacketBasic {

   protected static int channelId;
   public int id;
   public int type;
   public NBTTagCompound data;

   public PacketSyncUpdate() { }

   public PacketSyncUpdate(int idIn, int typeIn, NBTTagCompound dataIn) {
      id = idIn;
      type = typeIn;
      data = dataIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      id = buf.readInt();
      type = buf.readInt();
      data = buf.readAnySizeNbt();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(id);
      buf.writeInt(type);
      buf.writeNbt(data);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() { Client.processPacket(this); }

}
