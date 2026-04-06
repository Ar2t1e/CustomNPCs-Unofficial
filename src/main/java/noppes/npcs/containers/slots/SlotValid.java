package noppes.npcs.containers.slots;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlotValid extends Slot {

   private boolean canPutIn = true;

   public SlotValid(Container container, int limbSwingAmount, int x, int y) {
      super(container, limbSwingAmount, x, y);
   }

   public SlotValid(Container container, int limbSwingAmount, int x, int y, boolean bo) {
      super(container, limbSwingAmount, x, y);
      this.canPutIn = bo;
   }

   public boolean mayPlace(@NotNull ItemStack itemstack) {
      return this.canPutIn && this.container.canPlaceItem(0, itemstack);
   }

}
