package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiUpdate extends PacketBasic {

   protected static int channelId;

   public static void encode(PacketGuiUpdate ignoredMsg, FriendlyByteBuf ignoredBuf) { }

   public static PacketGuiUpdate decode(FriendlyByteBuf ignoredBuf) { return new PacketGuiUpdate(); }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Minecraft mc = Minecraft.getInstance();
      if (mc.screen instanceof IGuiInterface) { mc.screen.init(mc, mc.screen.width, mc.screen.height); }
      CustomNpcs.debugData.end("Packets");
   }

}
