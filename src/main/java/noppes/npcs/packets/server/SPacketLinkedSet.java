package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketLinkedSet extends PacketServerBasic {

   protected static int channelId;
   private final String name;

   public SPacketLinkedSet(String nameIn) { name = nameIn; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_ADVANCED); }

   public static void encode(SPacketLinkedSet msg, FriendlyByteBuf buf) { buf.writeUtf(msg.name); }

   public static SPacketLinkedSet decode(FriendlyByteBuf buf) { return new SPacketLinkedSet(buf.readUtf()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      npc.linkedName = name;
      LinkedNpcController.Instance.loadNpcData(npc);
      CustomNpcs.debugData.end("Packets");
   }

}
