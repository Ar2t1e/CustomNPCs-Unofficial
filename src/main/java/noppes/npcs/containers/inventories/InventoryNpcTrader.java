package noppes.npcs.containers.inventories;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.containers.ContainerNPCTrader;
import org.jetbrains.annotations.NotNull;

public class InventoryNpcTrader implements Container {

   private final String inventoryTitle;
   private final int slotsCount;
   private final ContainerNPCTrader con;
   public final NonNullList<ItemStack> inventoryContents;

   public InventoryNpcTrader(String title, int size, ContainerNPCTrader con) {
      this.con = con;
      this.inventoryTitle = title;
      this.slotsCount = size;
      this.inventoryContents = NonNullList.withSize(size, ItemStack.EMPTY);
   }

   public @NotNull ItemStack getItem(int index) {
      ItemStack toBuy = this.inventoryContents.get(index);
      return NoppesUtilServer.isItemStackNull(toBuy) ? ItemStack.EMPTY : toBuy.copy();
   }

   public @NotNull ItemStack removeItem(int index, int count) {
      ItemStack stack = this.inventoryContents.get(index);
      return !NoppesUtilServer.isItemStackNull(stack) ? stack.copy() : ItemStack.EMPTY;
   }

   public void setItem(int i, ItemStack itemstack) {
      if (!itemstack.isEmpty()) {
         this.inventoryContents.set(i, itemstack.copy());
      }

      this.setChanged();
   }

   public int getContainerSize() {
      return this.slotsCount;
   }

   public int getMaxStackSize() {
      return 64;
   }

   public boolean stillValid(@NotNull Player player) {
      return true;
   }

   public @NotNull ItemStack removeItemNoUpdate(int index) {
      return this.inventoryContents.set(index, ItemStack.EMPTY);
   }

   public boolean canPlaceItem(int index, @NotNull ItemStack itemstack) {
      return true;
   }

   public void setChanged() {
      this.con.slotsChanged(this);
   }

   public void startOpen(@NotNull Player player) {
   }

   public void stopOpen(@NotNull Player player) {
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
