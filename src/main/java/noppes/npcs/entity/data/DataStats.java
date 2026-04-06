package noppes.npcs.entity.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.entity.data.INPCMelee;
import noppes.npcs.api.entity.data.INPCRanged;
import noppes.npcs.api.entity.data.INPCStats;
import noppes.npcs.constants.EnumCreatureRarity;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataStats implements INPCStats {

   public int aggroRange = 16;
   public int maxHealth = 20;
   public int respawnTime = 20;
   public int spawnCycle = 0;
   public boolean hideKilledBody = false;
   public boolean immuneToFire = false;
   public boolean potionImmune = false;
   public boolean canDrown = true;
   public boolean burnInSun = false;
   public boolean noFallDamage = false;
   public boolean ignoreCobweb = false;
   public int healthRegen = 1;
   public int combatRegen = 0;
   public MobType creatureType;
   public DataMelee melee;
   public DataRanged ranged;
   private final EntityNPCInterface npc;

   // New from Unofficial (BetaZavr)
   public Resistances resistances = new Resistances();
   public boolean calmdown = true;
   private int level = 1;
   private String rarityTitle = ((char) 167) + "flv." + ((char) 167) + "21";
   private EnumCreatureRarity rarity = EnumCreatureRarity.NORMAL;
   private float chanceBlockDamage = 2.0f;

   public DataStats(EntityNPCInterface npcIn) {
      creatureType = MobType.UNDEFINED;
      npc = npcIn;
      melee = new DataMelee(npc);
      ranged = new DataRanged(npc);
   }

   public void load(CompoundTag compound) {
      setMaxHealth(compound.getInt("MaxHealth"));
      hideKilledBody = compound.getBoolean("HideBodyWhenKilled");
      aggroRange = compound.getInt("AggroRange");
      respawnTime = compound.getInt("RespawnTime");
      spawnCycle = compound.getInt("SpawnCycle");
      setCreatureType(compound.getInt("CreatureType"));
      healthRegen = compound.getInt("HealthRegen");
      combatRegen = compound.getInt("CombatRegen");
      immuneToFire = compound.getBoolean("ImmuneToFire");
      potionImmune = compound.getBoolean("PotionImmune");
      canDrown = compound.getBoolean("CanDrown");
      burnInSun = compound.getBoolean("BurnInSun");
      noFallDamage = compound.getBoolean("NoFallDamage");
      npc.setImmuneToFire(immuneToFire);
      ignoreCobweb = compound.getBoolean("IgnoreCobweb");
      melee.load(compound);
      ranged.load(compound);

      // New from Unofficial (BetaZavr)
      if (compound.contains("ChanceBlockDamage", 5)) { setChanceBlockDamage(compound.getFloat("ChanceBlockDamage")); }
      if (compound.contains("Resistances", 9)) { resistances.load(compound.getList("Resistances", 10)); }
      else { resistances.oldLoad(compound.getCompound("Resistances")); }
      level = compound.getInt("NPCLevel");
      rarity = EnumCreatureRarity.values()[compound.getInt("NPCRarity")];
      rarityTitle = compound.getString("RarityTitle");
      if (compound.contains("CalmdownRange", 1)) { calmdown = compound.getBoolean("CalmdownRange"); }
   }

   public CompoundTag save(CompoundTag compound) {
      compound.putInt("MaxHealth", maxHealth);
      compound.putInt("AggroRange", aggroRange);
      compound.putBoolean("HideBodyWhenKilled", hideKilledBody);
      compound.putInt("RespawnTime", respawnTime);
      compound.putInt("SpawnCycle", spawnCycle);
      compound.putInt("CreatureType", getCreatureType());
      compound.putInt("HealthRegen", healthRegen);
      compound.putInt("CombatRegen", combatRegen);
      compound.putBoolean("ImmuneToFire", immuneToFire);
      compound.putBoolean("PotionImmune", potionImmune);
      compound.putBoolean("CanDrown", canDrown);
      compound.putBoolean("BurnInSun", burnInSun);
      compound.putBoolean("NoFallDamage", noFallDamage);
      compound.putBoolean("IgnoreCobweb", ignoreCobweb);
      melee.save(compound);
      ranged.save(compound);
      // New from Unofficial (BetaZavr)
      compound.putFloat("ChanceBlockDamage", chanceBlockDamage);
      compound.put("Resistances", resistances.save());
      compound.putInt("NPCLevel", level);
      compound.putInt("NPCRarity", rarity.ordinal());
      compound.putString("RarityTitle", rarityTitle);
      compound.putBoolean("CalmdownRange", calmdown);
      return compound;
   }

   @Override
   public void setMaxHealth(int maxHealthIn) {
      if (maxHealth != maxHealthIn) {
         maxHealth = maxHealthIn;
         Objects.requireNonNull(npc.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(maxHealth);
         npc.updateClient = true;
      }
   }

   @Override
   public int getMaxHealth() { return maxHealth; }

   @Override
   public int getCombatRegen() { return combatRegen; }

   @Override
   public void setCombatRegen(int regen) { combatRegen = regen; }

   @Override
   public int getHealthRegen() { return healthRegen; }

   @Override
   public void setHealthRegen(int regen) {
      healthRegen = regen;
   }

   @Override
   public INPCMelee getMelee() { return melee; }

   @Override
   public INPCRanged getRanged() { return ranged; }

   @Override
   public boolean getImmune(int type) {
      return switch (type) {
         case 0 -> potionImmune;
         case 1 -> noFallDamage;
         case 2 -> burnInSun;
         case 3 -> immuneToFire;
         case 4 -> canDrown;
         case 5 -> ignoreCobweb;
         default -> throw new CustomNPCsException("Unknown immune type: " + type);
      };
   }

   @Override
   public void setImmune(int type, boolean bo) {
      switch (type) {
         case 0 -> potionImmune = bo;
         case 1 -> noFallDamage = bo;
         case 2 -> burnInSun = bo;
         case 3 -> immuneToFire = bo;
         case 4 -> canDrown = bo;
         case 5 -> ignoreCobweb = bo;
         default -> throw new CustomNPCsException("Unknown immune type: " + type);
      }
   }

   @Override
   public int getCreatureType() {
      if (creatureType == MobType.UNDEAD) { return 1; }
      else if (creatureType == MobType.ARTHROPOD) { return 2; }
      else if (creatureType == MobType.ILLAGER) { return 3; }
      else if (creatureType == MobType.WATER) { return 4; }
      return 0;
   }

   @Override
   public void setCreatureType(int type) {
      creatureType = switch (type) {
         case 1 -> MobType.UNDEAD;
         case 2 -> MobType.ARTHROPOD;
         case 3 -> MobType.ILLAGER;
         case 4 -> MobType.WATER;
         default -> MobType.UNDEFINED;
      };
   }

   @Override
   public int getRespawnType() { return spawnCycle; }

   @Override
   public void setRespawnType(int type) { spawnCycle = type; }

   @Override
   public int getRespawnTime() { return respawnTime; }

   @Override
   public void setRespawnTime(int seconds) { respawnTime = seconds; }

   @Override
   public boolean getHideDeadBody() { return hideKilledBody; }

   @Override
   public void setHideDeadBody(boolean hide) {
      hideKilledBody = hide;
      npc.updateClient = true;
   }

   public int getAggroRange() {
      return aggroRange;
   }

   public void setAggroRange(int range) {
      aggroRange = range;
      npc.restrictTo(npc.ais.startPos(), aggroRange * 2);
   }

   // New from Unofficial (BetaZavr)
   @Override
   public List<String> getResistanceKeys() {
      return new ArrayList<>(resistances.data.keySet());
   }

   @Override
   public float getResistance(String type) { return resistances.get(type); }

   @Override
   public void setResistance(String type, float value) { resistances.data.put(type, ValueUtil.correctFloat(value, 0.0f, 2.0f)); }

   @Override
   public int getLevel() { return level = ValueUtil.correctInt(level, 1, CustomNpcs.MaxLv); }

   @Override
   public void setLevel(int levelIn) { level = ValueUtil.correctInt(levelIn, 1, CustomNpcs.MaxLv); }

   @Override
   public int getRarity() { return rarity.ordinal(); }

   @Override
   public void setRarity(int rarityIn) {
      rarity = EnumCreatureRarity.values()[ValueUtil.correctInt(rarityIn, 0, EnumCreatureRarity.values().length)];
      npc.updateClient = true;
   }

   @Override
   public String getRarityTitle() { return rarityTitle; }

   @Override
   public void setRarityTitle(String rarity) {
      if (rarityTitle.equals(rarity)) { return; }
      rarityTitle = rarity;
      npc.updateClient = true;
   }

   public double getHP() {
      int[] corr = CustomNpcs.HealthNormal;
      if (rarity == EnumCreatureRarity.ELITE) { corr = CustomNpcs.HealthElite; }
      else if (rarity == EnumCreatureRarity.BOSS) { corr = CustomNpcs.HealthBoss; }
      double a = ((double) corr[0] - (double) corr[1]) / (1 - Math.pow(CustomNpcs.MaxLv, 2));
      double b = (double) corr[0] - a;
      double hp = Math.round(a * Math.pow(level, 2) + b);
      if (hp <= 1.0d) { hp = 1.0d; }
      if (hp > 10000) { hp = Math.ceil(hp / 100.0d) * 100.0d; }
      else if (hp > 1000) { hp = Math.ceil(hp / 25.0d) * 25.0d; }
      else if (hp > 100) { hp = Math.ceil(hp / 10.0d) * 10.0d; }
      else if (hp > 50) { hp = Math.ceil(hp / 5.0d) * 5.0d; }
      else { hp = Math.ceil(hp); }
      if (hp > (double) corr[1]) { hp = corr[1]; }
      return hp;
   }

   @Override
   public float getChanceBlockDamage() { return chanceBlockDamage; }

   @Override
   public void setChanceBlockDamage(float chance) {
      if (chance < 0.0f) { chance *= -1.0f; }
      if (chance > 100.0f) { chance = 100.0f; }
      chanceBlockDamage = chance;
   }

   // New from Unofficial (BetaZavr)
   @Override
   public boolean isCalmdown() { return calmdown; }

   @Override
   public void setCalmdown(boolean range) { calmdown = range; }

}
