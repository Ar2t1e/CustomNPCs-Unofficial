package noppes.npcs.containers.slots;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.roles.RoleFollower;

public class SlotNpcMercenaryCurrency extends Slot {

   final RoleFollower role;

   public SlotNpcMercenaryCurrency(RoleFollower roleIn, Container inv, int slotIndex, int x, int y) {
      super(inv, slotIndex, x, y);
      role = roleIn;
   }

   @Override
   public int getMaxStackSize() { return 64; }

   @Override
   public boolean mayPlace(ItemStack itemstack) {
      Item item = itemstack.getItem();
      for (int i = 0; i < role.inventory.getContainerSize(); i++) {
         if (role.inventory.getItem(i).getItem() == item) { return true; }
      }
      return false;
   }

}
