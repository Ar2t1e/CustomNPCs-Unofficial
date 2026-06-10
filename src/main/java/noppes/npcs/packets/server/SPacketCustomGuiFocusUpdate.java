package noppes.npcs.packets.server;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiTextFieldWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketCustomGuiFocusUpdate extends PacketServerBasic {

   protected static int channelId;
   private UUID id;
   private boolean focus;

   public SPacketCustomGuiFocusUpdate() { }

   public SPacketCustomGuiFocusUpdate(UUID idIn, boolean focusIn) {
      id = idIn;
      focus = focusIn;
   }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() {
      if (player.openContainer instanceof ContainerCustomGui) {
         return Collections.singletonList(((ContainerCustomGui) player.openContainer).activeGui.getPermission());
      }
      return null;
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeUUID(id);
      buf.writeBoolean(focus);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      id = buf.readUUID();
      focus = buf.readBoolean();
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (player.openContainer instanceof ContainerCustomGui) {
         ContainerCustomGui container = (ContainerCustomGui) player.openContainer;
         ICustomGuiComponent comp = container.activeGui.getComponentUuid(id);
         if (comp instanceof CustomGuiTextFieldWrapper) {
            ((CustomGuiTextFieldWrapper) comp).setFocused(focus);
            if (!focus) { ((CustomGuiTextFieldWrapper) comp).onFocusLost(container.activeGui); }
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
