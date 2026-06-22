package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.shared.common.util.LogWriter;

import java.util.Collections;
import java.util.List;

public class SPacketNpcFactionSet extends PacketServerBasic {

   protected static int channelId;
   private final int id;

   public SPacketNpcFactionSet(int idIn) { id = idIn; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_ADVANCED); }

   public static void encode(SPacketNpcFactionSet msg, FriendlyByteBuf buf) { buf.writeInt(msg.id); }

   public static SPacketNpcFactionSet decode(FriendlyByteBuf buf) { return new SPacketNpcFactionSet(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      npc.setFaction(id);
      LogWriter.info("[DEBUG] "+id+" / "+npc.getFaction().id);
      CustomNpcs.debugData.end("Packets");
   }

}
