package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.player.GuiMailmanWrite;
import noppes.npcs.shared.client.gui.listeners.IGuiClose;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiClose extends PacketBasic {

   protected static int channelId;
   private final CompoundTag data;

   public PacketGuiClose(CompoundTag dataIn) { data = dataIn; }

   public PacketGuiClose() { this(new CompoundTag()); }

   public static void encode(PacketGuiClose msg, FriendlyByteBuf buf) { buf.writeNbt(msg.data); }

   public static PacketGuiClose decode(FriendlyByteBuf buf) { return new PacketGuiClose(buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Minecraft mc = Minecraft.getInstance();
      Screen gui = mc.screen;
      if (gui != null) {
         if (gui instanceof IGuiClose guiClose) {
            guiClose.setClose(data);
            if (gui instanceof GuiMailmanWrite) {
               CustomNpcs.debugData.end("Packets");
               return;
            }
         }
         mc.popGuiLayer();
         mc.mouseHandler.grabMouse();
      }
      CustomNpcs.debugData.end("Packets");
   }

}
