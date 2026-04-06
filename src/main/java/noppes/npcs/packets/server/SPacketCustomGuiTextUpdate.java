package noppes.npcs.packets.server;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiAssetsSelectorWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiTextFieldWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketCustomGuiTextUpdate extends PacketServerBasic {

   protected static int channelId;
   private final UUID id;
   private final String text;

   public SPacketCustomGuiTextUpdate(UUID idIn, String textIn) {
      id = idIn;
      text = textIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public PermissionNode<Boolean> getPermission() {
      if (player.containerMenu instanceof ContainerCustomGui container) { return container.activeGui.getPermission(); }
      return null;
   }

   public static void encode(SPacketCustomGuiTextUpdate msg, FriendlyByteBuf buf) {
      buf.writeUUID(msg.id);
      buf.writeUtf(msg.text, 131068);
   }

   @Override
   public int getChannelId() { return channelId; }

   public static SPacketCustomGuiTextUpdate decode(FriendlyByteBuf buf) {
      return new SPacketCustomGuiTextUpdate(buf.readUUID(), buf.readUtf(131068));
   }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (player.containerMenu instanceof ContainerCustomGui container) {
         ICustomGuiComponent comp = container.activeGui.getComponentUuid(id);
         if (comp instanceof CustomGuiTextFieldWrapper tf) {
            tf.setText(text);
            tf.onChange(container.activeGui);
         }
         if (comp instanceof CustomGuiAssetsSelectorWrapper as) {
            as.setSelected(text);
            as.onChange(container.activeGui);
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
