package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.entity.data.role.IJobItemGiver;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.containers.NpcMiscInventory;
import noppes.npcs.controllers.GlobalDataController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerItemGiverData;
import noppes.npcs.entity.EntityNPCInterface;

public class JobItemGiver extends JobInterface implements IJobItemGiver {

	protected final List<EntityPlayer> recentlyChecked = new ArrayList<>();
	protected List<EntityPlayer> toCheck;
	protected int ticks = 10;

	public List<String> lines = new ArrayList<>();
	public Availability availability = new Availability();
	public NpcMiscInventory inventory = new NpcMiscInventory(9);
	public int cooldownType = 0; // 0:timer, 1:one, 2:rldaily
	public int givingMethod = 0; // 0:rnd, 1:all, 2:owned, 3:doesn't own, 4:chained
	public int cooldown = 10;
	public int itemGiverId = 0;

	public JobItemGiver(EntityNPCInterface npc) {
        super(npc);
        lines.add("Have these items {player}");
		type = JobType.ITEM_GIVER;
	}

	@Override
	public NBTTagCompound save(NBTTagCompound compound) {
		super.save(compound);
		compound.setInteger("igCooldownType", cooldownType);
		compound.setInteger("igGivingMethod", givingMethod);
		compound.setInteger("igCooldown", cooldown);
		compound.setInteger("ItemGiverId", itemGiverId);
		compound.setTag("igLines", NBTTags.nbtStringList(lines));
		compound.setTag("igJobInventory", inventory.save());
		compound.setTag("igAvailability", availability.save(new NBTTagCompound()));
		return compound;
	}

	@Override
	public void load(NBTTagCompound compound) {
		super.load(compound);
		type = JobType.ITEM_GIVER;
		itemGiverId = compound.getInteger("ItemGiverId");
		cooldownType = compound.getInteger("igCooldownType");
		givingMethod = compound.getInteger("igGivingMethod");
		cooldown = compound.getInteger("igCooldown");
		lines = NBTTags.getStringList(compound.getTagList("igLines", 10));
		inventory.load(compound.getCompoundTag("igJobInventory"));
		if (itemGiverId == 0 && GlobalDataController.instance != null) {
			itemGiverId = GlobalDataController.instance.incrementItemGiverId();
		}
		availability.load(compound.getCompoundTag("igAvailability"));
	}

