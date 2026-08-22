package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ContainerManageRecipes extends Container {

	public ContainerManageRecipes(EntityPlayer player) {
		for(int y = 0; y < 3; ++y) {
			for(int x = 0; x < 9; ++x) {
				addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, 8 + x * 18, 113 + y * 18));
			}
		}
		for(int x = 0; x < 9; ++x) {
			addSlotToContainer(new Slot(player.inventory, x, 8 + x * 18, 171));
		}
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer entityplayer) { return true; }

	@Override
	public @Nonnull ItemStack transferStackInSlot(@Nonnull EntityPlayer player, int i) { return ItemStack.EMPTY; }

}
