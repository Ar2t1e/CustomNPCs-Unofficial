package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
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
   private int categoryId;
   private NBTTagCompound data;

   public SPacketQuestSave() { }

   public SPacketQuestSave(int categoryIdIn, NBTTagCompound dataIn) {
      data = dataIn;
      categoryId = categoryIdIn;
   }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_QUEST); }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(categoryId);
      buf.writeNbt(data);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      categoryId = buf.readInt();
      data = buf.readNbt();
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
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
