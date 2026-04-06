package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketFactionRemove extends PacketServerBasic {

   protected static int channelId;
   private final int id;

   public SPacketFactionRemove(int idIn) {
      id = idIn;
   }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_FACTION; }

   public static void encode(SPacketFactionRemove msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

   public static SPacketFactionRemove decode(FriendlyByteBuf buf) { return new SPacketFactionRemove(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      FactionController.instance.delete(id);
      SPacketFactionsGet.sendFactionDataAll(player);
      CompoundTag compound = new CompoundTag();
      (new Faction()).save(compound);
      Packets.send(player, new PacketGuiData(compound));
      CustomNpcs.debugData.end("Packets");
   }

}
