package noppes.npcs.packets.server;

import java.util.List;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiSliderWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketCustomGuiSliderUpdate extends PacketServerBasic {

   protected static int channelId;
   private UUID id;
   private float value;

   public SPacketCustomGuiSliderUpdate() { }

   public SPacketCustomGuiSliderUpdate(UUID idIn, float valueIn) {
      id = idIn;
      value = valueIn;
   }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeUUID(id);
      buf.writeFloat(value);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      id = buf.readUUID();
      value = buf.readFloat();
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (player.openContainer instanceof ContainerCustomGui) {
         ContainerCustomGui container = (ContainerCustomGui) player.openContainer;
         ICustomGuiComponent comp = container.activeGui.getComponentUuid(id);
         if (comp instanceof CustomGuiSliderWrapper) {
            ((CustomGuiSliderWrapper) comp).setValue(value);
            ((CustomGuiSliderWrapper) comp).onChange(container.activeGui);
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
