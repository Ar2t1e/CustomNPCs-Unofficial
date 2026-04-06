package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.containers.ContainerNPCBank;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketBankOpen extends PacketServerBasic {

   protected static int channelId;
   private int bankId;
   private int ceil;
   private int ceilPos;
   private int scrollY;
   private int ceilsUpdate;

   public SPacketBankOpen() { }

   public SPacketBankOpen(int bankIdIn, int ceilIn, int ceilPosIn, int scrollYIn, int ceilsUpdateIn) {
      ceil = ceilIn;
      ceilPos = ceilPosIn;
      scrollY = scrollYIn;
      ceilsUpdate = ceilsUpdateIn;
      bankId = bankIdIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(bankId);
      buf.writeInt(ceil);
      buf.writeInt(ceilPos);
      buf.writeInt(scrollY);
      buf.writeInt(ceilsUpdate);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      bankId = buf.readInt();
      ceil = buf.readInt();
      ceilPos = buf.readInt();
      scrollY = buf.readInt();
      ceilsUpdate = buf.readInt();
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
      else if (player.openContainer instanceof ContainerNPCBank) { player.closeContainer(); }
      CustomNpcs.debugData.end("Packets");
   }

}
