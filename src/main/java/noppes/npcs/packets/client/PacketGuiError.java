package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.client.gui.listeners.IGuiError;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiError extends PacketBasic {

   protected static int channelId;
   private int error;
   private NBTTagCompound data;

   public PacketGuiError() { }

   public PacketGuiError(int errorIn, NBTTagCompound dataIn) {
      error = errorIn;
      data = dataIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) { data = buf.readNbt(); }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeNbt(data); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (Minecraft.getMinecraft().currentScreen instanceof IGuiError) {
         ((IGuiError) Minecraft.getMinecraft().currentScreen).setError(error, data);
      }
      CustomNpcs.debugData.end("Packets");
   }

}
