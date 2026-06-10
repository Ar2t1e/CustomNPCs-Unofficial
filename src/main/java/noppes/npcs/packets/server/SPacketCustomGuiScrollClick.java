package noppes.npcs.packets.server;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiScrollWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketCustomGuiScrollClick extends PacketServerBasic {

   protected static int channelId;
   private final UUID id;
   private final int slotId;
   private final boolean doubleClicked;

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
   public List<PermissionNode<Boolean>> getPermission() {
      if (player.containerMenu instanceof ContainerCustomGui container) { return Collections.singletonList(container.activeGui.getPermission()); }
      return null;
   }

   public static void encode(SPacketCustomGuiScrollClick msg, FriendlyByteBuf buf) {
      buf.writeUUID(msg.id);
      buf.writeInt(msg.slotId);
      buf.writeBoolean(msg.doubleClicked);
   }

   public static SPacketCustomGuiScrollClick decode(FriendlyByteBuf buf) {
      return new SPacketCustomGuiScrollClick(buf.readUUID(), buf.readInt(), buf.readBoolean());
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (player.containerMenu instanceof ContainerCustomGui container) {
         ICustomGuiComponent comp = container.activeGui.getComponentUuid(id);
         if (comp instanceof CustomGuiScrollWrapper scroll) {
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
