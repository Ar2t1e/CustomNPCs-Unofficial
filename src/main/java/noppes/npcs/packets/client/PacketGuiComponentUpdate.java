package noppes.npcs.packets.client;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.wrapper.gui.CustomGuiComponentWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiComponentUpdate extends PacketBasic {

   protected static int channelId;
   private UUID id;
   private NBTTagCompound data;

   public PacketGuiComponentUpdate() { }

   public PacketGuiComponentUpdate(UUID idIn, NBTTagCompound dataIn) {
      id = idIn;
      data = dataIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      id = buf.readUUID();
      data = buf.readNbt();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeUUID(id);
      buf.writeNbt(data);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (Minecraft.getMinecraft().currentScreen instanceof GuiCustom) {
         GuiCustom cGui = (GuiCustom) Minecraft.getMinecraft().currentScreen;
         CustomGuiComponentWrapper component = (CustomGuiComponentWrapper) cGui.guiWrapper.getComponentUuid(id);
         if (component != null) {
            component.fromNBT(data);
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
