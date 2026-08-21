package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandom;
import noppes.npcs.NBTTags;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;

public class SpawnData extends WeightedRandom.Item {

	public List<String> biomes = new ArrayList<>();
	public String name = "";
	public boolean liquid = false;
	public int type = 0;
	public int id = -1;

	// New from Unofficial (BetaZavr)
	protected NBTTagCompound compoundEntity = new NBTTagCompound();
	public boolean canSeeSummon = true;
	public int group = 4;
	public int range = 8;
	public int maxNearPlayer = 10;

	public SpawnData() { super(10); }

	public void load(NBTTagCompound compound) {
		biomes = NBTTags.getStringList(compound.getTagList("SpawnBiomes", 10));
		name = compound.getString("SpawnName");
		liquid = compound.getBoolean("SpawnLiquid");
		type = compound.getInteger("SpawnType");
		id = compound.getInteger("SpawnId");
		itemWeight = ValueUtil.correctInt(compound.getInteger("SpawnWeight"), 1, 100);
		// New from Unofficial (BetaZavr)
		compoundEntity = compound.getCompoundTag("SpawnCompound1");
		if (compound.hasKey("PlayerCanSeeSummon", 1)) { canSeeSummon = compound.getBoolean("PlayerCanSeeSummon"); }
		if (compound.hasKey("MaxInGroup", 3)) { group = compound.getInteger("MaxInGroup"); }
		if (compound.hasKey("GroupInRange", 3)) { range = compound.getInteger("GroupInRange"); }
		if (compound.hasKey("MaximumNearPlayer", 3)) { maxNearPlayer = compound.getInteger("MaximumNearPlayer"); }
	}

	public NBTTagCompound save(NBTTagCompound compound) {
		compound.setTag("SpawnBiomes", NBTTags.nbtStringList(biomes));
		compound.setString("SpawnName", name);
		compound.setBoolean("SpawnLiquid", liquid);
		compound.setInteger("SpawnType", type);
		compound.setInteger("SpawnId", id);
		compound.setInteger("SpawnWeight", itemWeight);
		// New from Unofficial (BetaZavr)
		compound.setTag("SpawnCompound1", compoundEntity);
		compound.setBoolean("PlayerCanSeeSummon", canSeeSummon);
		compound.setInteger("MaxInGroup", group);
		compound.setInteger("GroupInRange", range);
		compound.setInteger("MaximumNearPlayer", maxNearPlayer);
		return compound;
	}

	// New from Unofficial (BetaZavr)
	@SuppressWarnings("unused")
	public void setClone(int tab, String name) { compoundEntity = new CloneSpawnData(tab, name).getCompound(); }

	public @Nonnull NBTTagCompound getCompound() { return compoundEntity; }

	public void setCompound(@Nonnull NBTTagCompound compound) { compoundEntity = compound; }

}
