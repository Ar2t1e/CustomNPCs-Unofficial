package noppes.npcs.packets.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.shared.common.PacketBasic;
import noppes.npcs.shared.common.util.LogWriter;

public class PacketGuiScrollData extends PacketBasic {

   protected static final Map<UUID, Map<String, Integer>> scrollData = new HashMap<>();
   protected static int channelId;

   private final Map<String, Integer> data;
   private final UUID id;
   private final int step;
   private final int size;

   public PacketGuiScrollData(Map<String, Integer> dataIn, UUID idIn, int stepIn, int sizeIn) {
      id = idIn;
      data = dataIn;
      step = stepIn;
      size = sizeIn;
   }

   public static void encode(PacketGuiScrollData msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.data.size());
      for (Entry<String, Integer> e : msg.data.entrySet()) {
         buf.writeUtf(e.getKey());
         buf.writeInt(e.getValue());
      }
      buf.writeUUID(msg.id);
      buf.writeInt(msg.step);
      buf.writeInt(msg.size);
   }

   public static PacketGuiScrollData decode(FriendlyByteBuf buf) {
      int size = buf.readInt();
      HashMap<String, Integer> data = new HashMap<>();
      for(int i = 0; i < size; ++i) { data.put(buf.readUtf(), buf.readInt()); }
      return new PacketGuiScrollData(data, buf.readUUID(), buf.readInt(), buf.readInt());
   }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (!scrollData.containsKey(id)) { scrollData.put(id, new HashMap<>()); }
      scrollData.get(id).putAll(data);
      if (step == size) {
         Screen gui = Minecraft.getInstance().screen;
         while (gui instanceof IGuiInterface guiMod && guiMod.hasSubGui()) { gui = guiMod.getSubGui(); }
         Map<String, Integer> map = scrollData.getOrDefault(id, new HashMap<>());
         if (gui instanceof IScrollData guiData) { guiData.setData(new Vector<>(map.keySet()), map); }
         scrollData.remove(id);
      }
      CustomNpcs.debugData.end("Packets");
   }

}
