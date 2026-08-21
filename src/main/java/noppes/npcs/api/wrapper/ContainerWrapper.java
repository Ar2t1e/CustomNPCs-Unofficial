package noppes.npcs.api.wrapper;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.ISlot;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;

import java.util.Objects;

public class ContainerWrapper implements IContainer {

	private Container container;
	private IInventory inventory;

	public ContainerWrapper(Container container) {
		this.container = container;
	}

	public ContainerWrapper(IInventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public int count(IItemStack item, boolean ignoreDamage, boolean ignoreNBT) {
		int count = 0;
		for (int i = 0; i < this.getSize(); ++i) {
			IItemStack toCompare = this.getItem(i);
			if (NoppesUtilPlayer.compareItems(item.getMCItemStack(), toCompare.getMCItemStack(), ignoreDamage,
					ignoreNBT)) {
				count += toCompare.getStackSize();
			}
		}
		return count;
	}

	@Override
	public IItemStack[] getItems() {
		IItemStack[] items = new IItemStack[this.getSize()];
		for (int i = 0; i < this.getSize(); ++i) {
			items[i] = this.getItem(i);
		}
		return items;
	}

	@Override
	public Container getMCContainer() {
		return this.container;
	}

	@Override
	public IInventory getMCInventory() {
		return this.inventory;
	}

	@Override
	public int getSize() {
		if (this.inventory != null) {
			return this.inventory.getSizeInventory();
		}
		return this.container.inventorySlots.size();
	}

	@Override
	public ISlot getSlot(int slotId) {
		if (slotId >= 0 && slotId < this.getSize()) {
			return new WrapperSlot(container.getSlot(slotId));
		} else {
			throw new CustomNPCsException("Slot is out of range " + slotId);
		}
	}

	@Override
	public IItemStack getItem(int slot) {
		if (slot < 0 || slot >= this.getSize()) {
			throw new CustomNPCsException("Slot is out of range " + slot);
		}
		if (this.inventory != null) {
			return Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(this.inventory.getStackInSlot(slot));
		}
		return Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(this.container.getSlot(slot).getStack());
	}

	@Override
	public boolean isEmpty() {
		return this.inventory == null || this.inventory.isEmpty();
	}

	@Override
	public void setItem(int slot, IItemStack item) {
		if (slot < 0 || slot >= this.getSize()) {
			throw new CustomNPCsException("Slot is out of range " + slot);
		}
		ItemStack itemstack = (item == null) ? ItemStack.EMPTY : item.getMCItemStack();
		if (this.inventory != null) {
			this.inventory.setInventorySlotContents(slot, itemstack);
		} else {
			this.container.putStackInSlot(slot, itemstack);
			this.container.detectAndSendChanges();
		}
	}
}
