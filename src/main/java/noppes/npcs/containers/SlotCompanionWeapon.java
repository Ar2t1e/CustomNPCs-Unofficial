package noppes.npcs.containers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.roles.RoleCompanion;

import javax.annotation.Nonnull;
import java.util.Objects;

class SlotCompanionWeapon extends Slot {

	final RoleCompanion role;

	public SlotCompanionWeapon(RoleCompanion roleIn, IInventory iinventory, int id, int x, int y) {
		super(iinventory, id, x, y);
		role = roleIn;
	}

	@Override
	public int getSlotStackLimit() { return 1; }

	@Override
	public boolean isItemValid(@Nonnull ItemStack itemstack) {
		return !NoppesUtilServer.isItemStackNull(itemstack) &&
				role.isWeapon(itemstack) &&
				role.canWearWeapon(Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(itemstack));
	}

}
