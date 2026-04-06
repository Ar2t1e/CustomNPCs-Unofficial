package noppes.npcs.containers;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobItemGiver;
import org.jetbrains.annotations.NotNull;

public class ContainerNpcItemGiver extends AbstractContainerMenu {

   public final JobItemGiver role;

   public ContainerNpcItemGiver(int containerId, Inventory playerInventory, int entityId) {
      super(CustomContainer.container_itemgiver, containerId);
      EntityNPCInterface npc = (EntityNPCInterface)playerInventory.player.level().getEntity(entityId);
      if (npc != null) { role = (JobItemGiver)npc.job; }
      else { role = new JobItemGiver(null); }

      int j1;
      for(j1 = 0; j1 < 9; ++j1) {
         this.addSlot(new Slot(this.role.inventory, j1, 6 + j1 * 18, 90));
      }

      for(j1 = 0; j1 < 3; ++j1) {
         for(int l1 = 0; l1 < 9; ++l1) {
            this.addSlot(new Slot(playerInventory, l1 + j1 * 9 + 9, 6 + l1 * 18, 116 + j1 * 18));
         }
      }

      for(j1 = 0; j1 < 9; ++j1) {
         this.addSlot(new Slot(playerInventory, j1, 6 + j1 * 18, 174));
      }

   }

   public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
      return ItemStack.EMPTY;
   }

   public boolean stillValid(@NotNull Player playerIn) {
      return true;
   }
}
