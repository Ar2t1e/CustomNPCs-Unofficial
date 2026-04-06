package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.controllers.OverlayController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketHideAllOverlays extends PacketBasic {

   protected static int channelId;

   public static void encode(PacketHideAllOverlays ignoredMsg, FriendlyByteBuf ignoredBuf) {}

   public static PacketHideAllOverlays decode(FriendlyByteBuf ignoredBuf) { return new PacketHideAllOverlays(); }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      OverlayController.getInstance().clear();
      CustomNpcs.debugData.end("Packets");
   }

}
