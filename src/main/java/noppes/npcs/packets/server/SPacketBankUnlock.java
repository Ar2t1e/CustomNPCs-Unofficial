package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.List;

public class SPacketBankUnlock extends PacketServerBasic {

   protected static int channelId;

   @Override
   public boolean toolAllowed(ItemStack item) { return true; }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return null; }

   @Override
   public void encode(FriendlyByteBuf buf) { }

   @Override
   public void decode(FriendlyByteBuf buf) { }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      /*if (npc.role.getType() == 3 && player.containerMenu instanceof ContainerNPCBankInterface container) {
         Bank bank = BankController.getInstance().getBank(container.bankId);
         ItemStack item = bank.currencyInventory.getItem(container.slot);
         if (!item.isEmpty()) {
            int price = item.getCount();
            ItemStack currency = container.currencyMatrix.getItem(0);
            if (!currency.isEmpty() && price <= currency.getCount()) {
               if (currency.getCount() - price == 0) {
                  container.currencyMatrix.setItem(0, ItemStack.EMPTY);
               } else {
                  currency.split(price);
               }
               player.closeContainer();
               PlayerBankData data = PlayerDataController.instance.getBankData(player, bank.id);
               BankData bankData = data.getBank(bank.id);
               if (bankData.unlockedSlots + 1 <= bank.maxSlots) {
                  ++bankData.unlockedSlots;
               }
               RoleEvent.BankUnlockedEvent event = new RoleEvent.BankUnlockedEvent(player, npc.wrappedNPC, container.slot);
               EventHooks.onNPCRole(npc, event);
               bankData.openBankGui(player, npc, bank.id, container.slot);
            }
         }
      }*/
      CustomNpcs.debugData.end("Packets");
   }

}
