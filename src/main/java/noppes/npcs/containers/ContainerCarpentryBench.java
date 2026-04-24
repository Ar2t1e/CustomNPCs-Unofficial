package noppes.npcs.containers;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomContainer;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.mixin.world.inventory.ISlotMixin;

import javax.annotation.Nonnull;

// net.minecraft.world.inventory.InventoryMenu
public class ContainerCarpentryBench extends RecipeBookMenu<CraftingContainer> {

   public static final int RESULT_SLOT = 0;
   public static final int CRAFT_SLOT_START = 1;
   public static final int CRAFT_SLOT_END = CRAFT_SLOT_START + 4 * 4; // 17
   public static final int INV_SLOT_START = CRAFT_SLOT_END; // 17
   public static final int INV_SLOT_END = INV_SLOT_START + 27; // 44
   public static final int USE_ROW_SLOT_START = INV_SLOT_END; // 44
   public static final int USE_ROW_SLOT_END = USE_ROW_SLOT_START + 9; // 53
   private final CraftingContainer craftSlots = new TransientCraftingContainer(this, 4, 4);
   private final ResultContainer resultSlots = new ResultContainer();
   public final boolean active = true;
   private final Player owner;
   public boolean isShowBook = false;

   private final BlockPos pos;

   public ContainerCarpentryBench(int id, Inventory inv, BlockPos posIn) {
      super(CustomContainer.container_carpentrybench, id);
      pos = posIn;
      owner = inv.player;
      // RESULT_SLOT
      addSlot(new ResultSlot(owner, craftSlots, resultSlots, 0, 140, 41));

      for(int y = 0; y < 4; ++y) {
         for(int x = 0; x < 4; ++x) {
            addSlot(new Slot(craftSlots, x + y * 4, 30 + x * 18, 14 + y * 18));
         }
      }
      for(int y = 0; y < 3; ++y) {
         for(int x = 0; x < 9; ++x) {
            addSlot(new Slot(inv, x + y * 9 + 9, 8 + x * 18, 98 + y * 18));
         }
      }
      for(int x = 0; x < 9; ++x) { addSlot(new Slot(inv, x, 8 + x * 18, 156)); }

      slotsChanged(craftSlots);
   }

   public static boolean isHotbarSlot(int p_150593_) {
      return p_150593_ >= 36 && p_150593_ < 45 || p_150593_ == 45;
   }

   @Override
   public void slotsChanged(@Nonnull Container container) {
      //CraftingMenu.slotChangedCraftingGrid(this, owner.level(), owner, craftSlots, resultSlots);
      if (!owner.level().isClientSide() && owner instanceof ServerPlayer player) {
         ItemStack itemstack = ItemStack.EMPTY;
         RecipeCarpentry recipe = RecipeController.getInstance().findMatchingAnvilRecipe(craftSlots, owner);
         if (recipe != null) {
            itemstack = recipe.assemble(craftSlots, owner.level().registryAccess());
         }
         resultSlots.setItem(RESULT_SLOT, itemstack);
         setRemoteSlot(RESULT_SLOT, itemstack);
         player.connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, itemstack));
      }
   }

   @Override
   public void removed(@Nonnull Player playerIn) {
      super.removed(playerIn);
      if (!playerIn.level().isClientSide) {
         for(int i = 0; i < 16; ++i) {
            ItemStack itemStack = craftSlots.removeItemNoUpdate(i);
            if (!itemStack.isEmpty()) { playerIn.drop(itemStack, false); }
         }
      }
   }

   @Override
   public boolean stillValid(Player playerIn) {
      return playerIn.level().getBlockState(pos).getBlock() == CustomBlocks.carpenty &&
              playerIn.distanceToSqr((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D) <= 64.0D;
   }

   @Override
   public @Nonnull ItemStack quickMoveStack(@Nonnull Player playerIn, int slotIn) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = slots.get(slotIn);
      if (slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();

         if (slotIn == RESULT_SLOT) {
            if (!moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, true)) { return ItemStack.EMPTY; }
            slot.onQuickCraft(itemstack1, itemstack);
         }
         else if (slotIn >= INV_SLOT_START && slotIn < INV_SLOT_END) {
            if (!moveItemStackTo(itemstack1, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) { return ItemStack.EMPTY; }
         }
         else if (slotIn >= USE_ROW_SLOT_START && slotIn < USE_ROW_SLOT_END) {
            if (!moveItemStackTo(itemstack1, INV_SLOT_START, INV_SLOT_END, false)) { return ItemStack.EMPTY; }
         }
         else if (!moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, false)) { return ItemStack.EMPTY; }
         if (itemstack1.getCount() == 0) { slot.set(ItemStack.EMPTY); }
         else { slot.setChanged(); }

         if (itemstack1.getCount() == itemstack.getCount()) { return ItemStack.EMPTY; }
         slot.onTake(playerIn, itemstack1);
      }
      return itemstack;
   }

   @Override
   public boolean canTakeItemForPickAll(@Nonnull ItemStack stack, Slot slotIn) {
      return slotIn.container != craftSlots && super.canTakeItemForPickAll(stack, slotIn);
   }

   public void checkPos(boolean showBook) {
      if (isShowBook != showBook) {
         int offsetX = (showBook ? 1 : -1 ) * 77;
         for (Slot slot : slots) { ((ISlotMixin) slot).setX(slot.x + offsetX); }
         isShowBook = showBook;
      }
   }

   @Override
   public void fillCraftSlotsStackedContents(@Nonnull StackedContents contents) {
      craftSlots.fillStackedContents(contents);
   }

   @Override
   public void clearCraftingContent() {
      resultSlots.clearContent();
      craftSlots.clearContent();
   }

   @Override
   public boolean recipeMatches(Recipe<? super CraftingContainer> container) {
      return container.matches(craftSlots, owner.level());
   }

   @Override
   public int getResultSlotIndex()  { return RESULT_SLOT; }

   @Override
   public int getGridWidth() { return craftSlots.getWidth(); }

   @Override
   public int getGridHeight() { return craftSlots.getHeight(); }

   @Override
   public int getSize() { return 17; }

   @Override
   public @Nonnull RecipeBookType getRecipeBookType() { return RecipeController.CRAFTING_CUSTOM_ANVIL; }

   @Override
   public boolean shouldMoveToInventory(int slotIn) { return slotIn != getResultSlotIndex(); }


}
