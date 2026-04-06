package noppes.npcs.packets.client;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.wrapper.NBTWrapper;
import noppes.npcs.api.wrapper.OverlayWrapper;
import noppes.npcs.client.controllers.OverlayController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketOverlayShow extends PacketBasic {

   protected static int channelId;
   private final CompoundTag compound;

   public PacketOverlayShow(CompoundTag compoundIn) { compound = compoundIn; }

   public static void encode(PacketOverlayShow msg, FriendlyByteBuf buf) { buf.writeNbt(msg.compound); }

   public static PacketOverlayShow decode(FriendlyByteBuf buf) { return new PacketOverlayShow(buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      OverlayWrapper wrapper = new OverlayWrapper(0);
      wrapper.load(new NBTWrapper(compound));
      OverlayController.getInstance().addOverlay(wrapper);
      CustomNpcs.debugData.end("Packets");
   }

}
