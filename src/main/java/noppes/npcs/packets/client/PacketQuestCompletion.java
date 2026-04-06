package noppes.npcs.packets.client;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.EnumRewardType;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketQuestChooseReward;
import noppes.npcs.packets.server.SPacketQuestCompletionCheck;
import noppes.npcs.shared.common.PacketBasic;

public class PacketQuestCompletion extends PacketBasic {

   protected static int channelId;
   private int id;

   public PacketQuestCompletion() { }

   public PacketQuestCompletion(int idIn) { id = idIn; }

   @Override
   public void decode(FriendlyByteBuf buf) { id = buf.readInt(); }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeInt(id); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Quest quest = QuestController.instance.get(id);
      if (quest != null) {
         if (quest.rewardType == EnumRewardType.ONE_SELECT && !quest.rewardItems.isEmpty()) { Packets.sendServer(new SPacketQuestChooseReward(id)); }
         else { Packets.sendServer(new SPacketQuestCompletionCheck(id, ItemStack.EMPTY)); }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
