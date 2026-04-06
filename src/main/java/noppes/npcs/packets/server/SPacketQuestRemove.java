package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

public class SPacketQuestRemove extends PacketServerBasic {

   protected static int channelId;
   private final int id;

   public SPacketQuestRemove(int idIn) { id = idIn; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_QUEST; }

   public static void encode(SPacketQuestRemove msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

   public static SPacketQuestRemove decode(FriendlyByteBuf buf) { return new SPacketQuestRemove(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Quest quest = QuestController.instance.quests.get(id);
      if (quest != null) {
         QuestController.instance.removeQuest(quest);
         Packets.send(player, new PacketGuiUpdate());
      }
      CustomNpcs.debugData.end("Packets");
   }

}
