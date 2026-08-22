package noppes.npcs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class NoppesUtilPlayer {

	public static boolean compareItems(ItemStack item, ItemStack item2, boolean ignoreDamage, boolean ignoreNBT) {
		return !NoppesUtilServer.isItemStackNull(item) &&
				!NoppesUtilServer.isItemStackNull(item2) &&
				compareItemDetails(item, item2, ignoreDamage, ignoreNBT);
	}

	private static boolean compareItemDetails(ItemStack item, ItemStack item2, boolean ignoreDamage, boolean ignoreNBT) {
		if (item.getItem() != item2.getItem()) { return false; }
		else if (!ignoreDamage && item.getItemDamage() != -1 && item.getItemDamage() != item2.getItemDamage()) { return false; }
		else if (!ignoreNBT && item.getTagCompound() != null && (item2.getTagCompound() == null || !item.getTagCompound().equals(item2.getTagCompound()))) { return false; }
		return ignoreNBT || item2.getTagCompound() == null || item.getTagCompound() != null;
	}

	public static boolean compareItems(EntityPlayer player, ItemStack item, boolean ignoreDamage, boolean ignoreNBT) {
		int size = 0;
		for(int i = 0; i < player.inventory.getSizeInventory(); ++i) {
			ItemStack is = player.inventory.getStackInSlot(i);
			if (!NoppesUtilServer.isItemStackNull(is) && compareItems(item, is, ignoreDamage, ignoreNBT)) {
				size += is.getCount();
			}
		}
		return size >= item.getCount();
	}

	// New from Unofficial (BetaZavr)
	public static boolean compareItems(EntityPlayer player, ItemStack item, boolean ignoreDamage, boolean ignoreNBT, int amount) {
		int size = 0;
		if (player == null) { return false; }
		for(int i = 0; i < player.inventory.getSizeInventory(); ++i) {
			ItemStack is = player.inventory.getStackInSlot(i);
			if (!NoppesUtilServer.isItemStackNull(is) && compareItems(item, is, ignoreDamage, ignoreNBT)) { size += is.getCount(); }
		}
		return size >= amount;
	}

}
