package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketQuestOpen extends PacketServerBasic {

   protected static int channelId;
   private final EnumGuiType gui;
   private final CompoundTag data;
   private final int tackPos;

   public SPacketQuestOpen(EnumGuiType guiIn, CompoundTag dataIn, int tackPosIn) {
      gui = guiIn;
      data = dataIn;
      tackPos = tackPosIn;
   }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_DIALOG); }

   public static void encode(SPacketQuestOpen msg, FriendlyByteBuf buf) {
      buf.writeEnum(msg.gui);
      buf.writeNbt(msg.data);
      buf.writeInt(msg.tackPos);
   }

   public static SPacketQuestOpen decode(FriendlyByteBuf buf) {
      return new SPacketQuestOpen(buf.readEnum(EnumGuiType.class), buf.readNbt(), buf.readInt());
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Quest quest = new Quest(null);
      quest.load(data);
      NoppesUtilServer.setEditingQuest(player, quest);
      NoppesUtilServer.openContainerGui(player, gui, (buf) -> buf.writeInt(tackPos));
      CustomNpcs.debugData.end("Packets");
   }

}
