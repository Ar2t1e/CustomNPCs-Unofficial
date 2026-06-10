package noppes.npcs.packets.server;

import java.util.List;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiAssetsSelectorWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiTextFieldWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketCustomGuiTextUpdate extends PacketServerBasic {

   protected static int channelId;
   private UUID id;
   private String text;

   public SPacketCustomGuiTextUpdate() { }

   public SPacketCustomGuiTextUpdate(UUID idIn, String textIn) {
      id = idIn;
      text = textIn;
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
      buf.writeUtf(text, 131068);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      id = buf.readUUID();
      text = buf.readUtf(131068);
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
            ((CustomGuiTextFieldWrapper) comp).setText(text);
            ((CustomGuiTextFieldWrapper) comp).onChange(container.activeGui);
         }
         if (comp instanceof CustomGuiAssetsSelectorWrapper) {
            ((CustomGuiAssetsSelectorWrapper) comp).setSelected(text);
            ((CustomGuiAssetsSelectorWrapper) comp).onChange(container.activeGui);
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
