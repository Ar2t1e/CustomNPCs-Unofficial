package noppes.npcs.packets.server;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiTextFieldWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketCustomGuiFocusUpdate extends PacketServerBasic {

   protected static int channelId;
   private final UUID id;
   private final boolean focus;

   public SPacketCustomGuiFocusUpdate(UUID idIn, boolean focusIn) {
      id = idIn;
      focus = focusIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public PermissionNode<Boolean> getPermission() {
      if (player.containerMenu instanceof ContainerCustomGui container) { return container.activeGui.getPermission(); }
      return null;
   }

   public static void encode(SPacketCustomGuiFocusUpdate msg, FriendlyByteBuf buf) {
      buf.writeUUID(msg.id);
      buf.writeBoolean(msg.focus);
   }

   public static SPacketCustomGuiFocusUpdate decode(FriendlyByteBuf buf) {
      return new SPacketCustomGuiFocusUpdate(buf.readUUID(), buf.readBoolean());
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (player.containerMenu instanceof ContainerCustomGui container) {
         ICustomGuiComponent comp = container.activeGui.getComponentUuid(id);
         if (comp instanceof CustomGuiTextFieldWrapper tf) {
            tf.setFocused(focus);
            if (!focus) { tf.onFocusLost(container.activeGui); }
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
