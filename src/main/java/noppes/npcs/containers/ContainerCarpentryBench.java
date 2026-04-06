package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeContainer;
import noppes.npcs.CustomBlocks;

import javax.annotation.Nonnull;

// ContainerWorkbench
public class ContainerCarpentryBench
		extends Container
		implements IRecipeContainer {

	public static final int RESULT_SLOT = 0;
	public static final int CRAFT_SLOT_START = 1;
	public static final int CRAFT_SLOT_END = CRAFT_SLOT_START + 4 * 4; // 17
	public static final int INV_SLOT_START = CRAFT_SLOT_END; // 17
	public static final int INV_SLOT_END = INV_SLOT_START + 27; // 44
	public static final int USE_ROW_SLOT_START = INV_SLOT_END; // 44
	public static final int USE_ROW_SLOT_END = USE_ROW_SLOT_START + 9; // 53

	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 4, 4);
	public InventoryCraftResult craftResult = new InventoryCraftResult();
	private final EntityPlayer player;
    private final BlockPos pos;
	private final World world;
	public boolean isShowBook = false;

	public ContainerCarpentryBench(InventoryPlayer playerInventory, World worldIn, BlockPos posIn) {
		world = worldIn;
		pos = posIn;
		player = playerInventory.player;
		// craftResult slot ID any = 0? next slots in craftMatrix:
		addSlotToContainer(new SlotCrafting(playerInventory.player, craftMatrix, craftResult, 0, 140, 41));
		for (int y = 0; y < 4; ++y) {
			for (int x = 0; x < 4; ++x) {
				addSlotToContainer(new Slot(craftMatrix, x + y * 4, 30 + x * 18, 14 + y * 18));
			}
		}
		// next slots in playerInventory:
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				addSlotToContainer(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 98 + y * 18));
			}
		}
		for (int x = 0; x < 9; ++x) {
			addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18, 156));
		}
		onCraftMatrixChanged(craftMatrix);
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
		return world.getBlockState(pos).getBlock() == CustomBlocks.carpentyBench &&
				playerIn.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public boolean canMergeSlot(@Nonnull ItemStack stack, @Nonnull Slot slotIn) {
		return slotIn.inventory != craftResult && super.canMergeSlot(stack, slotIn);
	}

	@Override
	public void onContainerClosed(@Nonnull EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		if (!world.isRemote) { clearContainer(playerIn, world, craftMatrix); }
	}

	@Override
	public void onCraftMatrixChanged(@Nonnull IInventory inventoryIn) {
		slotChangedCraftingGrid(world, player, craftMatrix, craftResult);
	}

	@Override
	public @Nonnull ItemStack transferStackInSlot(@Nonnull EntityPlayer playerIn, int slotIn) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(slotIn);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (slotIn == RESULT_SLOT) {
				itemstack1.getItem().onCreated(itemstack1, world, playerIn);
				if (!mergeItemStack(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
					return ItemStack.EMPTY;
				}
				slot.onSlotChange(itemstack1, itemstack);
			}
			else if (slotIn >= INV_SLOT_START && slotIn < INV_SLOT_END) {
				if (!mergeItemStack(itemstack1, INV_SLOT_END, USE_ROW_SLOT_END, false)) {
					return ItemStack.EMPTY;
				}
			}
			else if (slotIn >= USE_ROW_SLOT_START && slotIn < USE_ROW_SLOT_END) {
				if (!mergeItemStack(itemstack1, INV_SLOT_START, INV_SLOT_END, false)) {
					return ItemStack.EMPTY;
				}
			}
			else if (!mergeItemStack(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
				return ItemStack.EMPTY;
			}
			if (itemstack1.isEmpty()) { slot.putStack(ItemStack.EMPTY); }
			else { slot.onSlotChanged(); }
			if (itemstack1.getCount() == itemstack.getCount()) { return ItemStack.EMPTY; }
			ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);
			if (slotIn == 0) { playerIn.dropItem(itemstack2, false); }
		}
		return itemstack;
	}

	@Override
	public InventoryCraftResult getCraftResult() { return craftResult; }

	@Override
	public InventoryCrafting getCraftMatrix() { return craftMatrix;	}

	public void checkPos(boolean showBook) {
		if (isShowBook != showBook) {
			int offsetX = (showBook ? 1 : -1 ) * 77;
			for (Slot slot : inventorySlots) {
				slot.xPos += offsetX;
			}
			isShowBook = showBook;
		}
	}

}
