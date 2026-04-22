package noppes.npcs.containers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleFollower;
import org.jetbrains.annotations.NotNull;

public class ContainerNPCFollowerHire extends ContainerNpcInterface {

   public final SimpleContainer currencyMatrix;
   public final RoleFollower role;

   public ContainerNPCFollowerHire(int containerId, Inventory playerInventory, int entityId, BlockPos data) {
      super(CustomContainer.container_followerhire, containerId, playerInventory);
      EntityNPCInterface npc = (EntityNPCInterface)player.level().getEntity(entityId);
      if (npc != null) { role = (RoleFollower) npc.role; }
      else { role = new RoleFollower(null); }
      currencyMatrix = new SimpleContainer(1);

      int offSet = data.getX() == 0 ? 0 : 58;
      int size = role.inventory.getContainerSize();
      if (size > 0) {
         int s = (size == 2 || size == 4) ? 2 : 3;
         boolean bo = false;
         for (int y = 0; y < s; ++y) {
            for (int x = 0; x < s; ++x) {
               bo = (x + y * s) >= size;
               if (!bo) {
                  addSlot(new Slot(role.inventory, x + y * s, 174 + x * 18, 142 + y * 18));
               }
            }
            if (bo) { break; }
         }
      }
      for(int y = 0; y < 3; ++y) {
         for(int x = 0; x < 9; ++x) {
            addSlot(new Slot(player.getInventory(), x + y * 9 + 9, 8 + x * 18, 84 + y * 18 + offSet));
         }
      }
      for(int x = 0; x < 9; ++x) {
         addSlot(new Slot(player.getInventory(), x, 8 + x * 18, 142 + offSet));
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
