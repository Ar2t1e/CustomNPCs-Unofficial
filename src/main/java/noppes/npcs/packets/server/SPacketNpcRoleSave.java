package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketNpcRoleSave extends PacketServerBasic {

   protected static int channelId;
   private final CompoundTag data;

   public SPacketNpcRoleSave(CompoundTag dataIn) { data = dataIn; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_ADVANCED); }

   public static void encode(SPacketNpcRoleSave msg, FriendlyByteBuf buf) { buf.writeNbt(msg.data); }

   public static SPacketNpcRoleSave decode(FriendlyByteBuf buf) { return new SPacketNpcRoleSave(buf.readNbt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      npc.role.load(data);
      npc.updateClient = true;
      CustomNpcs.debugData.end("Packets");
   }

}
