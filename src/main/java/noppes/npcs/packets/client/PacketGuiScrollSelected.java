package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiScrollSelected extends PacketBasic {

   protected static int channelId;
   private final String selected;

   public PacketGuiScrollSelected(String selectedIn) {
      selected = selectedIn;
   }

   public static void encode(PacketGuiScrollSelected msg, FriendlyByteBuf buf) {
      buf.writeUtf(msg.selected);
   }

   public static PacketGuiScrollSelected decode(FriendlyByteBuf buf) {
      return new PacketGuiScrollSelected(buf.readUtf(32767));
   }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Screen gui = Minecraft.getInstance().screen;
      if (gui instanceof IScrollData scroll) { scroll.setSelected(selected); }
      CustomNpcs.debugData.end("Packets");
   }

}
