package noppes.npcs.packets.server;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonListWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketCustomGuiButtonList extends PacketServerBasic {

   protected static int channelId;
   private final UUID id;
   private final boolean isRightClick;

   public SPacketCustomGuiButtonList(UUID idIn, boolean isRightClickIn) {
      id = idIn;
      isRightClick = isRightClickIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public PermissionNode<Boolean> getPermission() {
      if (player.containerMenu instanceof ContainerCustomGui container) { return container.activeGui.getPermission(); }
      return null;
   }

   public static void encode(SPacketCustomGuiButtonList msg, FriendlyByteBuf buf) {
      buf.writeUUID(msg.id);
      buf.writeBoolean(msg.isRightClick);
   }

   public static SPacketCustomGuiButtonList decode(FriendlyByteBuf buf) { return new SPacketCustomGuiButtonList(buf.readUUID(), buf.readBoolean()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (player.containerMenu instanceof ContainerCustomGui container &&
              container.activeGui.getComponentUuid(id) instanceof CustomGuiButtonListWrapper button) {
         button.setSelected(button.getSelected() + (isRightClick ? 1 : -1));
         button.onPress(container.activeGui);
         EventHooks.onCustomGuiButton(iPlayer, container.activeGui, button);
      }
      CustomNpcs.debugData.end("Packets");
   }

}
