package noppes.npcs.containers;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomContainer;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.mixin.world.item.crafting.IRecipeManagerMixin;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Map;

// net.minecraft.world.inventory.CraftingMenu
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
   private final Player player;

   private final ContainerLevelAccess access;

   public ContainerCarpentryBench(int id, Inventory inv, BlockPos pos) {
      super(CustomContainer.container_carpentrybench, id);
      access = ContainerLevelAccess.create(inv.player.level(), pos);
      player = inv.player;
      addSlot(new ResultSlot(player, craftSlots, resultSlots, 0, 140, 41));
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
   }

   protected static void slotChangedCraftingGrid(AbstractContainerMenu menu, Level level, Player playerIn,
                                                 CraftingContainer craftingContainer, ResultContainer resultContainer) {
      MinecraftServer server = level.getServer();
      if (!level.isClientSide && server != null && playerIn instanceof ServerPlayer player) {
         ItemStack itemstack = ItemStack.EMPTY;
         Map<ResourceLocation, Recipe<?>> byName = ((IRecipeManagerMixin) server.getRecipeManager()).getByName();
         for (Recipe<?> recipe : new ArrayList<>(byName.values())) {
            if (recipe instanceof RecipeCarpentry npcRecipe && !npcRecipe.isGlobal() &&
                    npcRecipe.matches(craftingContainer, level) && resultContainer.setRecipeUsed(level, player, recipe)) {
               ItemStack itemstack1 = npcRecipe.assemble(craftingContainer, level.registryAccess());
               if (itemstack1.isItemEnabled(level.enabledFeatures())) {
                  itemstack = itemstack1;
               }
               break;
            }
         }
         resultContainer.setItem(0, itemstack);
         menu.setRemoteSlot(0, itemstack);
         player.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), 0, itemstack));
      }
   }

   @Override
   public void slotsChanged(@Nonnull Container container) {
      access.execute((level, blockPos) -> slotChangedCraftingGrid(this, level, player, craftSlots, resultSlots));
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
   public boolean recipeMatches(Recipe<? super CraftingContainer> container) { return container.matches(craftSlots, player.level()); }

   @Override
   public void removed(@Nonnull Player playerIn) {
      super.removed(playerIn);
      access.execute((level, blockPos) -> clearContainer(playerIn, craftSlots));
   }

   @Override
   public boolean stillValid(@Nonnull Player playerIn) { return stillValid(access, playerIn, CustomBlocks.carpenty); }

   @Override
   public @Nonnull ItemStack quickMoveStack(@Nonnull Player playerIn, int slotIn) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = slots.get(slotIn);
      if (slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if (slotIn == RESULT_SLOT) {
            access.execute((level, blockPos) -> itemstack1.getItem().onCraftedBy(itemstack1, level, playerIn));
            if (!moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, true)) { return ItemStack.EMPTY; }
            slot.onQuickCraft(itemstack1, itemstack);
         }
         else if (slotIn >= INV_SLOT_START && slotIn < USE_ROW_SLOT_END) {
            if (!moveItemStackTo(itemstack1, CRAFT_SLOT_START, CRAFT_SLOT_END, false)) {
               if (slotIn < USE_ROW_SLOT_START) {
                  if (!moveItemStackTo(itemstack1, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) { return ItemStack.EMPTY; }
               }
               else if (!moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_START, false)) { return ItemStack.EMPTY; }
            }
            if (!moveItemStackTo(itemstack1, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) { return ItemStack.EMPTY; }
         }
         else if (!moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, false)) { return ItemStack.EMPTY; }
         if (itemstack1.isEmpty()) { slot.setByPlayer(ItemStack.EMPTY); }
         else { slot.setChanged(); }
         if (itemstack1.getCount() == itemstack.getCount()) { return ItemStack.EMPTY; }
         slot.onTake(playerIn, itemstack1);
         if (slotIn == RESULT_SLOT) { playerIn.drop(itemstack1, false); }
      }
      return itemstack;
   }

   @Override
   public boolean canTakeItemForPickAll(@Nonnull ItemStack stack, Slot slotIn) {
      return slotIn.container != resultSlots && super.canTakeItemForPickAll(stack, slotIn);
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
