package noppes.npcs.containers.slots;

import moe.plushie.armourers_workshop.api.ArmourersWorkshopApi;
import moe.plushie.armourers_workshop.api.ArmourersWorkshopClientApi;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class AWSlotWings extends Slot {

	public AWSlotWings(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	public int getSlotStackLimit() {
		return 1;
	}

	public boolean isItemValid(@Nonnull ItemStack itemstack) {
		if (ArmourersWorkshopApi.isAvailable()) {
			ISkinDescriptor skinDescriptor = ArmourersWorkshopApi.getSkinNBTUtils().getSkinDescriptor(itemstack);
			if (skinDescriptor != null) {
				return skinDescriptor.getIdentifier().getSkinType().getName().equalsIgnoreCase("wings");
			}
		}
		return false;
	}

}
