package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.controllers.OverlayController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketHideAllOverlays extends PacketBasic {

   protected static int channelId;

   public PacketHideAllOverlays() { }

   @Override
   public void decode(FriendlyByteBuf buf) { }

   @Override
   public void encode(FriendlyByteBuf buf) { }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      OverlayController.getInstance().clear();
      CustomNpcs.debugData.end("Packets");
   }

}
