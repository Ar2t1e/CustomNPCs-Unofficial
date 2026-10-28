package noppes.npcs.containers.slots;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.roles.RoleCompanion;

import javax.annotation.Nonnull;

public class SlotCompanionArmor extends Slot {

	EntityEquipmentSlot armorType;
	RoleCompanion role;

	public SlotCompanionArmor(RoleCompanion roleIn, IInventory iinventory, int id, int x, int y,
			EntityEquipmentSlot type) {
		super(iinventory, id, x, y);
		armorType = type;
		role = roleIn;
	}

	@Override
	public int getSlotStackLimit() { return 1; }

	@Override
	@SideOnly(Side.CLIENT)
	public String getSlotTexture() {
		return ItemArmor.EMPTY_SLOT_NAMES[armorType.getIndex()];
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack itemstack) {
		if (itemstack.getItem() instanceof ItemArmor && role.canWearArmor(itemstack)) {
			return ((ItemArmor) itemstack.getItem()).armorType == armorType;
		}
		return itemstack.getItem() instanceof ItemBlock && armorType == EntityEquipmentSlot.HEAD;
	}
}
