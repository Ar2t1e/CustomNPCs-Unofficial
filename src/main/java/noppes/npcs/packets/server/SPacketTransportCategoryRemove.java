package noppes.npcs.packets.server;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketTransportCategoryRemove extends PacketServerBasic {

   protected static int channelId;
   private int id;

   public SPacketTransportCategoryRemove() { }

   public SPacketTransportCategoryRemove(int idIn) { id = idIn; }

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.GLOBAL_TRANSPORT; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeInt(id); }

   @Override
   public void decode(FriendlyByteBuf buf) { id = buf.readInt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      TransportController.getInstance().removeCategory(id);
      TransportController.getInstance().sendTo(player);
      Packets.send(player, new PacketGuiData(new NBTTagCompound()));
      CustomNpcs.debugData.start("Packets");
   }

}
