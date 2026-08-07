package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSync extends PacketBasic {

   protected static int channelId;
   public int type;
   public NBTTagCompound data;
   public boolean syncEnd;

   public PacketSync() { }

   public PacketSync(int typeIn, NBTTagCompound dataIn, boolean syncEndIn) {
      type = typeIn;
      data = dataIn;
      syncEnd = syncEndIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      type = buf.readInt();
      data = buf.readNbt();
      syncEnd = buf.readBoolean();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(type);
      buf.writeNbt(data);
      buf.writeBoolean(syncEnd);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() { Client.processPacket(this); }

}
