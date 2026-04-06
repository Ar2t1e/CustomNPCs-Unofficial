package noppes.npcs.containers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.NoppesUtilServer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContainerMerchantAdd extends ContainerNpcInterface {

	private final InventoryBasic merchantInventory;
	public final @Nullable EntityVillager trader;

    public ContainerMerchantAdd(EntityPlayer player, int entityId) {
		super(player);
		Entity e = player.world.getEntityByID(entityId);
		if (e instanceof EntityVillager) { trader = (EntityVillager) e; } else { trader = null; }
		merchantInventory = new InventoryBasic("", false, 3);
		addSlotToContainer(new Slot(merchantInventory, 0, 36, 53));
		addSlotToContainer(new Slot(merchantInventory, 1, 62, 53));
		addSlotToContainer(new Slot(merchantInventory, 2, 120, 53));
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	}

	@Override
	public void onContainerClosed(@Nonnull EntityPlayer player) {
		super.onContainerClosed(player);
		trader.setCustomer(null);
		super.onContainerClosed(player);
		if (!player.world.isRemote) {
			ItemStack itemstack = merchantInventory.removeStackFromSlot(0);
			if (!NoppesUtilServer.isItemStackNull(itemstack)) {
				player.dropItem(itemstack, false);
			}
			itemstack = merchantInventory.removeStackFromSlot(1);
			if (!NoppesUtilServer.isItemStackNull(itemstack)) {
				player.dropItem(itemstack, false);
			}
		}
	}

	@Override
	public void onCraftMatrixChanged(@Nonnull IInventory inventory) {
		super.onCraftMatrixChanged(inventory);
	}

	@Override
	public @Nonnull ItemStack transferStackInSlot(@Nonnull EntityPlayer player, int par2) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(par2);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack2 = slot.getStack();
			itemstack = itemstack2.copy();
			if (par2 != 0 && par2 != 1 && par2 != 2) {
				if (par2 < 30) {
					if (!mergeItemStack(itemstack2, 30, 39, false)) {
						return ItemStack.EMPTY;
					}
				} else if (par2 < 39 && !mergeItemStack(itemstack2, 3, 30, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!mergeItemStack(itemstack2, 3, 39, false)) {
				return ItemStack.EMPTY;
			}
			if (itemstack2.getCount() == 0) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
			if (itemstack2.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(player, itemstack2);
		}
		return itemstack;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int par1, int par2) { }

}
