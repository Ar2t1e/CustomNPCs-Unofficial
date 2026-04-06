package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import noppes.npcs.EventHooks;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.gui.IItemSlot;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.api.wrapper.gui.GuiComponentsScrollableWrapper;
import noppes.npcs.util.CustomNPCsScheduler;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Objects;

public class ContainerCustomGui extends Container {

	public CustomGuiWrapper customGui;
	public CustomGuiWrapper activeGui;
	public InventoryBasic guiInventory;
	public NBTTagCompound data;

	public ContainerCustomGui(NBTTagCompound dataIn) {
		data = dataIn;
		guiInventory = new InventoryBasic(Component.empty(), 0);
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer playerIn) { return true; }

	@Override
	public @Nonnull ItemStack slotClick(int slotId, int dragType, @Nonnull ClickType clickTypeIn, @Nonnull EntityPlayer player) {
		if (slotId < 0) {
			return super.slotClick(slotId, dragType, clickTypeIn, player);
		}
		if (!player.world.isRemote) {
			IItemStack heldItem = Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(player.inventory.getItemStack());
			if (!EventHooks.onCustomGuiSlotClicked((PlayerWrapper<?>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(player),
					((ContainerCustomGui) player.openContainer).customGui, slotId, dragType, clickTypeIn.toString(),
					heldItem, this.inventorySlots.get(slotId))) {
				ItemStack item = super.slotClick(slotId, dragType, clickTypeIn, player);
				EntityPlayerMP p = (EntityPlayerMP) player;
				CustomNPCsScheduler.runTack(() -> p.sendContainerToPlayer(this), 10);
				return item;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public @Nonnull ItemStack transferStackInSlot(@Nonnull EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack2 = slot.getStack();
			itemstack = itemstack2.copy();
			if (index < this.guiInventory.getSizeInventory()) {
				if (!this.mergeItemStack(itemstack2, this.guiInventory.getSizeInventory(), this.inventorySlots.size(),
						true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack2, 0, this.guiInventory.getSizeInventory(), false)) {
				return ItemStack.EMPTY;
			}
			if (itemstack2.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}
		return itemstack;
	}

	@Override
	public void onContainerClosed(@Nonnull EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		if (!playerIn.world.isRemote) {
			EventHooks.onCustomGuiClose((PlayerWrapper<?>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(playerIn), customGui);
		}
	}

	public void setGui(CustomGuiWrapper gui, EntityPlayer player) {
		activeGui = gui.getActiveGui();
		guiInventory = new InventoryBasic(Component.empty(), activeGui.getSlots().size() + activeGui.getScrollingPanel().getSlots().size());
		customGui = gui;
		inventorySlots.clear();
		for (IItemSlot slot : activeGui.getSlots()) {
			Slot s = addSlotToContainer(new SlotCustomGui(gui, guiInventory, slot.getId(), slot, player));
			guiInventory.setInventorySlotContents(s.slotNumber, slot.getStack().getMCItemStack());
		}
		GuiComponentsScrollableWrapper panel = activeGui.getScrollingPanel();
		Iterator<IItemSlot> var9 = panel.getSlots().iterator();
		IItemSlot slot;
		while(var9.hasNext()) {
			slot = var9.next();
			Slot s = addSlotToContainer((new SlotCustomGui(gui, guiInventory, slot.getId(), slot, player)).update(panel.x, panel.y));
			guiInventory.setInventorySlotContents(s.slotNumber, slot.getStack().getMCItemStack());
		}
		var9 = activeGui.getPlayerSlots().iterator();
		while(var9.hasNext()) {
			slot = var9.next();
			addSlotToContainer(new SlotCustomGui(gui, player.inventory, slot.getId(), slot, player));
		}
		update();
	}

	public void update() {
		GuiComponentsScrollableWrapper panel = activeGui.getScrollingPanel();
		for(int i = 0; i < activeGui.getScrollingPanel().getSlots().size(); ++i) {
			SlotCustomGui slot = (SlotCustomGui) getSlot(i + activeGui.getSlots().size());
			if (panel.isVisible(slot.slot)) { slot.update(panel.x, panel.y - panel.scrollAmount); }
			else { slot.update(-1073741824, -1073741824); }
		}
	}

}
