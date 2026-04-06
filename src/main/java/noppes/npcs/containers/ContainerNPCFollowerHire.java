package noppes.npcs.containers;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.containers.slots.SlotNpcMercenaryCurrency;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleFollower;
import org.jetbrains.annotations.NotNull;

public class ContainerNPCFollowerHire extends ContainerNpcInterface {

   public final SimpleContainer currencyMatrix;
   public final RoleFollower role;

   public ContainerNPCFollowerHire(int containerId, Inventory playerInventory, int entityId) {
      super(CustomContainer.container_followerhire, containerId, playerInventory);
      EntityNPCInterface npc = (EntityNPCInterface)player.level().getEntity(entityId);
      if (npc != null) { role = (RoleFollower) npc.role; }
      else { role = new RoleFollower(null); }
      currencyMatrix = new SimpleContainer(1);
      addSlot(new SlotNpcMercenaryCurrency(role, currencyMatrix, 0, 44, 35));

      int j1;
      for(j1 = 0; j1 < 3; ++j1) {
         for(int l1 = 0; l1 < 9; ++l1) {
            addSlot(new Slot(player.getInventory(), l1 + j1 * 9 + 9, 8 + l1 * 18, 84 + j1 * 18));
         }
      }
      for(j1 = 0; j1 < 9; ++j1) {
         addSlot(new Slot(player.getInventory(), j1, 8 + j1 * 18, 142));
      }
   }

   @Override
   public void removed(@NotNull Player playerIn) {
      super.removed(playerIn);
      if (!playerIn.level().isClientSide) {
         ItemStack itemstack = currencyMatrix.removeItemNoUpdate(0);
         if (!NoppesUtilServer.isItemStackNull(itemstack) && !playerIn.level().isClientSide) {
            playerIn.spawnAtLocation(itemstack, 0.0F);
         }
      }
   }

}
