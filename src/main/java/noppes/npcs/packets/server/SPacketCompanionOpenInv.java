package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketCompanionOpenInv extends PacketServerBasic {

   protected static int channelId;

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return null; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public boolean requiresNpc() { return true; }

   public static void encode(SPacketCompanionOpenInv ignoredMsg, FriendlyByteBuf ignoredBuf) { }

   public static SPacketCompanionOpenInv decode(FriendlyByteBuf ignoredBuf) { return new SPacketCompanionOpenInv(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      if (npc.role.getEnumType() == RoleType.COMPANION && player == npc.getOwner()) {
         NoppesUtilServer.sendOpenGui(player, EnumGuiType.CompanionInv, npc);
      }
      CustomNpcs.debugData.end("Packets");
   }

}
