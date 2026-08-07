package noppes.npcs.packets.client;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiComponentUpdate extends PacketBasic {

   protected static int channelId;
   public UUID id;
   public NBTTagCompound data;

   public PacketGuiComponentUpdate() { }

   public PacketGuiComponentUpdate(UUID idIn, NBTTagCompound dataIn) {
      id = idIn;
      data = dataIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      id = buf.readUUID();
      data = buf.readNbt();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeUUID(id);
      buf.writeNbt(data);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() { Client.processPacket(this); }

}
