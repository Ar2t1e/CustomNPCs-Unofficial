package noppes.npcs.containers;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomContainer;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.controllers.data.Deal;
import noppes.npcs.controllers.data.Marcet;
import org.jetbrains.annotations.NotNull;

public class ContainerNPCTraderSetup extends AbstractContainerMenu {

   public Deal deal;
   public Marcet marcet;

   @SuppressWarnings("all")
   public ContainerNPCTraderSetup(int containerId, Inventory playerInventory, int npcID, BlockPos tempPos) {
      super(CustomContainer.container_tradersetup, containerId);
      MarcetController mData = MarcetController.getInstance();
      marcet = mData.getMarcet(tempPos.getX());
      if (marcet == null) { marcet = new Marcet(tempPos.getX()); }
      deal = mData.getDeal(tempPos.getY());
      if (deal == null) { deal = new Deal(tempPos.getY()); }
      else if (deal != null && !(playerInventory.player instanceof ServerPlayer)) { deal = deal.copy(); }
      if (deal != null) {
         addSlot(new Slot(deal.getMCInventoryProduct(), 0, 26, 17)); // 215
         for (int v = 0; v < 3; ++v) {
            for (int u = 0; u < 3; ++u) { addSlot(new Slot(deal.getMCInventoryCurrency(), u + v * 3, 8 + u * 18, 54 + v * 18)); }
         }
      }
      for (int i2 = 0; i2 < 3; ++i2) {
         for (int l1 = 0; l1 < 9; ++l1) {
            addSlot(new Slot(playerInventory, l1 + i2 * 9 + 9, 8 + l1 * 18, 135 + i2 * 18));
         }
      }
      for (int j1 = 0; j1 < 9; ++j1) {
         addSlot(new Slot(playerInventory, j1, 8 + j1 * 18, 193));
      }
   }

   @Override
   public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) { return ItemStack.EMPTY; }

   @Override
   public boolean stillValid(@NotNull Player playerIn) { return true; }

   @OnlyIn(Dist.CLIENT)
   public void setSlotPos(int slotID, int[] newPos) {
      if (newPos == null || newPos.length < 2 || slotID < 0 || slotID > 9) { return; }
      slots.set(slotID, new Slot(deal.getMCInventoryProduct(), 0, newPos[0], newPos[1]));
   }

}
