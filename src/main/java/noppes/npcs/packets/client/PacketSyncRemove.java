package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.global.GuiNpcManageMarkets;
import noppes.npcs.client.gui.player.GuiNPCTrader;
import noppes.npcs.controllers.*;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSyncRemove extends PacketBasic {

   protected static int channelId;
   private final int id;
   private final int type;

   public PacketSyncRemove(int idIn, int typeIn) {
      id = idIn;
      type = typeIn;
   }

   public static void encode(PacketSyncRemove msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.id);
      buf.writeInt(msg.type);
   }

   public static PacketSyncRemove decode(FriendlyByteBuf buf) { return new PacketSyncRemove(buf.readInt(), buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Minecraft mc = Minecraft.getInstance();
      switch (type) {
         case 1: {
            FactionController.instance.factions.remove(id);
            break;
         } // faction
         case 2: {
            Quest quest = QuestController.instance.quests.remove(id);
            if (quest != null) { quest.category.quests.remove(id); }
            break;
         } // quest
         case 3: {
            QuestCategory category = QuestController.instance.categories.remove(id);
            if (category != null) { QuestController.instance.quests.keySet().removeAll(category.quests.keySet()); }
            break;
         } // quest category
         case 4: {
            Dialog dialog = DialogController.instance.dialogs.remove(id);
            if (dialog != null) { dialog.category.dialogs.remove(id); }
            break;
         } // dialog
         case 5: {
            DialogCategory category = DialogController.instance.categories.remove(id);
            if (category != null) { DialogController.instance.dialogs.keySet().removeAll(category.dialogs.keySet()); }
            break;
         } // dialog category
         case 6: {
            MarcetController.getInstance().removeMarcet(id);
            if (mc.screen instanceof GuiNpcManageMarkets gui) { gui.setGuiData(new CompoundTag()); }
            else if (mc.screen instanceof GuiNPCTrader gui) { gui.setGuiData(new CompoundTag()); }
            break;
         } // marcet
         case 7: {
            MarcetController.getInstance().removeDeal(id);
            if (mc.screen instanceof GuiNpcManageMarkets gui) { gui.setGuiData(new CompoundTag()); }
            else if (mc.screen instanceof GuiNPCTrader gui) { gui.setGuiData(new CompoundTag()); }
            break;
         } // marcet deal
         case 8: {
            KeyController.getInstance().removeKeySetting(id);
            CustomNpcs.proxy.updateKeys();
            break;
         } // IKeySetting
         case 9: {
            if (id < 0) { AnimationController.getInstance().clearAnimations(); }
            else { AnimationController.getInstance().removeAnimation(id); }
            break;
         } // custom animation clear / del
         case 10: {
            if (id < 0) { AnimationController.getInstance().clearEmotions(); }
            else { AnimationController.getInstance().removeEmotion(id); }
            break;
         } // custom emotion clear / del
      }
      CustomNpcs.debugData.end("Packets");
   }

}
