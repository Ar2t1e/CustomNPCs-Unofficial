package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.client.gui.select.SubGuiSoundSelection;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSoundGUIOpen extends PacketBasic {

   protected static int channelId;
   public PacketSoundGUIOpen() { }

   @Override
   public void decode(FriendlyByteBuf buf) { }

   @Override
   public void encode(FriendlyByteBuf buf) { }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      try { Minecraft.getMinecraft().displayGuiScreen(new SubGuiSoundSelection(Minecraft.getMinecraft().currentScreen, 0, null, "")); }
      catch (Exception e) { LogWriter.error(e); }
      CustomNpcs.debugData.end("Packets");
   }

}
