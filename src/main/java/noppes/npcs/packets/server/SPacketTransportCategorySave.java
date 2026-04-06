package noppes.npcs.packets.server;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketTransportCategorySave extends PacketServerBasic {

   protected static int channelId;
   private NBTTagCompound compound;

   public SPacketTransportCategorySave() { }

   public SPacketTransportCategorySave(NBTTagCompound compoundIn) { compound = compoundIn; }

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.GLOBAL_TRANSPORT; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeNbt(compound); }

   @Override
   public void decode(FriendlyByteBuf buf) { compound = buf.readAnySizeNbt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      TransportController.getInstance().saveCategory(compound);
      CustomNpcs.debugData.end("Packets");
   }

}
