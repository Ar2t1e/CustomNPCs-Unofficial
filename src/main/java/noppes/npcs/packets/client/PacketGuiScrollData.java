package noppes.npcs.packets.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiScrollData extends PacketBasic {

   public static final Map<UUID, Map<String, Integer>> scrollData = new HashMap<>();
   protected static int channelId;

   public Map<String, Integer> data;
   public UUID id;
   public int step;
   public int size;

   public PacketGuiScrollData() { }

   public PacketGuiScrollData(Map<String, Integer> dataIn, UUID idIn, int stepIn, int sizeIn) {
      id = idIn;
      data = dataIn;
      step = stepIn;
      size = sizeIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      int s = buf.readInt();
      data = new HashMap<>();
      for(int i = 0; i < s; ++i) { data.put(buf.readUtf(), buf.readInt()); }
      id  = buf.readUUID();
      step = buf.readInt();
      size = buf.readInt();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(data.size());
      for (Entry<String, Integer> e : data.entrySet()) {
         buf.writeUtf(e.getKey());
         buf.writeInt(e.getValue());
      }
      buf.writeUUID(id);
      buf.writeInt(step);
      buf.writeInt(size);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() { Client.processPacket(this); }

}
