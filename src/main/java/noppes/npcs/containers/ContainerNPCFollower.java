package noppes.npcs.containers;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.containers.inventories.InventoryNPC;
import noppes.npcs.containers.slots.SlotNpcMercenaryCurrency;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleFollower;

import javax.annotation.Nonnull;

public class ContainerNPCFollower extends ContainerNpcInterface {

   public final InventoryNPC currencyMatrix;
   public final RoleFollower role;

   public ContainerNPCFollower(int containerId, Inventory playerInventory, int entityId) {
      super(CustomContainer.container_follower, containerId, playerInventory);
      EntityNPCInterface npc = (EntityNPCInterface) player.level().getEntity(entityId);
      if (npc != null) { role = (RoleFollower) npc.role; }
      else { role = new RoleFollower(null); }
      currencyMatrix = new InventoryNPC("currency", 1, this);
      addSlot(new SlotNpcMercenaryCurrency(role, currencyMatrix, 0, 26, 9));
      for(int j1 = 0; j1 < 9; ++j1) {
         addSlot(new Slot(player.getInventory(), j1, 8 + j1 * 18, 142));
      }
   }

   @Override
   public void removed(@Nonnull Player playerIn) {
      super.removed(playerIn);
      if (!playerIn.level().isClientSide) {
         ItemStack itemstack = currencyMatrix.removeItemNoUpdate(0);
         if (!NoppesUtilServer.isItemStackNull(itemstack)) { playerIn.spawnAtLocation(itemstack, 0.0F); }
      }
   }

}
