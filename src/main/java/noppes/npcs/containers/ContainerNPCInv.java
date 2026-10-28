package noppes.npcs.containers;

import moe.plushie.armourers_workshop.api.ArmourersWorkshopApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import noppes.npcs.containers.slots.*;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataInventory;

import javax.annotation.Nonnull;

public class ContainerNPCInv extends Container {

	public ContainerNPCInv(EntityNPCInterface npc, EntityPlayer player) {
		DataInventory inv = npc != null ? npc.inventory : new DataInventory(null);
		addSlotToContainer(new SlotNPCArmor(inv, 0, 9, 22, EntityEquipmentSlot.HEAD));
		addSlotToContainer(new SlotNPCArmor(inv, 1, 9, 40, EntityEquipmentSlot.CHEST));
		addSlotToContainer(new SlotNPCArmor(inv, 2, 9, 58, EntityEquipmentSlot.LEGS));
		addSlotToContainer(new SlotNPCArmor(inv, 3, 9, 76, EntityEquipmentSlot.FEET));
		addSlotToContainer(new SlotNPCHand(inv, 4, 81, 22, "weapon"));
		addSlotToContainer(new SlotNPCHand(inv, 5, 81, 40, "arrow"));
		addSlotToContainer(new SlotNPCHand(inv, 6, 81, 58, "shield"));
		// AW mod
		if (ArmourersWorkshopApi.isAvailable()) {
			addSlotToContainer(new AWSlotOutfit(inv, 7, 27, 4));
			addSlotToContainer(new AWSlotWings(inv, 8, 45, 4));
		}
		// player inventory
		for(int x = 0; x < 3; ++x) {
			for(int y = 0; y < 9; ++y) { addSlotToContainer(new Slot(player.inventory, y + x * 9 + 9, y * 18 + 8, 113 + x * 18)); }
		}
		// player hotbar
		for(int x = 0; x < 9; ++x) { addSlotToContainer(new Slot(player.inventory, x, x * 18 + 8, 171)); }
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer entityplayer) { return true; }

	@Override
	public @Nonnull ItemStack slotClick(int slotId, int dragType, @Nonnull ClickType clickTypeIn, @Nonnull EntityPlayer player) {
		if (clickTypeIn == ClickType.QUICK_MOVE && dragType == 0) { // shift + LMB
			if (slotId < 0) { return ItemStack.EMPTY; }
			Slot slot = inventorySlots.get(slotId);
			if (slot == null || !slot.canTakeStack(player) || !slot.getHasStack()) { return ItemStack.EMPTY; }
			ItemStack stackInClickSlot = slot.getStack();
			if (slotId < 7) { // take off your equipment
				if (player.inventory.addItemStackToInventory(stackInClickSlot)) { slot.putStack(ItemStack.EMPTY); }
			}
			else if (stackInClickSlot.getItem() instanceof ItemArmor) {
				Slot armSlot;
				switch (((ItemArmor) stackInClickSlot.getItem()).armorType) {
					case HEAD:
						armSlot = inventorySlots.get(0);
						break;
					case CHEST:
						armSlot = inventorySlots.get(1);
						break;
					case LEGS:
						armSlot = inventorySlots.get(2);
						break;
					case FEET:
						armSlot = inventorySlots.get(3);
						break;
					default:
						armSlot = null;
						break;
				}
				if (armSlot != null) {
					ItemStack stackInArmSlot = armSlot.getStack().copy();
					armSlot.putStack(stackInClickSlot);
					slot.putStack(stackInArmSlot);
				}
			}
			else if (stackInClickSlot.getItem() instanceof ItemSword || stackInClickSlot.getItem() instanceof ItemBow) {
				Slot wpnSlot = inventorySlots.get(4);
				if (wpnSlot != null) {
					ItemStack stackInWpnSlot = wpnSlot.getStack().copy();
					wpnSlot.putStack(stackInClickSlot);
					slot.putStack(stackInWpnSlot);
				}
			}
			else if (stackInClickSlot.getItem() instanceof ItemShield) {
				Slot sldSlot = inventorySlots.get(6);
				if (sldSlot != null) {
					ItemStack stackInSldSlot = sldSlot.getStack().copy();
					sldSlot.putStack(stackInClickSlot);
					slot.putStack(stackInSldSlot);
				}
			}
			else {
				Slot pjcSlot = inventorySlots.get(5);
				if (pjcSlot != null) {
					ItemStack stackInPjcSlot = pjcSlot.getStack().copy();
					pjcSlot.putStack(stackInClickSlot);
					slot.putStack(stackInPjcSlot);
				}
			} // any projective
			return ItemStack.EMPTY;
		}
		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

	@Override
	public @Nonnull ItemStack transferStackInSlot(@Nonnull EntityPlayer playerIn, int index) { return ItemStack.EMPTY; }

}
