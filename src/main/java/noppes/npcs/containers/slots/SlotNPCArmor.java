package noppes.npcs.containers.slots;

import moe.plushie.armourers_workshop.api.ArmourersWorkshopApi;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class SlotNPCArmor extends Slot {

	EntityEquipmentSlot armorType;

	public SlotNPCArmor(IInventory inventoryIn, int index, int xPosition, int yPosition, EntityEquipmentSlot slot) {
		super(inventoryIn, index, xPosition, yPosition);
		armorType = slot;
	}

	@Override
	public int getSlotStackLimit() { return 1; }

	@Override
	@SideOnly(Side.CLIENT)
	public String getSlotTexture() { return getHasStack() ? null : ItemArmor.EMPTY_SLOT_NAMES[armorType.getIndex()]; }

	@Override
	public boolean isItemValid(@Nonnull ItemStack itemstack) {
		if (itemstack.getItem() instanceof ItemElytra) { return armorType == EntityEquipmentSlot.CHEST; }
		if (itemstack.getItem() instanceof ItemArmor) { return ((ItemArmor) itemstack.getItem()).armorType == armorType; }
		if (itemstack.getItem() instanceof ItemBlock ||
				itemstack.getItem() instanceof ItemSword ||
				itemstack.getItem() instanceof ItemBow) { return armorType == EntityEquipmentSlot.HEAD; }
		if (ArmourersWorkshopApi.isAvailable()) {
			ISkinDescriptor skinDescriptor = ArmourersWorkshopApi.getSkinNBTUtils().getSkinDescriptor(itemstack);
			if (skinDescriptor != null) {
				return skinDescriptor.getIdentifier().getSkinType().getName().equalsIgnoreCase(armorType.getName());
			}
		}
		return false;
	}
}
