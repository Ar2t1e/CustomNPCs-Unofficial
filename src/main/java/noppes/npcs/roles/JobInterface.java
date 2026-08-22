package noppes.npcs.roles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.entity.data.INPCJob;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.entity.EntityNPCInterface;

import javax.annotation.Nullable;

public class JobInterface implements INPCJob {

	public static final JobInterface NONE = new JobInterface(null) {

		@Override
		public NBTTagCompound save(NBTTagCompound compound) { return compound; }

		@Override
		public void load(NBTTagCompound compound) { }

		@Override
		public int getType() { return JobType.NONE.get(); }

		@Override
		public JobType getEnumType() { return JobType.NONE; }

	};

	public @Nullable EntityNPCInterface npc;
	public boolean overrideMainHand = false;
	public boolean overrideOffHand = false;
	public JobType type = JobType.NONE;

	public JobInterface(@Nullable EntityNPCInterface npcIn) { npc = npcIn; }

	public boolean aiContinueExecute() { return aiShouldExecute(); }

	public void aiDeathExecute(Entity attackingEntity) { }

	public boolean aiShouldExecute() { return false; }

	public void aiStartExecuting() { }

	public void aiUpdateTask() { }

	public void delete() { }

	public JobType getEnumType() { return this.type; }

	public IItemStack getMainhand() { return null; }

	public int getMutexBits() { return 0; }

	public IItemStack getOffhand() { return null; }

	@Override
	public int getType() { return type.get(); }

	@Override
	public boolean isWorking() { return false; }

	public boolean isFollowing() { return false; }

	public String itemToString(ItemStack item) {
		if (item == null || item.isEmpty()) { return ""; }
		return Item.REGISTRY.getNameForObject(item.getItem()) + " - " + item.getItemDamage();
	}

	public void killed() { }

	public void load(NBTTagCompound compound) {
		this.type = JobType.get(compound.getInteger("Type"));
	}

	public void reset() { }

	public void stop() { }

	public ItemStack stringToItem(String s) {
		if (s.isEmpty()) { return ItemStack.EMPTY; }
		int damage = 0;
		if (s.contains(" - ")) {
			String[] split = s.split(" - ");
			if (split.length == 2) {
				try { damage = Integer.parseInt(split[1]); }
				catch (Exception e) { LogWriter.error(e); }
				s = split[0];
			}
		}
		Item item = Item.getByNameOrId(s);
		if (item == null) { return ItemStack.EMPTY; }
		return new ItemStack(item, 1, damage);
	}

	public NBTTagCompound save(NBTTagCompound compound) {
		compound.setInteger("Type", this.type.get());
		return compound;
	}

	public void interact(EntityPlayer player) {}

}
