package noppes.npcs.packets.server;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.EventHooks;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiAssetsSelectorWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketCustomGuiButton extends PacketServerBasic {

   protected static int channelId;
   private UUID id;

   public SPacketCustomGuiButton() { }

   public SPacketCustomGuiButton(UUID idIn) { id = idIn; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public CustomNpcsPermissions.Permission getPermission() {
      if (player.openContainer instanceof ContainerCustomGui) {
         return ((ContainerCustomGui) player.openContainer).activeGui.getPermission();
      }
      return null;
   }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeUUID(id); }

   @Override
   public void decode(FriendlyByteBuf buf) { id = buf.readUUID(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (player.openContainer instanceof ContainerCustomGui) {
         ContainerCustomGui container = (ContainerCustomGui) player.openContainer;
         ICustomGuiComponent comp = container.activeGui.getComponentUuid(id);
         if (comp instanceof CustomGuiButtonWrapper) {
            ((CustomGuiButtonWrapper) comp).onPress(container.activeGui);
            EventHooks.onCustomGuiButton(iPlayer, container.activeGui, (CustomGuiButtonWrapper) comp);
         }
         if (comp instanceof CustomGuiAssetsSelectorWrapper) {
            ((CustomGuiAssetsSelectorWrapper) comp).onPress(container.activeGui);
         }
      }
      CustomNpcs.debugData.end("Packets");
   }
}
