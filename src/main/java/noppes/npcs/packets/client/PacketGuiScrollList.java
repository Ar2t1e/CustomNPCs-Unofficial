package noppes.npcs.packets.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiScrollList extends PacketBasic {

   protected static final Map<UUID, Vector<String>> listData = new HashMap<>();
   protected static int channelId;

   private final Vector<String> data;
   private final UUID id;
   private final int step;
   private final int size;

   public PacketGuiScrollList(Vector<String> dataIn, UUID idIn, int stepIn, int sizeIn) {
      id = idIn;
      data = dataIn;
      step = stepIn;
      size = sizeIn;
   }

   public static void encode(PacketGuiScrollList msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.data.size());
      for (String s : msg.data) { buf.writeUtf(s); }
      buf.writeUUID(msg.id);
      buf.writeInt(msg.step);
      buf.writeInt(msg.size);
   }

   public static PacketGuiScrollList decode(FriendlyByteBuf buf) {
      Vector<String> data = new Vector<>();
      int size = buf.readInt();
      for(int i = 0; i < size; ++i) { data.add(buf.readUtf()); }
      return new PacketGuiScrollList(data, buf.readUUID(), buf.readInt(), buf.readInt());
   }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (!listData.containsKey(id)) { listData.put(id, new Vector<>()); }
      listData.get(id).addAll(data);
      if (step == size) {
         Screen gui = Minecraft.getInstance().screen;
         while (gui instanceof IGuiInterface guiMod && guiMod.hasSubGui()) { gui = guiMod.getSubGui(); }
         Vector<String> list = listData.getOrDefault(id, new Vector<>());
         if (gui instanceof IScrollData guiData) { guiData.setData(list, null); }
         listData.remove(id);
      }
      CustomNpcs.debugData.end("Packets");
   }

}
