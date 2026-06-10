package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketRemoteMenuOpen extends PacketServerBasic {

   protected static int channelId;
   private final int entityId;

   public SPacketRemoteMenuOpen(int entityIdIn) { entityId = entityIdIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_GUI); }

   public static void encode(SPacketRemoteMenuOpen msg, FriendlyByteBuf buf) { buf.writeInt(msg.entityId); }

   public static SPacketRemoteMenuOpen decode(FriendlyByteBuf buf) { return new SPacketRemoteMenuOpen(buf.readInt()); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Entity entity = player.level().getEntity(entityId);
      if (entity instanceof EntityNPCInterface npcIn) { NoppesUtilServer.sendOpenGui(player, EnumGuiType.MainMenuDisplay, npcIn); }
      CustomNpcs.debugData.end("Packets");
   }

}
