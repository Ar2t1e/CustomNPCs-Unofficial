package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

public class SPacketQuestCategoryRemove extends PacketServerBasic {

   protected static int channelId;
   private final int id;

   public SPacketQuestCategoryRemove(int idIn) { id = idIn; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_QUEST; }

   public static void encode(SPacketQuestCategoryRemove msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

   public static SPacketQuestCategoryRemove decode(FriendlyByteBuf buf) { return new SPacketQuestCategoryRemove(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      QuestController.instance.removeCategory(id);
      Packets.send(player, new PacketGuiUpdate());
      CustomNpcs.debugData.end("Packets");
   }

}
