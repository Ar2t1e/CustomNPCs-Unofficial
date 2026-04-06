package noppes.npcs.containers;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.controllers.data.Marcet;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleTrader;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class ContainerNPCTrader extends ContainerNpcInterface {

   protected final Inventory inv;
   public Marcet marcet;

   public ContainerNPCTrader(int containerId, Inventory playerInventory, EntityNPCInterface npc, int marcetId) {
      super(CustomContainer.container_trader, containerId, playerInventory);
      inv = playerInventory;
      if (npc != null && npc.role instanceof RoleTrader role) { marcet = (Marcet) role.getMarket(); }
      else if (marcetId >= 0) { marcet = MarcetController.getInstance().getMarcet(marcetId); }
      else { marcet = null; }

      if (marcet != null && player instanceof ServerPlayer) { marcet.addListener(player, true); }
      for (int i2 = 0; i2 < 3; ++i2) {
         for (int l1 = 0; l1 < 9; ++l1) {
            addSlot(new Slot(inv, l1 + i2 * 9 + 9, 1 + l1 * 18, i2 * 18 - 5));
         }
      }
      for (int j1 = 0; j1 < 9; ++j1) {
         addSlot(new Slot(inv, j1, 1 + j1 * 18, 71));
      }
   }

   @Override
   public boolean stillValid(@Nonnull Player entityplayer) { return true; }

   @Override
   public @Nonnull ItemStack quickMoveStack(@Nonnull Player player, int slotId) { return ItemStack.EMPTY; }

   @Override
   public void removed(@NotNull Player playerIn) {
      super.removed(playerIn);
      if (playerIn instanceof ServerPlayer) { marcet.removeListener(playerIn, true); }
   }

   private void reAddSlot(Slot slot) {
      slot.index = slots.size();
      slots.add(slot);
   }

   public void reset(int width, int height) {
      slots.clear();
      int offsetX = width - 169;
      int offsetY = height - 79;
      for (int i2 = 0; i2 < 3; ++i2) {
         for (int l1 = 0; l1 < 9; ++l1) {
            reAddSlot(new Slot(inv, l1 + i2 * 9 + 9, offsetX + l1 * 18, offsetY + i2 * 18 - 4));
         }
      }
      for (int j1 = 0; j1 < 9; ++j1) {
         reAddSlot(new Slot(inv, j1, offsetX + j1 * 18, offsetY + 54));
      }
   }

}
