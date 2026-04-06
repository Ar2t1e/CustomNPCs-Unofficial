package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.SpawnController;
import noppes.npcs.controllers.data.SpawnData;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.shared.common.util.LogWriter;

public class SPacketNaturalSpawnGet extends PacketServerBasic {

   protected static int channelId;
   private final int id;

   public SPacketNaturalSpawnGet(int idIn) { id = idIn; }

   public static void encode(SPacketNaturalSpawnGet msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

   public static SPacketNaturalSpawnGet decode(FriendlyByteBuf buf) { return new SPacketNaturalSpawnGet(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      SpawnData spawn = SpawnController.instance.getSpawnData(id);
      if (spawn != null) { Packets.send(player, new PacketGuiData(spawn.save(new CompoundTag()))); }
      CustomNpcs.debugData.end("Packets");
   }

}
