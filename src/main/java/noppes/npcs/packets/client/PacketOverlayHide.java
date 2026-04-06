package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.controllers.OverlayController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketOverlayHide extends PacketBasic {

   protected static int channelId;
   private int id;

   public PacketOverlayHide() { }

   public PacketOverlayHide(int idIn) { id = idIn; }

   @Override
   public void decode(FriendlyByteBuf buf) { id = buf.readInt(); }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeInt(id); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      OverlayController.getInstance().removeOverlay(id);
      CustomNpcs.debugData.end("Packets");
   }

}
