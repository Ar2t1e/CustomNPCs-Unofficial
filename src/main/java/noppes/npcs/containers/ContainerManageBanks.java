package noppes.npcs.containers;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.containers.inventories.NpcMiscInventory;
import noppes.npcs.controllers.data.Bank;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class ContainerManageBanks extends AbstractContainerMenu {

   protected int bankId = -1;
   public int ceil = -1;

   public ContainerManageBanks(int containerId, Inventory playerInventory) {
      super(CustomContainer.container_managebanks, containerId);
      Container inv = new NpcMiscInventory(2);
      addSlot(new Slot(inv, 0, -5000, -5000));
      addSlot(new Slot(inv, 1, -5000, -5000));
      for (int i = 0; i < 3; ++i) {
         for (int j = 0; j < 9; ++j) {
            addSlot(new Slot(playerInventory, j + i * 9 + 9, j * 18 + 8, 113 + i * 18));
         }
      }
      for (int j = 0; j < 9; ++j) {
         addSlot(new Slot(playerInventory, j, j * 18 + 8, 171));
      }
   }

   @Override
   public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int i) { return ItemStack.EMPTY; }

   @Override
   public boolean stillValid(@NotNull Player playerIn) {
      if (playerIn instanceof ServerPlayer player) {
         return CustomNpcsPermissions.hasPermission(player, CustomNpcsPermissions.GLOBAL_BANK);
      }
      return true;
   }

   // Server
   public void setBank(@Nonnull Bank bank, int ceilIn) {
      if (bank.ceilSettings.containsKey(ceilIn)) {
         bankId = bank.id;
         ceil = ceilIn;
         getSlot(0).set(bank.ceilSettings.get(ceil).openStack);
         getSlot(1).set(bank.ceilSettings.get(ceil).upgradeStack);
         broadcastChanges();
      }
   }

   public boolean isBank(int bankIdIn) { return bankId == bankIdIn; }

}
