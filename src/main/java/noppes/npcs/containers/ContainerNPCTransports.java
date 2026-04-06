package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.TransportCategory;
import noppes.npcs.controllers.data.TransportLocation;

import javax.annotation.Nonnull;

public class ContainerNPCTransports extends Container {

	protected final IInventory inv;
	public TransportLocation location;

	public ContainerNPCTransports(EntityPlayer player, BlockPos pos) {
		inv = player.inventory;
		TransportLocation loc = TransportController.getInstance().getTransport(pos.getX());
		if (loc.id < 0) {
			loc = new TransportLocation();
			loc.id = pos.getX();
			loc.category = TransportController.getInstance().getCategory(loc, pos.getY());
		}
		if (player.world.isRemote) { loc = loc.copy(); }
		location = loc;
		resetStacks();
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer entityplayer) { return true; }

	@Override
	public @Nonnull ItemStack transferStackInSlot(@Nonnull EntityPlayer player, int i) { return ItemStack.EMPTY; }

	public NBTTagCompound saveTransport(TransportCategory category) {
		NBTTagCompound compound = new NBTTagCompound();
		if (category == null) {
			return compound;
		}
		for (int i = 0; i < 9; i++) {
			location.inventory.setInventorySlotContents(i, getSlot(i).getStack());
		}
		category.locations.put(location.id, location);
		category.save(compound);
		return compound;
	}

	public void resetStacks() {
		inventorySlots.clear();
		inventoryItemStacks.clear();
		for (int v = 0; v < 3; ++v) {
			for (int u = 0; u < 3; ++u) {
				addSlotToContainer(new Slot(location.inventory, u + v * 3,
						(location.id < 0 ? -5000 : 0) + 215 + u * 18, (location.id < 0 ? -5000 : 0) + 20 + v * 18));
			}
		}
		// player inventory
		for(int x = 0; x < 3; ++x) {
			for(int y = 0; y < 9; ++y) { addSlotToContainer(new Slot(inv, y + x * 9 + 9, y * 18 + 8, 113 + x * 18)); }
		}
		// player hotbar
		for(int x = 0; x < 9; ++x) { addSlotToContainer(new Slot(inv, x, x * 18 + 8, 171)); }
	}

}
