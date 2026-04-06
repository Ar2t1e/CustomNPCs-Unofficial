package noppes.npcs.packets.server;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiAssetsSelectorWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketCustomGuiButton extends PacketServerBasic {

   protected static int channelId;
   private final UUID id;

   public SPacketCustomGuiButton(UUID idIn) { id = idIn; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public PermissionNode<Boolean> getPermission() {
      if (player.containerMenu instanceof ContainerCustomGui container) { return container.activeGui.getPermission(); }
      return null;
   }

   public static void encode(SPacketCustomGuiButton msg, FriendlyByteBuf buf) {
      buf.writeUUID(msg.id);
   }

   public static SPacketCustomGuiButton decode(FriendlyByteBuf buf) { return new SPacketCustomGuiButton(buf.readUUID()); }

   @Override
   public int getChannelId() { return channelId; }

   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (player.containerMenu instanceof ContainerCustomGui container) {
         ICustomGuiComponent comp = container.activeGui.getComponentUuid(id);
         if (comp instanceof CustomGuiButtonWrapper button) {
            button.onPress(container.activeGui);
            EventHooks.onCustomGuiButton(iPlayer, container.activeGui, button);
         }
         if (comp instanceof CustomGuiAssetsSelectorWrapper assets) {
            assets.onPress(container.activeGui);
         }
      }
      CustomNpcs.debugData.end("Packets");
   }
}
