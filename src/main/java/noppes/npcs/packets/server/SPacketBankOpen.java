package noppes.npcs.packets.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketBankOpen extends PacketServerBasic {

   protected static int channelId;
   private final int bankId;
   private final int ceil;
   private final int ceilPos;
   private final int scrollY;
   private final int ceilsUpdate;

   public SPacketBankOpen(int bankIdIn, int ceilIn, int ceilPosIn, int scrollYIn, int ceilsUpdateIn) {
      ceil = ceilIn;
      ceilPos = ceilPosIn;
      scrollY = scrollYIn;
      ceilsUpdate = ceilsUpdateIn;
      bankId = bankIdIn;
   }

   @Override
   public List<PermissionNode<Boolean>> getPermission() { return null; }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public boolean requiresNpc() { return true; }

   public static void encode(SPacketBankOpen msg, FriendlyByteBuf buf) {
      buf.writeInt(msg.bankId);
      buf.writeInt(msg.ceil);
      buf.writeInt(msg.ceilPos);
      buf.writeInt(msg.scrollY);
      buf.writeInt(msg.ceilsUpdate);
   }

   public static SPacketBankOpen decode(FriendlyByteBuf buf) {
      return new SPacketBankOpen(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Bank bank = BankController.getInstance().getBank(bankId);
      if (bank != null) {
         PlayerData.get(player).bankData.get(bank.id).openToPlayer(player, ceil, scrollY, ceilPos, ceilsUpdate);
      }
      else if (player.containerMenu instanceof ContainerNPCBank) { player.closeContainer(); }
      CustomNpcs.debugData.end("Packets");
   }

}
