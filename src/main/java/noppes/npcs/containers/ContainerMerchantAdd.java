package noppes.npcs.containers;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import noppes.npcs.CustomContainer;
import noppes.npcs.containers.inventories.MerchantAddContainer;
import org.jetbrains.annotations.NotNull;

public class ContainerMerchantAdd extends ContainerNpcInterface {

   public final Merchant trader;
   public final MerchantAddContainer tradeContainer;

    public ContainerMerchantAdd(int containerId, Inventory playerInventory, int entityId) {
      super(CustomContainer.container_merchantadd, containerId, playerInventory);
      Level level = playerInventory.player.level();
      if (level.getEntity(entityId) instanceof Villager villager) { trader = villager; }
      else { trader = null; }
      tradeContainer = new MerchantAddContainer(trader);
      addSlot(new Slot(tradeContainer, 0, 136, 37));
      addSlot(new Slot(tradeContainer, 1, 162, 37));
      addSlot(new Slot(tradeContainer, 2, 220, 37));
      int i;
      for(i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) { addSlot(new Slot(playerInventory, j + i * 9 + 9, 108 + j * 18, 84 + i * 18)); }
      }
      for(i = 0; i < 9; ++i) { addSlot(new Slot(playerInventory, i, 108 + i * 18, 142)); }
   }

   @Override
   public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int limbSwingAmount) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = slots.get(limbSwingAmount);
      if (slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if (limbSwingAmount != 0 && limbSwingAmount != 1 && limbSwingAmount != 2) {
            if (limbSwingAmount >= 3 && limbSwingAmount < 30) {
               if (!moveItemStackTo(itemstack1, 30, 39, false)) { return ItemStack.EMPTY; }
            }
            else if (limbSwingAmount >= 30 && limbSwingAmount < 39 && !moveItemStackTo(itemstack1, 3, 30, false)) { return ItemStack.EMPTY; }
         }
         else if (!moveItemStackTo(itemstack1, 3, 39, false)) { return ItemStack.EMPTY; }
         if (itemstack1.getCount() == 0) { slot.set(ItemStack.EMPTY); }
         else { slot.setChanged(); }
         if (itemstack1.getCount() == itemstack.getCount()) { return ItemStack.EMPTY; }
         slot.onTake(playerIn, itemstack1);
      }
      return itemstack;
   }

   @Override
   public void removed(@NotNull Player playerIn) { }

   public MerchantOffers getOffers() { return trader.getOffers(); }

   public void setShopItem(int shopItem) {
      tradeContainer.setSelectionHint(shopItem);
   }

}
