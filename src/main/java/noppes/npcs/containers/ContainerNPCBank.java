package noppes.npcs.containers;

import java.util.Objects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.BankController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketBankSetPlayer;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.controllers.data.BankData;
import noppes.npcs.controllers.data.PlayerData;

import javax.annotation.Nonnull;

public class ContainerNPCBank extends Container {

	public static String editPlayerBankData;

	public final @Nonnull NpcMiscInventory items;
	public final @Nonnull BankData data;
	public final int scrollY;
	public final int ceilPos;
	public final int ceilsUpdate;
	public final int ceil;

	public ContainerNPCBank(EntityPlayer player, @Nonnull NBTTagCompound nbtBD) {
		ceil = nbtBD.getInteger("GuiCeil");
		scrollY = nbtBD.getInteger("GuiScrollY");
		ceilPos = nbtBD.getInteger("GuiCeilPos");
		ceilsUpdate = nbtBD.getInteger("GuiCeilsUpdate");
		Bank bank = BankController.getInstance().getBank(nbtBD.getInteger("id"));
		if (bank == null) { bank = new Bank(); }
		// Server
		BankData bd = new BankData(bank, "");
		if (player instanceof EntityPlayerMP) {
			EntityPlayerMP sPlayer = (EntityPlayerMP) player;
			PlayerData pd = PlayerDataController.instance.getDataFromUsername(sPlayer.getServer(), ContainerNPCBank.editPlayerBankData);
			if (pd == null) {
				ContainerNPCBank.editPlayerBankData = null;
				Packets.send(sPlayer, new PacketBankSetPlayer(""));
				pd = PlayerDataController.instance.getDataFromUsername(sPlayer.getServer(), sPlayer.getName());
			}
			if (pd != null) {
				bd = pd.bankData.get(bank.id);
				bd.addListener(sPlayer);
			}
		}
		else { bd = PlayerData.get(player).bankData.get(bank.id); }
		bd.load(nbtBD);
		data = bd;
		items = Objects.requireNonNull(data.get(ceil));
		for (int i = 0; i < items.getSizeInventory(); i++) {
			addSlotToContainer(new Slot(items, i, -5000, -5000));
		}
		// player Inventory
		int h = items.getSizeInventory() > 0 ? 95 : 0;
		for (int r = 0; r < 3; ++r) {
			for (int p = 0; p < 9; ++p) {
				addSlotToContainer(new Slot(player.inventory, p + r * 9 + 9, 9 + p * 18, 40 + r * 18 + h));
			}
		}
		for (int p = 0; p < 9; ++p) {
			addSlotToContainer(new Slot(player.inventory, p, 9 + p * 18, 98 + h));
		}
	}

	@Override
	public boolean canInteractWith(@Nonnull EntityPlayer playerIn) { return true; }

	@Override
	public @Nonnull ItemStack transferStackInSlot(@Nonnull EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (index < items.getSizeInventory()) {
				if (!mergeItemStack(itemstack1, items.getSizeInventory(), inventorySlots.size(), true)) { return ItemStack.EMPTY; }
			}
			else if (!mergeItemStack(itemstack1, 0, items.getSizeInventory(), false)) { return ItemStack.EMPTY; }
			if (itemstack1.isEmpty()) { slot.putStack(ItemStack.EMPTY); }
			else { slot.onSlotChanged(); }
		}
		return itemstack;
	}

	@Override
	public void onContainerClosed(@Nonnull EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		if (playerIn instanceof EntityPlayerMP) {
			data.removeListener((EntityPlayerMP) playerIn);
			if (!data.bank.isPublic) { data.save(); }
		}
	}

}
