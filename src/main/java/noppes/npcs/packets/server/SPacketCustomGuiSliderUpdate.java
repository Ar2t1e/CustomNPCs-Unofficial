package noppes.npcs.packets.server;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiSliderWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketCustomGuiSliderUpdate extends PacketServerBasic {

   protected static int channelId;
   private final UUID id;
   private final float value;

   public SPacketCustomGuiSliderUpdate(UUID idIn, float valueIn) {
      id = idIn;
      value = valueIn;
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

   public static void encode(SPacketCustomGuiSliderUpdate msg, FriendlyByteBuf buf) {
      buf.writeUUID(msg.id);
      buf.writeFloat(msg.value);
   }

   public static SPacketCustomGuiSliderUpdate decode(FriendlyByteBuf buf) {
      return new SPacketCustomGuiSliderUpdate(buf.readUUID(), buf.readFloat());
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (player.containerMenu instanceof ContainerCustomGui container) {
         ICustomGuiComponent comp = container.activeGui.getComponentUuid(id);
         if (comp instanceof CustomGuiSliderWrapper slider) {
            slider.setValue(value);
            slider.onChange(container.activeGui);
         }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
