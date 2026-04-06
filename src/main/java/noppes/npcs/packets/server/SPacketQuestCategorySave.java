package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;
import noppes.npcs.util.CustomNPCsScheduler;

public class SPacketQuestCategorySave extends PacketServerBasic {

   protected static int channelId;
   private final CompoundTag data;

   public SPacketQuestCategorySave(CompoundTag dataIn) { data = dataIn; }

   @Override
   public PermissionNode<Boolean> getPermission() { return CustomNpcsPermissions.GLOBAL_QUEST; }

   public static void encode(SPacketQuestCategorySave msg, FriendlyByteBuf buf) { buf.writeNbt(msg.data); }

   public static SPacketQuestCategorySave decode(FriendlyByteBuf buf) { return new SPacketQuestCategorySave(buf.readNbt()); }

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
