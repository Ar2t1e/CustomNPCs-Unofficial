package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiUpdate extends PacketBasic {

   protected static int channelId;

   @Override
   public void decode(FriendlyByteBuf buf) { }

   @Override
   public void encode(FriendlyByteBuf buf) { }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.currentScreen instanceof IGuiInterface) { mc.currentScreen.setWorldAndResolution(mc, mc.currentScreen.width, mc.currentScreen.height); }
      CustomNpcs.debugData.end("Packets");
   }

}
