package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.util.CustomNPCsScheduler;

import java.util.List;

public class SPacketQuestCompletionCheckAll extends PacketServerBasic {

   protected static int channelId;

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return null; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   public static void encode(SPacketQuestCompletionCheckAll ignoredMsg, FriendlyByteBuf ignoredBuf) { }

   public static SPacketQuestCompletionCheckAll decode(FriendlyByteBuf ignoredBuf) { return new SPacketQuestCompletionCheckAll(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      PlayerQuestData playerdata = PlayerData.get(player).questData;
      CustomNPCsScheduler.runTack(() -> {
         for (QuestData data : playerdata.activeQuests.values()) { playerdata.checkQuestCompletion(player, data); }
      });
      CustomNpcs.debugData.end("Packets");
   }
}