	private boolean giveItems(EntityPlayer player) {
		PlayerItemGiverData data = PlayerData.get(player).itemgiverData;
		if (!canPlayerInteract(data)) { return false; }
		Vector<ItemStack> items = new Vector<>();
		Vector<ItemStack> toGive = new Vector<>();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (!stack.isEmpty()) { items.add(stack.copy()); }
		}
		if (!items.isEmpty()) {
			if (isAllGiver()) { toGive = items; }
			else if (isRemainingGiver()) {
				for (ItemStack is : items) {
					if (!playerHasItem(player, is.getItem())) { toGive.add(is); }
				}
			}
			else if (isRandomGiver()) {
				int index = npc != null ? npc.world.rand.nextInt(items.size()) : new Random().nextInt(items.size());
				toGive.add((items.get(index)).copy());
			}
			else if (isGiverWhenNotOwnedAny()) {
				boolean ownsItems = false;
				for (ItemStack is2 : items) {
					if (playerHasItem(player, is2.getItem())) {
						ownsItems = true;
						break;
					}
				}
				if (ownsItems) {
					return false;
				}
				toGive = items;
			}
			else if (isChainedGiver()) {
				int itemIndex = data.getItemIndex(this);
				if (itemIndex > 0 && itemIndex < inventory.getSizeInventory()) { toGive.add(inventory.getStackInSlot(itemIndex)); }
			}
			if (toGive.isEmpty()) { return false; }
			if (givePlayerItems(player, toGive)) {
				if (npc != null && !lines.isEmpty()) { npc.say(player, new Line(lines.get(npc.getRNG().nextInt(lines.size())))); }
				if (isDaily()) { data.setTime(this, getDay()); }
				else { data.setTime(this, System.currentTimeMillis()); }
				if (isChainedGiver()) {
					data.setItemIndex(this, (data.getItemIndex(this) + 1) % inventory.getSizeInventory());
				}
				return true;
			}
		}
		return false;
	}

	private long getDay() { return npc == null ? 0 : (npc.world.getTotalWorldTime() / 24000L); }

	private boolean canPlayerInteract(PlayerItemGiverData data) {
		if (inventory.getSizeInventory() == 0) { return false; }
		if (isOnTimer()) {
			return data.notInteractedBefore(this) || data.getTime(this) + cooldown * 1000L < System.currentTimeMillis();
		}
		if (isGiveOnce()) { return data.notInteractedBefore(this); }
		if (isDaily()) {
			return data.notInteractedBefore(this) || getDay() > data.getTime(this);
		}
		return false;
	}

	private boolean givePlayerItems(EntityPlayer player, Vector<ItemStack> toGive) {
		if (toGive.isEmpty() || freeInventorySlots(player) < toGive.size()) { return false; }
		if (npc != null) {
			for (ItemStack is : toGive) { npc.givePlayerItem(player, is); }
		}
		return true;
	}

	private boolean playerHasItem(EntityPlayer player, Item item) {
		for (ItemStack is : player.inventory.mainInventory) {
			if (!is.isEmpty() && is.getItem() == item) { return true; }
		}
		for (ItemStack is : player.inventory.armorInventory) {
			if (!is.isEmpty() && is.getItem() == item) { return true; }
		}
		return false;
	}

	private int freeInventorySlots(EntityPlayer player) {
		int i = 0;
		for (ItemStack is : player.inventory.mainInventory) {
			if (NoppesUtilServer.isItemStackNull(is)) { ++i; }
		}
		return i;
	}

	private boolean isRandomGiver() { return givingMethod == 0; }

	private boolean isAllGiver() { return givingMethod == 1; }

	private boolean isRemainingGiver() {return givingMethod == 2; }

	private boolean isGiverWhenNotOwnedAny() { return givingMethod == 3; }

	private boolean isChainedGiver() { return givingMethod == 4; }

	public boolean isOnTimer() { return cooldownType == 0; }

	private boolean isGiveOnce() { return cooldownType == 1; }

	private boolean isDaily() { return cooldownType == 2; }

	@Override
	public boolean aiShouldExecute() {
		if (npc == null || npc.isAttacking()) { return false; }
		--ticks;
		if (ticks > 0) { return false; }
		ticks = 10;
		List<EntityPlayer> list = new ArrayList<>();
		try {
			list = npc.world.getEntitiesWithinAABB(EntityPlayer.class, npc.getEntityBoundingBox().grow(3.0, 3.0, 3.0));
		}
		catch (Exception ignored) { }
		(toCheck = list).removeAll(recentlyChecked);
		List<EntityPlayer> listMax = new ArrayList<>();
		try {
			listMax = npc.world.getEntitiesWithinAABB(EntityPlayer.class,
					npc.getEntityBoundingBox().grow(10.0, 10.0, 10.0));
		}
		catch (Exception ignored) { }
		recentlyChecked.retainAll(listMax);
		recentlyChecked.addAll(toCheck);
		return !toCheck.isEmpty();
	}

	@Override
	public boolean aiContinueExecute() { return false; }

	@Override
	public void aiStartExecuting() {
		if (npc != null) {
			for (EntityPlayer player : toCheck) {
				if (npc.canSee(player) && availability.isAvailable(player)) {
					recentlyChecked.add(player);
					interact(player);
				}
			}
		}
	}

	@Override
	public void interact(EntityPlayer player) {
		if (npc != null && !giveItems(player)) { npc.say(player, npc.advanced.getInteractLine()); }
	}

	// New from Unofficial (BetaZavr)
	@Override
	public IItemStack[] getItemStacks() {
		IItemStack[] items = new IItemStack[inventory.getSizeInventory()];
		NpcAPI api = NpcAPI.Instance();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (api != null) { items[i] = api.getIItemStack(inventory.getStackInSlot(i)); }
			else { items[i] = ItemStackWrapper.AIR; }
		}
		return items;
	}

	@Override
	public void setItemStacks(IItemStack[] stacks) {
		inventory.clear();
		if (stacks == null) { return; }
		for (int i = 0; i < inventory.getSizeInventory() && i < stacks.length; i++) {
			inventory.setInventorySlotContents(i, stacks[i].getMCItemStack());
		}
	}

	@Override
	public String[] getLines() {
		String[] ls = new String[3];
		for (int i = 0; i < 3; i++) {
			if (lines.get(i) != null) { ls[i] = lines.get(i); }
			else { ls[i] = ""; }
		}
		return ls;
	}

	@Override
	public void setLines(String[] linesIn) {
		lines.clear();
		if (linesIn == null) { return; }
		for (int i = 0; i < 3; i++) {
			if (i < linesIn.length) { lines.add(linesIn[i]); }
			else { lines.add(""); }
		}
	}

	@Override
	public int getCooldownType() { return cooldownType; }

	@Override
	public void setCooldownType(int type) {
		if (type < 0 || type > 2) {
			throw new CustomNPCsException("Cooldown type must be between 0 and 2");
		}
		cooldownType = type;
	}

	@Override
	public int getGivingType() { return givingMethod; }

	@Override
	public void setGivingType(int type) {
		if (type < 0 || type > 4) {
			throw new CustomNPCsException("Giving type must be between 0 and 4");
		}
		givingMethod = type;
	}

}
