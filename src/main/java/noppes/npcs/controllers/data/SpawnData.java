package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import noppes.npcs.NBTTags;

import javax.annotation.Nonnull;

public class SpawnData implements WeightedEntry {

   public List<ResourceLocation> biomes = new ArrayList<>();
   protected Weight weight = Weight.of(10);
   public String name = "";
   public boolean liquid = false;
   public int type = 0;
   public int id = -1;

   // New from Unofficial (BetaZavr)
   protected CompoundTag compoundEntity = new CompoundTag();
   public boolean canSeeSummon = true;
   public int group = 4;
   public int range = 8;
   public int maxNearPlayer = 10;

   public void load(CompoundTag compound) {
      biomes = NBTTags.getResourceLocationList(compound.getList("SpawnBiomes", 10));
      setWeight(compound.getInt("SpawnWeight"));
      name = compound.getString("SpawnName");
      liquid = compound.getBoolean("SpawnLiquid");
      type = compound.getInt("SpawnType");
      id = compound.getInt("SpawnId");
      // New from Unofficial (BetaZavr)
      compoundEntity = compound.getCompound("SpawnCompound1");
      if (compound.contains("PlayerCanSeeSummon", 1)) { canSeeSummon = compound.getBoolean("PlayerCanSeeSummon"); }
      if (compound.contains("MaxInGroup", 3)) { group = compound.getInt("MaxInGroup"); }
      if (compound.contains("GroupInRange", 3)) { range = compound.getInt("GroupInRange"); }
      if (compound.contains("MaximumNearPlayer", 3)) { maxNearPlayer = compound.getInt("MaximumNearPlayer"); }
   }

   public CompoundTag save(CompoundTag compound) {
      compound.put("SpawnBiomes", NBTTags.nbtResourceLocationList(biomes));
      compound.putInt("SpawnWeight", weight.asInt());
      compound.putString("SpawnName", name);
      compound.putBoolean("SpawnLiquid", liquid);
      compound.putInt("SpawnType", type);
      compound.putInt("SpawnId", id);
      // New from Unofficial (BetaZavr)
      compound.put("SpawnCompound1", compoundEntity);
      compound.putBoolean("PlayerCanSeeSummon", canSeeSummon);
      compound.putInt("MaxInGroup", group);
      compound.putInt("GroupInRange", range);
      compound.putInt("MaximumNearPlayer", maxNearPlayer);
      return compound;
   }

   public void setWeight(int weightIn) {
      if (weightIn == 0) { weightIn = 1; }
      weight = Weight.of(weightIn);
   }

   public void setClone(int tab, String name) { compoundEntity = new CloneSpawnData(tab, name).getCompound(); }

   public @Nonnull CompoundTag getCompound() { return compoundEntity; }

   public void setCompound(@Nonnull CompoundTag compound) { compoundEntity = compound; }

   @Override
   public @Nonnull Weight getWeight() { return weight; }

}
