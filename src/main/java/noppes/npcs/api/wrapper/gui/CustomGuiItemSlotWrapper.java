package noppes.npcs.api.wrapper.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.functions.gui.GuiItemSlotUpdate;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.api.gui.IItemSlot;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.containers.ContainerCustomGui;

import java.util.Objects;

public class CustomGuiItemSlotWrapper extends CustomGuiComponentWrapper implements IItemSlot {

	protected IItemStack stack = ItemStackWrapper.AIR;
	protected int guiType = 1;
	protected EntityPlayer player;
	protected GuiItemSlotUpdate onSlotUpdate = null;

	public CustomGuiItemSlotWrapper() { }

	public CustomGuiItemSlotWrapper(int x, int y, IItemStack stack) {
		setPos(x, y);
		setSize(14, 14);
		setStack(stack);
	}

	public CustomGuiItemSlotWrapper(int x, int y, EntityPlayer playerIn) {
		player = playerIn;
		setPos(x, y);
		setSize(14, 14);
	}

	@Override
	public boolean hasStack() { return !stack.isEmpty(); }

	@Override
	public IItemStack getStack() {
		if (player != null) { stack = Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(player.inventory.getStackInSlot(getId())); }
		return stack;
	}

	@Override
	public IItemSlot setStack(IItemStack stackIn) {
		stack = stackIn == null ? ItemStackWrapper.AIR : stackIn;
		if (player != null) { player.inventory.setInventorySlotContents(getId(), stack.getMCItemStack()); }
		return this;
	}

	@Override
	public int getGuiType() { return guiType; }

	@Override
	public CustomGuiItemSlotWrapper setGuiType(int type) {
		guiType = type;
		return this;
	}

	@Override
	public Slot getMCSlot() {
		if (player != null && player.openContainer instanceof ContainerCustomGui) { return ((ContainerCustomGui) player.openContainer).getSlot(id); }
		return null;
	}

	@Override
	public int getType() { return GuiComponentType.ITEM_SLOT.get(); }

	@Override
	public NBTTagCompound toNBT(NBTTagCompound nbt) {
		super.toNBT(nbt);
		nbt.setTag("stack", stack.getMCItemStack().serializeNBT());
		nbt.setInteger("guiType", guiType);
		nbt.setBoolean("playerSlot", isPlayerSlot());
		return nbt;
	}

	@Override
	public CustomGuiComponentWrapper fromNBT(NBTTagCompound nbt) {
		super.fromNBT(nbt);
		setStack(Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(new ItemStack(nbt.getCompoundTag("stack"))));
		setGuiType(nbt.getInteger("guiType"));
		if (nbt.getBoolean("playerSlot")) { player = CustomNpcs.proxy.getPlayer(); }
		return this;
	}

	@Override
	public boolean isPlayerSlot() { return player != null; }

	@Override
	public CustomGuiItemSlotWrapper setOnUpdate(GuiItemSlotUpdate onPress) {
		onSlotUpdate = onPress;
		return this;
	}

	public final void onUpdate(ICustomGui gui) {
		if (onSlotUpdate != null) { onSlotUpdate.onUpdate(gui, this); }
	}

}
