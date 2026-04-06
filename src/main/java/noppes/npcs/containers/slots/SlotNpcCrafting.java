package noppes.npcs.containers.slots;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.NoppesUtilServer;
import org.jetbrains.annotations.NotNull;

public class SlotNpcCrafting extends ResultSlot {
   private final CraftingContainer craftMatrix;

   public SlotNpcCrafting(Player player, CraftingContainer craftingInventory, Container inventory, int slotIndex, int x, int y) {
      super(player, craftingInventory, inventory, slotIndex, x, y);
      this.craftMatrix = craftingInventory;
   }

   public void onTake(@NotNull Player player, @NotNull ItemStack itemStack) {
      this.checkTakeAchievements(itemStack);
      for(int i = 0; i < this.craftMatrix.getContainerSize(); ++i) {
         ItemStack itemstack1 = this.craftMatrix.getItem(i);
         if (!NoppesUtilServer.isItemStackNull(itemstack1)) {
            this.craftMatrix.removeItem(i, 1);
            if (itemstack1.getItem().hasCraftingRemainingItem(itemstack1)) {
               ItemStack itemstack2 = itemstack1.getItem().getCraftingRemainingItem(itemstack1);
               if ((NoppesUtilServer.isItemStackNull(itemstack2) || !itemstack2.isDamageableItem() || itemstack2.getDamageValue() <= itemstack2.getMaxDamage()) && !player.getInventory().add(itemstack2)) {
                  if (NoppesUtilServer.isItemStackNull(this.craftMatrix.getItem(i))) {
                     this.craftMatrix.setItem(i, itemstack2);
                  } else {
                     player.drop(itemstack2, false);
                  }
               }
            }
         }
      }
   }

}
