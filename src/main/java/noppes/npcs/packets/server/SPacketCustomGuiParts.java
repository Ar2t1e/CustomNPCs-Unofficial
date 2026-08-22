package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketCustomGuiParts extends PacketServerBasic {

   protected static int channelId;
   private NBTTagCompound data;

   public SPacketCustomGuiParts() { }

   @SuppressWarnings("unused")
   public SPacketCustomGuiParts(NBTTagCompound dataIn) { data = dataIn; }

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
   public void encode(FriendlyByteBuf buf) { buf.writeNbt(data); }

   @Override
   public void decode(FriendlyByteBuf buf) { data = buf.readNbt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (player.openContainer instanceof ContainerCustomGui) {
         ((ContainerCustomGui) player.openContainer).customGui.npc.modelData.load(data);
         ((ContainerCustomGui) player.openContainer).customGui.npc.updateClient = true;
      }
      CustomNpcs.debugData.end("Packets");
   }

}
