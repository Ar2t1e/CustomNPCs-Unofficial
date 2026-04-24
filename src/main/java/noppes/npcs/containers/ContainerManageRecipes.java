package noppes.npcs.containers;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomContainer;

import javax.annotation.Nonnull;

public class ContainerManageRecipes extends AbstractContainerMenu {

   public ContainerManageRecipes(int containerId, Inventory playerInventory) {
      super(CustomContainer.container_managerecipes, containerId);
      for(int y = 0; y < 3; ++y) {
         for(int x = 0; x < 9; ++x) {
            addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 113 + y * 18));
         }
      }
      for(int x = 0; x < 9; ++x) {
         addSlot(new Slot(playerInventory, x, 8 + x * 18, 171));
      }
   }

   @Override
   public @Nonnull ItemStack quickMoveStack(@Nonnull Player playerIn, int i) { return ItemStack.EMPTY; }

   @Override
   public boolean stillValid(@Nonnull Player playerIn) { return true; }

}
