package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;
import noppes.npcs.util.CustomNPCsScheduler;

import java.util.Collections;
import java.util.List;

public class SPacketQuestCategorySave extends PacketServerBasic {

   protected static int channelId;
   private NBTTagCompound data;

   public SPacketQuestCategorySave() { }

   public SPacketQuestCategorySave(NBTTagCompound dataIn) { data = dataIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_QUEST); }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeNbt(data); }

   @Override
   public void decode(FriendlyByteBuf buf) { data = buf.readNbt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   public void handle() {
      CustomNpcs.debugData.start("Packets");
      QuestCategory category = new QuestCategory();
      category.load(data);
      QuestController.instance.saveCategory(category);
      CustomNPCsScheduler.runTack(() -> Packets.send(player, new PacketGuiUpdate()), 150);
      CustomNpcs.debugData.end("Packets");
   }

}
