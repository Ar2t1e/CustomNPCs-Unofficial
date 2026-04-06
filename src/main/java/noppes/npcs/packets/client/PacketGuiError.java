package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.client.gui.listeners.IGuiError;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiError extends PacketBasic {

   protected static int channelId;
   private final int error;
   private final CompoundTag data;

   public PacketGuiError(int errorIn, CompoundTag dataIn) {
      error = errorIn;
      data = dataIn;
   }

   public static void encode(PacketGuiError msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.error);
      buf.writeNbt(msg.data);
   }

   public static PacketGuiError decode(FriendlyByteBuf buf) { return new PacketGuiError(buf.readInt(), buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (Minecraft.getInstance().screen instanceof IGuiError gui) { gui.setError(error, data); }
      CustomNpcs.debugData.end("Packets");
   }

}
