package noppes.npcs.packets.server;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.EventHooks;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiScrollWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketCustomGuiScrollClick extends PacketServerBasic {

   protected static int channelId;
   private UUID id;
   private int slotId;
   private boolean doubleClicked;

   public SPacketCustomGuiScrollClick() { }

   public SPacketCustomGuiScrollClick(UUID idIn, int slotIdIn, boolean doubleClickedIn) {
      id = idIn;
      slotId = slotIdIn;
      doubleClicked = doubleClickedIn;
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
      buf.writeInt(slotId);
      buf.writeBoolean(doubleClicked);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      id = buf.readUUID();
      slotId = buf.readInt();
      doubleClicked = buf.readBoolean();
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (player.openContainer instanceof ContainerCustomGui) {
         ContainerCustomGui container = (ContainerCustomGui) player.openContainer;
         ICustomGuiComponent comp = container.activeGui.getComponentUuid(id);
         if (comp instanceof CustomGuiScrollWrapper) {
            CustomGuiScrollWrapper scroll = (CustomGuiScrollWrapper) comp;
            if (scroll.isMultiSelect()) {
               List<Integer> list = Arrays.stream(scroll.getSelection()).boxed().collect(Collectors.toList());
               if (list.contains(slotId)) { list.remove(slotId); }
               else { list.add(slotId); }
               scroll.setSelection(list.stream().mapToInt(Integer::intValue).toArray());
            }
            else { scroll.setSelection(slotId); }
            if (doubleClicked) { scroll.onDoubleClick(container.activeGui); }
            else { scroll.onClick(container.activeGui); }
            EventHooks.onCustomGuiScrollClick(iPlayer, container.activeGui, scroll, slotId, scroll.getSelectionList(), doubleClicked);
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
