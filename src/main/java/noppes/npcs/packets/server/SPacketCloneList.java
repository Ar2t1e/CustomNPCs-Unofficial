package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketCloneList extends PacketServerBasic {

   protected static int channelId;
   private final int tab;

   public SPacketCloneList(int tabIn) { tab = tabIn; }

   @Override
   public boolean toolAllowed(ItemStack item) {
      return item.getItem() == CustomItems.wand || item.getItem() == CustomItems.cloner || item.getItem() == CustomItems.mount;
   }

   public static void encode(SPacketCloneList msg, FriendlyByteBuf buf) { buf.writeInt(msg.tab); }

   public static SPacketCloneList decode(FriendlyByteBuf buf) { return new SPacketCloneList(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      sendList(player, tab);
      CustomNpcs.debugData.end("Packets");
   }

   public static void sendList(ServerPlayer player, int tab) {
      ListTag list = new ListTag();
      for (String name : ServerCloneController.Instance.getClones(tab)) { list.add(StringTag.valueOf(name)); }
      CompoundTag compound = new CompoundTag();
      compound.put("List", list);
      Packets.send(player, new PacketGuiData(compound));
   }

}
