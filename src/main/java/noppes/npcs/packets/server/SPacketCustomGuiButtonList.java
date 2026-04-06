package noppes.npcs.packets.server;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonListWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketCustomGuiButtonList extends PacketServerBasic {

   protected static int channelId;
   private UUID id;
   private boolean isRightClick;

   public SPacketCustomGuiButtonList() { }

   public SPacketCustomGuiButtonList(UUID idIn, boolean isRightClickIn) {
      id = idIn;
      isRightClick = isRightClickIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeUUID(id);
      buf.writeBoolean(isRightClick);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      id = buf.readUUID();
      isRightClick = buf.readBoolean();
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (player.openContainer instanceof ContainerCustomGui) {
         ContainerCustomGui container = (ContainerCustomGui) player.openContainer;
         ICustomGuiComponent comp = container.activeGui.getComponentUuid(id);
         if (comp instanceof CustomGuiButtonListWrapper) {
            CustomGuiButtonListWrapper button = (CustomGuiButtonListWrapper) comp;
            button.setSelected(button.getSelected() + (isRightClick ? 1 : -1));
            button.onPress(container.activeGui);
            EventHooks.onCustomGuiButton(iPlayer, container.activeGui, button);
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
