package noppes.npcs.containers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.oredict.OreDictionary;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.mixin.core.NonNullListMixin;
import noppes.npcs.mixin.inv.IInventoryBasicMixin;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class NpcMiscInventory extends InventoryBasic {

	public int stackLimit;

	public NpcMiscInventory(int size) { this("", true, size); }

	public NpcMiscInventory(String ignoredTitle, boolean ignoredCustomName, int size) {
		super("Npc Misc Inventory", true, size);
		stackLimit = 64;
	}

	public void addItemStack(ItemStack item) {
        ItemStack mergable;
		while (!(mergable = getMergableItem(item)).isEmpty() && mergable.getCount() > 0) {
			int size = mergable.getMaxStackSize() - mergable.getCount();
			if (size > item.getCount()) {
				mergable.setCount(mergable.getMaxStackSize());
				item.setCount(item.getCount() - size);
            }
			else {
				mergable.setCount(mergable.getCount() + item.getCount());
				item.setCount(0);
			}
		}
		if (item.getCount() <= 0) { return; }
		int slot = firstFreeSlot();
		if (slot >= 0) {
			setInventorySlotContents(slot, item.copy());
			item.setCount(0);
		}
	}

	@Override
	public @Nonnull ItemStack decrStackSize(int index, int count) {
		return ItemStackHelper.getAndSplit(((IInventoryBasicMixin) this).getItems(), index, count);
	}

	public boolean decrStackSize(ItemStack eating, int decrease) {
		for (int slot = 0; slot < getSizeInventory(); ++slot) {
			ItemStack item = getStackInSlot(slot);
			if (!item.isEmpty() && eating == item && item.getCount() >= decrease) {
				item.splitStack(decrease);
				if (item.getCount() <= 0) { setInventorySlotContents(slot, ItemStack.EMPTY); }
				return true;
			}
		}
		return false;
	}

	// New from Unofficial (BetaZavr)
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void setNewSize(int sizeIn) {
		if (sizeIn != getSizeInventory()) {
			((IInventoryBasicMixin) this).setSize(sizeIn);
			ItemStack[] newList = new ItemStack[sizeIn];
			Arrays.fill(newList, ItemStack.EMPTY);
			for (int i = 0; i < getSizeInventory() && i < sizeIn; i++) { newList[i] = getStackInSlot(i); }
			((NonNullListMixin) ((IInventoryBasicMixin) this).getItems()).setList(Arrays.asList(newList));
			markDirty();
		}
	}

	public NpcMiscInventory fill(IInventory inv) {
		((IInventoryBasicMixin) this).getItems().clear();
		for (int i = 0; i < getSizeInventory() && i < inv.getSizeInventory(); i++) { setInventorySlotContents(i, inv.getStackInSlot(i)); }
		markDirty();
		return this;
	}

	public int firstFreeSlot() {
		for (int i = 0; i < getSizeInventory(); ++i) {
			if ((getStackInSlot(i)).isEmpty()) { return i; }
		}
		return -1;
	}

	public int getCountEmpty() {
		int c = 0;
		for (int s = 0; s < getSizeInventory(); ++s) {
			if (getStackInSlot(s).isEmpty()) { c++; }
		}
		return c;
	}

	@Override
	public @Nonnull ITextComponent getDisplayName() { return new Component("Custom Inventory").getParent(); }

	@Override
	public int getInventoryStackLimit() { return stackLimit;}

	public ItemStack getMergableItem(ItemStack item) {
		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack is = getStackInSlot(i);
			if (compareItems(item, is) && is.getCount() < is.getMaxStackSize()) { return is; }
		}
		return ItemStack.EMPTY;
	}

	private boolean compareItems(ItemStack stack_0, ItemStack stack_1) {
		if (!NoppesUtilServer.isItemStackNull(stack_0) && !NoppesUtilServer.isItemStackNull(stack_1)) {
			OreDictionary.itemMatches(stack_0, stack_1, false); // meta
			int[] ids = OreDictionary.getOreIDs(stack_0);
			for (int id : ids) {
				boolean match1 = false;
				boolean match2 = false;
				for (ItemStack is : OreDictionary.getOres(OreDictionary.getOreName(id))) {
					if (compareItemDetails(stack_0, is)) { match1 = true; }
					if (compareItemDetails(stack_1, is)) { match2 = true; }
				}
				if (match1 && match2) { return true; }
			}
			return compareItemDetails(stack_0, stack_1);
		}
		return false;
	}

	private boolean compareItemDetails(ItemStack stack_0, ItemStack stack_1) {
		return stack_0.getItem() == stack_1.getItem()
				&& (stack_0.getItemDamage() == -1 || stack_0.getItemDamage() == stack_1.getItemDamage())
				&& (stack_0.getTagCompound() == null || (stack_1.getTagCompound() != null && stack_0.getTagCompound().equals(stack_1.getTagCompound())))
				&& (stack_1.getTagCompound() == null || stack_0.getTagCompound() != null);
	}

	public NBTTagCompound save() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("NpcMiscInv", NBTTags.nbtItemStackList(this));
		compound.setInteger("NpcMiscInvSize", getSizeInventory());
		return compound;
	}

	public boolean isFull() {
		for (int slot = 0; slot < getSizeInventory(); ++slot) {
			if (getStackInSlot(slot).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public void load(NBTTagCompound compound) {
		NBTTags.getItemStackList(compound.getTagList("NpcMiscInv", 10), ((IInventoryBasicMixin) this).getItems());
	}

	// New from Unofficial (BetaZavr)
	@Override
	public void setInventorySlotContents(int slotId, @Nonnull ItemStack stack) {
		if (slotId >= getSizeInventory()) { return; }
		super.setInventorySlotContents(slotId, stack);
	}

}
