package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleFollower;

import javax.annotation.Nonnull;

public class ContainerNPCFollowerHire extends ContainerNpcInterface {

	public final RoleFollower role;

	public ContainerNPCFollowerHire(EntityPlayer player, int entityId, BlockPos data) {
		super(player);
		EntityNPCInterface npc = (EntityNPCInterface) player.world.getEntityByID(entityId);
		if (npc != null) { role = (RoleFollower) npc.role; }
		else { role = new RoleFollower(null); }

		int offSet = data.getX() == 0 ? 0 : 58;
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18 + offSet));
			}
		}
		for (int x = 0; x < 9; ++x) {
			addSlotToContainer(new Slot(player.inventory, x, 8 + x * 18, 142 + offSet));
		}
	}

	@Override
	public void onContainerClosed(@Nonnull EntityPlayer entityplayer) { super.onContainerClosed(entityplayer); }

	@Override
	public @Nonnull ItemStack transferStackInSlot(@Nonnull EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (index < this.role.rentalItems.getSizeInventory()) {
				if (!this.mergeItemStack(itemstack1, this.role.rentalItems.getSizeInventory(), this.inventorySlots.size(),
						true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, this.role.rentalItems.getSizeInventory(), false)) {
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
