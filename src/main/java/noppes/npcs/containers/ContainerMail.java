package noppes.npcs.containers;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.PlayerMailData;

import javax.annotation.Nonnull;

public class ContainerMail extends ContainerNpcInterface {

	public static PlayerMail staticMail = new PlayerMail();
	public final boolean canEdit;
	public final boolean canSend;
	public boolean sendMail = false;
	public PlayerMail mail;

	public ContainerMail(EntityPlayer player, boolean canEditIn, boolean canSendIn) {
		super(player);
		mail = ContainerMail.staticMail;
		ContainerMail.staticMail = new PlayerMail();
		canEdit = canEditIn;
		canSend = canSendIn;
		player.inventory.openInventory(player);
		for (int k = 0; k < 4; ++k) {
			addSlotToContainer(new SlotValid(mail, k, 199 + (k % 2) * 18, 190 + (k / 2) * 18, canEdit));
		}
		for (int j = 0; j < 3; ++j) {
			for (int k = 0; k < 9; ++k) {
				addSlotToContainer(new Slot(player.inventory, k + j * 9 + 9, 7 + k * 18, 168 + j * 18));
			}
		}
		for (int j = 0; j < 9; ++j) {
			addSlotToContainer(new Slot(player.inventory, j, 7 + j * 18, 223));
		}
	}

	public void onContainerClosed(@Nonnull EntityPlayer player) {
		super.onContainerClosed(player);
		if (player.world.isRemote) { return; }
		if (!canEdit) {
			PlayerMailData data = CustomNpcs.proxy.getPlayerData(player).mailData;
			for (PlayerMail m : data.playerMails) {
				if (m.timeWhenReceived == mail.timeWhenReceived && m.sender.equals(mail.sender)) {
					m.load(mail.save());
					break;
				}
			}
		}
		else if (!sendMail) {
			for (int i = 0; i < 4; i++) {
				Slot slot = getSlot(i);
				if (!slot.getHasStack()) { continue;}
				EntityItem entityitem = new EntityItem(player.world, player.posX, player.posY + 0.16f, player.posZ, slot.getStack());
				entityitem.setPickupDelay(1);
				entityitem.setOwner(player.getName());
				player.world.spawnEntity(entityitem);
			}
		}
	}

	@Override
	public @Nonnull ItemStack slotClick(int slotId, int dragType, @Nonnull ClickType clickTypeIn, @Nonnull EntityPlayer player) {
		if (!canEdit && !canSend && slotId > -1 && slotId < 4) {
			Slot slot = inventorySlots.get(slotId);
			if (slot != null && slot.getHasStack()) {
				return super.slotClick(slotId, 0, ClickType.QUICK_MOVE, player); // take
			}
		}
		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

	@Override
	public @Nonnull ItemStack transferStackInSlot(@Nonnull EntityPlayer player, int slotId) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(slotId);
		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();
			if (slotId < 4) {
				if (!mergeItemStack(slotStack, 4, inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!canEdit || !mergeItemStack(slotStack, 0, 4, false)) {
				return ItemStack.EMPTY;
			}
			if (slotStack.getCount() == 0) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}
		return stack;
	}

}
