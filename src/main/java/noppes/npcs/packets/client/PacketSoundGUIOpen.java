package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.select.SubGuiSoundSelection;
import noppes.npcs.shared.common.PacketBasic;
import noppes.npcs.shared.common.util.LogWriter;

public class PacketSoundGUIOpen extends PacketBasic {

   protected static int channelId;

   public static void encode(PacketSoundGUIOpen ignoredMsg, FriendlyByteBuf ignoredBuf) { }

   public static PacketSoundGUIOpen decode(FriendlyByteBuf ignoredBuf) { return new PacketSoundGUIOpen(); }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      try { Minecraft.getInstance().setScreen(new SubGuiSoundSelection(Minecraft.getInstance().screen, 0, null, "")); }
      catch (Exception e) { LogWriter.error(e); }
      CustomNpcs.debugData.end("Packets");
   }

}
