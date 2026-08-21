package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.blocks.custom.tiles.CustomTileEntityChest;

import javax.annotation.Nonnull;

public class ContainerChestCustom extends Container {

	public BlockPos pos;
	public int height;
	public EntityPlayer player;
	public CustomTileEntityChest customChest;

	public ContainerChestCustom(InventoryPlayer playerInventory, CustomTileEntityChest customChestIn, EntityPlayer playerIn) {
		pos = customChestIn.getPos();
		player = playerIn;
		customChest = customChestIn;
		customChest.openInventory(player);
		if (player.world.isRemote) { customChest = customChest.copy(); }
		int h = ((int) Math.ceil((double) customChest.getSizeInventory() / 9.0d) - 4) * 18;
		int w = 0;
		if (customChest.getSizeInventory() > 45) { h = 18; }
		h -= 6;
		// Inventory
		if (customChest.getSizeInventory() > 45) { // Creative
			w = 8;
			height = 5 * 18;
			for (int i = 0; i < customChest.getSizeInventory(); i++) {
				addSlotToContainer(new Slot(customChest, i, -5000, -5000));
			}
		} else { // 9x(2 / 5)
			height = (int) Math.ceil((double) customChest.getSizeInventory() / 9.0d) * 18;
			int u = 0, e = customChest.getSizeInventory();
			if (customChest.getSizeInventory() % 9 != 0) {
				e -= customChest.getSizeInventory() % 9;
			}
			for (int i = 0; i < customChest.getSizeInventory(); i++) {
				if (i >= e) {
					u = (int) (((9.0d - ((double) customChest.getSizeInventory() % 9.0d)) / 2.0d) * 18.0d);
				}
				addSlotToContainer(new Slot(customChest, i, 8 + u + (i % 9) * 18,
						18 + (int) Math.floor((double) i / 9.0d) * 18));
			}
		}
		// Player Inventory
		for (int r = 0; r < 3; ++r) {
			for (int p = 0; p < 9; ++p) {
				addSlotToContainer(new Slot(playerInventory, p + r * 9 + 9, 8 + w + p * 18, 103 + r * 18 + h));
			}
		}
		for (int p = 0; p < 9; ++p) {
			addSlotToContainer(new Slot(playerInventory, p, 8 + w + p * 18, 161 + h));
		}
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
		return true;
	}

	public BlockPos getPos() {
		return pos;
	}

	public void onContainerClosed(@Nonnull EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		customChest.closeInventory(playerIn);
	}

	public @Nonnull ItemStack transferStackInSlot(@Nonnull EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (index < customChest.items.size()) {
				if (!mergeItemStack(itemstack1, customChest.items.size(), inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!mergeItemStack(itemstack1, 0, customChest.items.size(), false)) {
				return ItemStack.EMPTY;
			}
			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}
		return itemstack;
	}

}
