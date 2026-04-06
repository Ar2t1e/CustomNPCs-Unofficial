package noppes.npcs.packets.server;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.roles.RoleTransporter;

public class SPacketTransportSave extends PacketServerBasic {

   protected static int channelId;
   private int category;
   private NBTTagCompound data;

   public SPacketTransportSave() { }

   public SPacketTransportSave(int categoryIn, NBTTagCompound dataIn) {
      data = dataIn;
      category = categoryIn;
   }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.NPC_ADVANCED; }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(category);
      buf.writeNbt(data);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      category = buf.readInt();
      data = buf.readNbt();
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      TransportLocation location = TransportController.getInstance().saveLocation(category, data, player, npc);
      if (location != null && npc.role.getType() == 4) {
         RoleTransporter role = (RoleTransporter) npc.role;
         role.setTransport(location);
      }
      CustomNpcs.debugData.end("Packets");
   }
}
