package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

import java.util.Collections;
import java.util.List;

public class SPacketQuestSave extends PacketServerBasic {

   protected static int channelId;
   private final int categoryId;
   private final CompoundTag data;

   public SPacketQuestSave(int categoryIdIn, CompoundTag dataIn) {
      data = dataIn;
      categoryId = categoryIdIn;
   }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_QUEST); }

   public static void encode(SPacketQuestSave msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.categoryId);
      buf.writeNbt(msg.data);
   }

   public static SPacketQuestSave decode(FriendlyByteBuf buf) { return new SPacketQuestSave(buf.readInt(), buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   public void handle() {
      CustomNpcs.debugData.start("Packets");
      QuestCategory category = QuestController.instance.categories.get(categoryId);
      if (category != null) {
         Quest quest = new Quest(category);
         quest.load(data);
         QuestController.instance.saveQuest(category, quest);
         Packets.send(player, new PacketGuiUpdate());
      }
      CustomNpcs.debugData.end("Packets");
   }

}
