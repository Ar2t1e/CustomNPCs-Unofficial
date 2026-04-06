package noppes.npcs.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
   private final int id;

   public PacketQuestCompletion(int idIn) { id = idIn; }

   public static void encode(PacketQuestCompletion msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

   public static PacketQuestCompletion decode(FriendlyByteBuf buf) { return new PacketQuestCompletion(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
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
