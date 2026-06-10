package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketNpcTransform extends PacketServerBasic {

   protected static int channelId;
   private final boolean isActive;

   public SPacketNpcTransform(boolean isActiveIn) { isActive = isActiveIn; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_ADVANCED); }

   public static void encode(SPacketNpcTransform msg, FriendlyByteBuf buf) { buf.writeBoolean(msg.isActive); }

   public static SPacketNpcTransform decode(FriendlyByteBuf buf) { return new SPacketNpcTransform(buf.readBoolean()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (npc.transform.isValid()) { npc.transform.transform(isActive); }
      CustomNpcs.debugData.end("Packets");
   }

}
