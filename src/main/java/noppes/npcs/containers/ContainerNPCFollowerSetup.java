package noppes.npcs.containers;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleFollower;
import org.jetbrains.annotations.NotNull;

public class ContainerNPCFollowerSetup extends AbstractContainerMenu {

   public final RoleFollower role;

   public ContainerNPCFollowerSetup(int containerId, Inventory playerInventory, int entityId) {
      super(CustomContainer.container_followersetup, containerId);
      EntityNPCInterface npc = (EntityNPCInterface)playerInventory.player.level().getEntity(entityId);
      if (npc != null) { role = (RoleFollower) npc.role; }
      else { role = new RoleFollower(null); }
      for(int y = 0; y < 3; ++y) {
         addSlot(new Slot(role.rentalItems, y, 44, 39 + y * 25));
      }
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
   public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = slots.get(index);
      if (slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if (index >= 0 && index < 3) {
            if (!moveItemStackTo(itemstack1, 3, 38, true)) { return ItemStack.EMPTY; }
         } else if (index >= 3 && index < 30) {
            if (!moveItemStackTo(itemstack1, 30, 38, false)) { return ItemStack.EMPTY; }
         } else if (index >= 30 && index < 38) {
            if (!moveItemStackTo(itemstack1, 3, 29, false)) { return ItemStack.EMPTY; }
         }
         else if (!moveItemStackTo(itemstack1, 3, 38, false)) { return ItemStack.EMPTY; }
         if (itemstack1.getCount() == 0) { slot.set(ItemStack.EMPTY); }
         else { slot.setChanged(); }
         if (itemstack1.getCount() == itemstack.getCount()) { return ItemStack.EMPTY; }
         slot.onTake(playerIn, itemstack1);
      }
      return itemstack;
   }

   @Override
   public boolean stillValid(@NotNull Player playerIn) { return true; }

}
