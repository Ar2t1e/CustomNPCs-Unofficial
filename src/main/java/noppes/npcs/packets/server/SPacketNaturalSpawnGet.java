package noppes.npcs.packets.server;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.SpawnController;
import noppes.npcs.controllers.data.SpawnData;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketNaturalSpawnGet extends PacketServerBasic {

   protected static int channelId;
   private int id;

   public SPacketNaturalSpawnGet() { }

   public SPacketNaturalSpawnGet(int idIn) { id = idIn; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeInt(id); }

   @Override
   public void decode(FriendlyByteBuf buf) { id = buf.readInt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      SpawnData spawn = SpawnController.instance.getSpawnData(id);
      if (spawn != null) { Packets.send(player, new PacketGuiData(spawn.save(new NBTTagCompound()))); }
      CustomNpcs.debugData.end("Packets");
   }

}
