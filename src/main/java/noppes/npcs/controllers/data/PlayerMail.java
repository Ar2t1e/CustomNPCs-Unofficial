package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.chat.Component;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.data.IPlayerMail;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;

// Changed by Unofficial (BetaZavr)
public class PlayerMail implements IInventory, IPlayerMail {

	public final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
	public NBTTagCompound message = new NBTTagCompound();
	public long timeWhenReceived = System.currentTimeMillis();
	public long timeWillCome = 0L;
	public int questId = -1;
	public int ransom = 0;
	public int money = 0;
	public boolean beenRead = false;
	public boolean returned = false;
	public String sender = "";
	public String title = "";

	@Override
	public void clear() {
		title = "";
		sender = "";
		beenRead = false;
		returned = false;
		questId = -1;
		items.clear();
		money = 0;
		ransom = 0;
		for (String key : message.getKeySet()) { message.removeTag(key); }
		timeWhenReceived = System.currentTimeMillis();
	}

	public void load(NBTTagCompound compound) {
		title = compound.getString("Subject");
		sender = compound.getString("Sender");
		message = compound.getCompoundTag("Message");
		beenRead = compound.getBoolean("BeenRead");
		returned = compound.getBoolean("Returned");
		timeWillCome = compound.getLong("TimeWillCome");
		timeWhenReceived = compound.getLong("TimeWhenReceived");
		if (compound.hasKey("MailQuest")) { questId = compound.getInteger("MailQuest"); }
		items.clear();
		NBTTagList list = compound.getTagList("MailItems", 10);
		for (int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound nbt = list.getCompoundTagAt(i);
			int j = nbt.getByte("Slot") & 0xFF;
			if (j < items.size()) { items.set(j, new ItemStack(nbt)); }
		}
		money = compound.getInteger("Money");
		ransom = compound.getInteger("Ransom");
	}

	public NBTTagCompound save() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString("Subject", title);
		compound.setString("Sender", sender);
		compound.setTag("Message", message);
		compound.setBoolean("BeenRead", beenRead);
		compound.setBoolean("Returned", returned);
		compound.setLong("TimeWillCome", timeWillCome);
		compound.setLong("TimeWhenReceived", timeWhenReceived);
		compound.setInteger("MailQuest", questId);
		if (hasQuest()) { compound.setString("MailQuestTitle", getQuest().getTitle().getFormattedText()); }
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < items.size(); ++i) {
			if (!(items.get(i)).isEmpty()) {
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setByte("Slot", (byte) i);
				(items.get(i)).writeToNBT(nbt);
				list.appendTag(nbt);
			}
		}
		compound.setTag("MailItems", list);
		compound.setInteger("Money", money);
		compound.setInteger("Ransom", ransom);
		return compound;
	}

	public boolean isValid() { return !title.isEmpty() && !message.getKeySet().isEmpty() && !sender.isEmpty(); }

	public boolean hasQuest() { return getQuest() != null; }

	@Override
	public Quest getQuest() { return QuestController.instance != null ? QuestController.instance.quests.get(questId) : null; }

	@Override
	public void setQuest(int id) {
		if (id < 0) { throw new CustomNPCsException("Quest id is lower than 0"); }
		questId = id;
	}

	@Override
	public int getSizeInventory() { return items.size(); }

	@Override
	public @Nonnull ItemStack getStackInSlot(int slotId) { return items.get(slotId); }

	@Override
	public @Nonnull ItemStack decrStackSize(int slotId, int count) {
		ItemStack itemstack = ItemStackHelper.getAndSplit(items, slotId, count);
		if (!itemstack.isEmpty()) { markDirty(); }
		return itemstack;
	}

	@Override
	public @Nonnull ItemStack removeStackFromSlot(int slotId) { return items.set(slotId, ItemStack.EMPTY); }

	@Override
	public void setInventorySlotContents(int slotId, @Nonnull ItemStack stack) {
		items.set(slotId, stack);
		if (stack.getCount() > getInventoryStackLimit()) { stack.setCount(getInventoryStackLimit()); }
		markDirty();
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean isUsableByPlayer(@Nonnull EntityPlayer player) { return true; }

	@Override
	public void openInventory(@Nonnull EntityPlayer player) {}

	@Override
	public void closeInventory(@Nonnull EntityPlayer player) { }

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack item) { return true; }

	@Override
	public boolean isEmpty() {
		for (int slot = 0; slot < getSizeInventory(); ++slot) {
			if (!NoppesUtilServer.isItemStackNull(getStackInSlot(slot))) { return false; }
		}
		return true;
	}

	@Override
	public String getSender() { return sender; }

	@Override
	public void setSender(String senderIn) { sender = senderIn == null ? "" : senderIn; }

	@Override
	public String getSubject() { return title; }

	@Override
	public void setSubject(String titleIn) { title = titleIn == null ? "" : titleIn; }

	@Override
	public List<String> getText() {
		List<String> list = new ArrayList<>();
		NBTTagList pages = message.getTagList("pages", 8);
		for (int i = 0; i < pages.tagCount(); ++i) { list.add(pages.getStringTagAt(i)); }
		return list;
	}

	@Override
	public void setText(String ... pages) {
		NBTTagList list = new NBTTagList();
		if (pages != null) {
			for (String page : pages) { list.appendTag(new NBTTagString(page)); }
		}
		message.setTag("pages", list);
	}

	@Override
	public IContainer getContainer() { return Objects.requireNonNull(NpcAPI.Instance()).getIContainer(this); }

	@Override
	public int getMoney() { return money; }

	@Override
	public void setMoney(int moneyIn) { money = ValueUtil.correctInt(moneyIn, 0, Integer.MAX_VALUE); }

	@Override
	public int getRansom() { return ransom; }

	@Override
	public void setRansom(int moneyIn) { ransom = ValueUtil.correctInt(moneyIn, 0, Integer.MAX_VALUE); }

	public boolean isReturned() { return returned; }

	public PlayerMail copy() {
		PlayerMail mail = new PlayerMail();
		mail.load(save());
		return mail;
	}

	@Override
	public @Nonnull ITextComponent getDisplayName() { return Component.literal(getName()).getParent(); }

	@Override
	public int getField(int id) { return 0; }

	@Override
	public int getFieldCount() { return 0; }

	@Override
	public int getInventoryStackLimit() { return 64; }

	@Override
	public @Nonnull String getName() { return "Mail Inventory"; }

	@Override
	public boolean hasCustomName() { return false; }

	@Override
	public void setField(int id, int value) {}

}
