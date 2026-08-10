package noppes.npcs.roles.data;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.Component;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.data.role.IJobSpawner;
import noppes.npcs.api.wrapper.NBTWrapper;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;
import java.util.Objects;

public class JobSpawnerNbtData implements IJobSpawner.IJobSpawnerData {

	protected final @Nonnull EntityNPCInterface parent;
	protected int count = 1;
	protected NBTTagCompound compound = new NBTTagCompound();
	protected Component title = null;

	public JobSpawnerNbtData(@Nonnull EntityNPCInterface npc) { parent = npc; }

	@Override
	public Component getTitle() {
		if (title == null) {
			Entity entity = EntityList.createEntityFromNBT(compound, parent.world);
			title = entity != null ? Component.literal(entity.getName()) : Component.literal(compound.getString("id"));
		}
		return title;
	}

	@Override
	public int getCount() { return count; }

	@Override
	public void setCount(int countIn) { count = ValueUtil.correctInt(countIn, 1, 7); }

	@Override
	public INbt getNbt() { return new NBTWrapper(save()); }

	@Override
	public IEntity<?> getEntity() {
		Entity entity = EntityList.createEntityFromNBT(compound, parent.world);
		return entity != null ? Objects.requireNonNull(NpcAPI.Instance()).getIEntity(entity) : null;
	}

	@Override
	public boolean isValid() {
		return EntityList.createEntityFromNBT(compound, parent.world) != null;
	}

	@Override
	public void setNbt(INbt nbt) {
		if (nbt != null) { load(nbt.getMCNBT()); }
	}

	public boolean isClientClone() { return compound.getBoolean("ClientClone"); }

	public void load(@Nonnull NBTTagCompound nbt) { compound = nbt; }

	public @Nonnull NBTTagCompound save() { return compound; }

	@Override
	public String toString() { return "JobSpawnerNbtData{ Name: \"" + getTitle().getString() + "\", isClientClone: " + isClientClone() + "}"; }

}
