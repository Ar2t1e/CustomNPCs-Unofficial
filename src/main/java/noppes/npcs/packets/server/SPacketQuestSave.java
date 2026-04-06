package noppes.npcs.packets.server;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

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
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.GLOBAL_QUEST; }

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
