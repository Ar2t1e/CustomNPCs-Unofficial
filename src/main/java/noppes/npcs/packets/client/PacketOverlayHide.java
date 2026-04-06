package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.controllers.OverlayController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketOverlayHide extends PacketBasic {

   protected static int channelId;
   private final int id;

   public PacketOverlayHide(int idIn) { id = idIn; }

   public static void encode(PacketOverlayHide msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

   public static PacketOverlayHide decode(FriendlyByteBuf buf) { return new PacketOverlayHide(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      OverlayController.getInstance().removeOverlay(id);
      CustomNpcs.debugData.end("Packets");
   }

}
