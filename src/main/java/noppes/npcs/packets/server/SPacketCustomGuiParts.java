package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketCustomGuiParts extends PacketServerBasic {

   protected static int channelId;
   private final CompoundTag data;

   public SPacketCustomGuiParts(CompoundTag dataIn) { data = dataIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() {
      if (player.containerMenu instanceof ContainerCustomGui container) { return Collections.singletonList(container.activeGui.getPermission()); }
      return null;
   }

   public static void encode(SPacketCustomGuiParts msg, FriendlyByteBuf buf) { buf.writeNbt(msg.data); }

   public static SPacketCustomGuiParts decode(FriendlyByteBuf buf) { return new SPacketCustomGuiParts(buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (player.containerMenu instanceof ContainerCustomGui container) {
         container.customGui.npc.modelData.load(data);
         container.customGui.npc.updateClient = true;
      }
      CustomNpcs.debugData.end("Packets");
   }

}
