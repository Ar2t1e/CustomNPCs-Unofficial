package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketTransportCategorySave extends PacketServerBasic {

   protected static int channelId;
   private final CompoundTag compound;

   public SPacketTransportCategorySave(CompoundTag compoundIn) { compound = compoundIn; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_TRANSPORT; }

   public static void encode(SPacketTransportCategorySave msg, FriendlyByteBuf buf) { buf.writeNbt(msg.compound); }

   public static SPacketTransportCategorySave decode(FriendlyByteBuf buf) { return new SPacketTransportCategorySave(buf.readAnySizeNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      TransportController.getInstance().saveCategory(compound);
      CustomNpcs.debugData.end("Packets");
   }

}
