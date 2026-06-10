package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
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
   private EnumGuiType gui;
   private NBTTagCompound data;
   private int tackPos;

   public SPacketQuestOpen() { }

   public SPacketQuestOpen(EnumGuiType guiIn, NBTTagCompound dataIn, int tackPosIn) {
      gui = guiIn;
      data = dataIn;
      tackPos = tackPosIn;
   }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_DIALOG); }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeEnum(gui);
      buf.writeNbt(data);
      buf.writeInt(tackPos);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      gui = buf.readEnum(EnumGuiType.class);
      data = buf.readNbt();
      tackPos = buf.readInt();
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
