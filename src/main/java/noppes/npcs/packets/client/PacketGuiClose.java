package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.player.GuiMailmanWrite;
import noppes.npcs.shared.client.gui.listeners.IGuiClose;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiClose extends PacketBasic {

   protected static int channelId;
   private NBTTagCompound data;

   public PacketGuiClose(NBTTagCompound dataIn) { data = dataIn; }

   public PacketGuiClose() { this(new NBTTagCompound()); }

   @Override
   public void decode(FriendlyByteBuf buf) { data = buf.readNbt(); }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeNbt(data); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Minecraft mc = Minecraft.getMinecraft();
      GuiScreen gui = mc.currentScreen;
      if (gui != null) {
         if (gui instanceof IGuiClose) {
            ((IGuiClose) gui).setClose(data);
            if (gui instanceof GuiMailmanWrite) {
               CustomNpcs.debugData.end("Packets");
               return;
            }
         }
         mc.displayGuiScreen(null);
         mc.setIngameFocus();
      }
      CustomNpcs.debugData.end("Packets");
   }

}
