package noppes.npcs.packets.client;

import java.util.*;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.client.Client;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiScrollList extends PacketBasic {

   public static final Map<UUID, Vector<String>> listData = new HashMap<>();
   protected static int channelId;

   public Vector<String> data;
   public UUID id;
   public int step;
   public int size;

   public PacketGuiScrollList() { }

   public PacketGuiScrollList(Vector<String> dataIn, UUID idIn, int stepIn, int sizeIn) {
      id = idIn;
      data = dataIn;
      step = stepIn;
      size = sizeIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      int s = buf.readInt();
      data = new Vector<>();
      for(int i = 0; i < s; ++i) { data.add(buf.readUtf()); }
      id  = buf.readUUID();
      step = buf.readInt();
      size = buf.readInt();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(data.size());
      for (String s : data) { buf.writeUtf(s); }
      buf.writeUUID(id);
      buf.writeInt(step);
      buf.writeInt(size);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() { Client.processPacket(this); }

}
