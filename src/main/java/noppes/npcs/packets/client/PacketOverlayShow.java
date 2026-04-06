package noppes.npcs.packets.client;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.wrapper.NBTWrapper;
import noppes.npcs.api.wrapper.OverlayWrapper;
import noppes.npcs.client.controllers.OverlayController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketOverlayShow extends PacketBasic {

   protected static int channelId;
   private NBTTagCompound compound;

   public PacketOverlayShow() { }

   public PacketOverlayShow(NBTTagCompound compoundIn) { compound = compoundIn; }

   @Override
   public void decode(FriendlyByteBuf buf) { compound = buf.readNbt(); }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeNbt(compound); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      OverlayWrapper wrapper = new OverlayWrapper(0);
      wrapper.load(new NBTWrapper(compound));
      OverlayController.getInstance().addOverlay(wrapper);
      CustomNpcs.debugData.end("Packets");
   }

}
