package noppes.npcs.containers.inventories;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.NoppesUtilServer;
import org.jetbrains.annotations.NotNull;

public class InventoryNPC implements Container {

   private final String inventoryTitle;
   private final int slotsCount;
   private final AbstractContainerMenu con;
   public final NonNullList<ItemStack> inventoryContents;

   public InventoryNPC(String title, int size, AbstractContainerMenu con) {
      this.con = con;
      this.inventoryTitle = title;
      this.slotsCount = size;
      this.inventoryContents = NonNullList.withSize(size, ItemStack.EMPTY);
   }

   public @NotNull ItemStack getItem(int index) {
      return this.inventoryContents.get(index);
   }

   public @NotNull ItemStack removeItem(int index, int count) {
      return ContainerHelper.removeItem(this.inventoryContents, index, count);
   }

   public void setItem(int index, @NotNull ItemStack stack) {
      this.inventoryContents.set(index, stack);
      if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
         stack.setCount(this.getMaxStackSize());
      }

   }

   public int getContainerSize() {
      return this.slotsCount;
   }

   public int getMaxStackSize() {
      return 64;
   }

   public boolean stillValid(@NotNull Player playerIn) {
      return false;
   }

   public @NotNull ItemStack removeItemNoUpdate(int index) {
      return ContainerHelper.takeItem(this.inventoryContents, index);
   }

   public boolean canPlaceItem(int index, @NotNull ItemStack itemstack) {
      return true;
   }

   public void setChanged() {
      this.con.slotsChanged(this);
   }

   public void startOpen(@NotNull Player playerIn) {
   }

   public void stopOpen(@NotNull Player playerIn) {
   }

   public boolean isEmpty() {
      for(int slot = 0; slot < this.getContainerSize(); ++slot) {
         ItemStack item = this.getItem(slot);
         if (!NoppesUtilServer.isItemStackNull(item) && !item.isEmpty()) {
            return false;
         }
      }
      return true;
   }

   public void clearContent() {
   }

}
