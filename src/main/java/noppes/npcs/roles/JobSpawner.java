package noppes.npcs.roles;

import java.util.*;

import noppes.npcs.api.entity.IEntity;
import noppes.npcs.roles.data.JobSpawnerNbtData;
import noppes.npcs.roles.data.NPCSpawnerSetting;
import noppes.npcs.util.ValueUtil;
import org.apache.commons.lang3.RandomStringUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.Path;
import net.minecraft.world.EnumDifficulty;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.entity.data.role.IJobSpawner;
import noppes.npcs.entity.EntityNPCInterface;

import javax.annotation.Nonnull;

public class JobSpawner extends JobInterface implements IJobSpawner {

	protected final @Nonnull EntityNPCInterface npc;
	protected final Map<Boolean, NPCSpawnerSetting> data = new HashMap<>(); // false=alive | true=dead
	protected String id = RandomStringUtils.random(8, true, true);
	protected long cooldownSet = 3000L; // setting time cooldown
	protected long cooldown = System.currentTimeMillis() + cooldownSet; // cooldown time if alive
	protected EntityLivingBase target;
	protected int distance = 60;
	public boolean exact = false;
	public boolean resetUpdate = true;

	public JobSpawner(@Nonnull EntityNPCInterface npcIn) {
		super(npcIn);
		npc = npcIn;
		type = JobType.SPAWNER;
		data.put(false, new NPCSpawnerSetting(npc));
		data.put(true, new NPCSpawnerSetting(npc));
	}

	@Override
	public NBTTagCompound save(NBTTagCompound compound) {
		super.save(compound);
		if (!data.containsKey(false)) { data.put(false, new NPCSpawnerSetting(npc)); }
		if (!data.containsKey(true)) { data.put(true, new NPCSpawnerSetting(npc)); }
		compound.setString("SpawnerId", id);
		compound.setTag("SettingWhenAlive", data.get(false).save());
		compound.setTag("SettingWhenDead", data.get(true).save());
		compound.setInteger("SettingDistance", distance);
		compound.setLong("SpawnerCooldownSetting", cooldownSet);
		compound.setBoolean("IsExactOffsetSpawn", exact);
		compound.setBoolean("DespawnInReset", resetUpdate);
		return compound;
	}

	@Override
	public void load(NBTTagCompound compound) {
		super.load(compound);
		type = JobType.SPAWNER;
		id = compound.getString("SpawnerId");
		NPCSpawnerSetting alive = data.get(false);
		NPCSpawnerSetting dead = data.get(true);
		alive.clear();
		dead.clear();
		distance = 60;
		if (compound.hasKey("SpawnerDoesntDie", 1)) {
			cooldownSet = 3000L;
			exact = false;
			resetUpdate = true;
			List<IJobSpawner.IJobSpawnerData> sDs = new ArrayList<>();
			for (int i = 1; i < 7; i++) {
				if (!compound.hasKey("SpawnerNBT" + i, 10)) { continue; }
				JobSpawnerNbtData sd = new JobSpawnerNbtData(npc);
				sd.load(compound.getCompoundTag("SpawnerNBT" + i));
				sDs.add(sd);
			}
			int i = 0;
			if (compound.getBoolean("SpawnerDoesntDie")) {
				dead.spawnType = compound.getInteger("SpawnerType");
				dead.offset[0] = compound.getInteger("SpawnerXOffset");
				dead.offset[1] = compound.getInteger("SpawnerYOffset");
				dead.offset[2] = compound.getInteger("SpawnerZOffset");
				dead.despawnOnTargetLost = compound.getBoolean("DespawnOnTargetLost");
				for (IJobSpawner.IJobSpawnerData sd : sDs) { dead.dataEntitys.put(i++, sd); }
			} // dead
			else {
				alive.spawnType = compound.getInteger("SpawnerType");
				alive.offset[0] = compound.getInteger("SpawnerXOffset");
				alive.offset[1] = compound.getInteger("SpawnerYOffset");
				alive.offset[2] = compound.getInteger("SpawnerZOffset");
				alive.despawnOnTargetLost = compound.getBoolean("DespawnOnTargetLost");
				for (IJobSpawner.IJobSpawnerData sd : sDs) { alive.dataEntitys.put(i++, sd); }
			} // Alive
		} // very OLD
		else {
			cooldownSet = compound.getLong("SpawnerCooldownSetting");
			exact = compound.getBoolean("IsExactOffsetSpawn");
			resetUpdate = compound.getBoolean("DespawnInReset");
			if (compound.hasKey("SettingDistance", 3)) { distance = compound.getInteger("SettingDistance"); }
			if (compound.hasKey("SpawnerWhenAlive", 3)) {
				alive.spawnType = compound.getInteger("SpawnerWhenAlive");
				dead.spawnType = compound.getInteger("SpawnerWhenDead");
				for (int i = 0; i < 2; i++) {
					int[] array = compound.getIntArray("OffsetWhen" + (i == 0 ? "Alive" : "Dead"));
					for (int k = 0; k < 3 && k < array.length; k++) {
						(i == 0 ? alive : dead).offset[k] = array[k];
					}
					NBTTagList nbt = compound.getTagList("DataEntitysWhen" + (i == 0 ? "Alive" : "Dead"), 10);
					for (int slot = 0; slot < nbt.tagCount(); slot++) {
						JobSpawnerNbtData sd = new JobSpawnerNbtData(npc);
						sd.load(nbt.getCompoundTagAt(slot));
						(i == 0 ? alive : dead).dataEntitys.put(slot, sd);
					}
				}
				alive.despawnOnTargetLost = compound.getBoolean("DespawnOnTargetLostWhenAlive");
				dead.despawnOnTargetLost = compound.getBoolean("DespawnOnTargetLostWhenDead");
			} // OLD in 1.12.2
			else {
				alive.load(compound.getCompoundTag("SettingWhenAlive"));
				dead.load(compound.getCompoundTag("SettingWhenDead"));
			}
		} // NEW
	}

	@Override
	public void aiDeathExecute(Entity attackingEntity) {
		if (attackingEntity instanceof EntityLivingBase) { target = (EntityLivingBase) attackingEntity; }
		aiUpdateTask();
	} // when death

	@Override
	public boolean aiShouldExecute() {
		if (!data.containsKey(false)) { data.put(false, new NPCSpawnerSetting(npc)); }
		if (!data.containsKey(true)) { data.put(true, new NPCSpawnerSetting(npc)); }
		boolean isDead = npc.getHealth() <= 0;
		if (isEmpty(isDead) || npc.isKilled()) { return false; }
		target = getTarget();
		if (!data.get(isDead).spawned.isEmpty()) { checkSpawns(); }
		return target != null;
	}

	@Override
	public void aiStartExecuting() {
		for (int i = 0; i < 2; i++) {
			NPCSpawnerSetting npcSS = data.get(i == 0);
			npcSS.number = 0;
			for (Entity entity : new ArrayList<>(npcSS.spawned)) {
				int slot = entity.getEntityData().getInteger("NpcSpawnerSlot");
				if (slot > npcSS.number) { npcSS.number = slot; }
				conveyTarget(entity, getTarget());
			}
		}
	} // after reset NPC

	@Override
	public void aiUpdateTask() {
		boolean isDead = npc.getHealth() <= 0;
		NPCSpawnerSetting npcSS = data.get(isDead);
		if (!npcSS.spawned.isEmpty()) {
			if (npc.world.getTotalWorldTime() % 20 == 0) {
				cooldown = System.currentTimeMillis() + (long) ((double) cooldownSet * (npc.getRNG().nextFloat() < 0.5f ? 1.1d : 0.9d));
			}
			checkSpawns();
			return;
		} // Has Spawned
		if (getTarget() == null || !isDead && isOnCooldown()) { return; } // is Alive and or Cooldown
		switch (npcSS.spawnType) {
			case 0: {
				spawnEntity(npcSS.number, isDead);
				npcSS.number++;
				if (npcSS.number > npcSS.dataEntitys.size()) { npcSS.number = 0; }
				break;
			} // one to one
			case 1: {
				while (npcSS.dataEntitys.size() > 7) { npcSS.dataEntitys.remove(npcSS.dataEntitys.size() - 1); }
				for (int slot : npcSS.dataEntitys.keySet()) {
					npcSS.number = slot;
					spawnEntity(slot, isDead);
				}
				break;
			} // all
			default: {
				npcSS.number = npc.getRNG().nextInt(npcSS.dataEntitys.size());
				spawnEntity(npcSS.number, isDead);
				break;
			} // random
		}
	} // after start any 20 ticks

	public void checkSpawns() {
		for (int i = 0; i < 2; i++) {
			NPCSpawnerSetting npcSS = data.get(i == 0);
			for (Entity spawn : new ArrayList<>(npcSS.spawned)) {
				if (shouldDelete(spawn)) {
					spawn.isDead = true;
					npcSS.spawned.remove(spawn);
				}
				else { checkTarget(spawn); }
			}
		}
	}

	public void checkTarget(Entity entity) {
		if (entity instanceof EntityLiving) {
			EntityLiving liv = (EntityLiving) entity;
			if (liv.getAttackTarget() == null || npc.getRNG().nextInt(100) == 1) {
				liv.setAttackTarget(target);
			}
		}
		else if (entity instanceof EntityLivingBase) {
			EntityLivingBase livb = (EntityLivingBase) entity;
			if (livb.getRevengeTarget() == null || npc.getRNG().nextInt(100) == 1) {
				livb.setRevengeTarget(target);
			}
		}
	}

	public void cleanCompound(NBTTagCompound compound) {
		for (int i = 0; i < 2; i++) {
			String key = "DataEntitysWhen" + (i == 0 ? "Alive" : "Dead");
			for (int j = 0; j < compound.getTagList(key, 10).tagCount(); j++) {
				NBTTagCompound sdNbt = compound.getTagList(key, 10).getCompoundTagAt(j).getCompoundTag("EntityNBT");
				String name = "type.empty";
                sdNbt = sdNbt.copy();
                if (sdNbt.hasKey("ClonedName", 8)) {
                    name = sdNbt.getString("ClonedName");
                }
				else if (sdNbt.hasKey("Name", 8)) {
                    name = sdNbt.getString("Name");
                }
				else if (sdNbt.hasKey("id", 8)) {
					if (npc.world != null) {
						Entity entity = EntityList.createEntityFromNBT(sdNbt, npc.world);
						if (entity != null) { name = entity.getName(); }
					}
                }
                compound.getTagList(key, 10).getCompoundTagAt(j).removeTag("EntityNBT");
				compound.getTagList(key, 10).getCompoundTagAt(j).setString("Name", name);
				if (sdNbt.hasKey("ClonedName", 8)) {
					compound.getTagList(key, 10).getCompoundTagAt(j).setString("ClonedName", sdNbt.getString("ClonedName"));
				}
				if (sdNbt.hasKey("ClonedTab", 3)) {
					compound.getTagList(key, 10).getCompoundTagAt(j).setInteger("ClonedTab", sdNbt.getInteger("ClonedTab"));
				}
			}
		}
	}

	public void clear(boolean isDead) { data.get(isDead).dataEntitys.clear(); }

	@Override
	public NPCSpawnerSetting get(boolean isDead) { return data.get(isDead); }

	public long getCooldown() { return cooldownSet; }

	public boolean getDespawnOnTargetLost(boolean isDead) { return data.get(isDead).despawnOnTargetLost; }

	public String getId() { return id; }

	private List<EntityLivingBase> getNearbySpawned(boolean isDead) {
		List<EntityLivingBase> list = new ArrayList<>();
		try { list = npc.world.getEntitiesWithinAABB(EntityLivingBase.class, npc.getEntityBoundingBox().grow(distance, distance, distance),
				entity -> entity.isDead && entity.getEntityData().getString("NpcSpawnerId").equals(id)
                && entity.getEntityData().getBoolean("NpcSpawnerDead") == isDead); }
		catch (Exception ignored) { }
		return new ArrayList<>(list);
	}

	public int[] getOffset(boolean isDead) { return data.get(isDead).offset; }

	public int getSpawnType(boolean isDead) { return data.get(isDead).spawnType; }

	private EntityLivingBase getTarget() {
		target = getTarget(npc);
		if (target != null) { return target; }
		for (int i = 0; i < 2; i++) {
			for (Entity entity : data.get(i == 0).spawned) {
				if (entity instanceof EntityLivingBase) {
					target = getTarget((EntityLivingBase) entity);
					if (target != null) { return target; }
				}
			}
		}
		return target;
	}

	private EntityLivingBase getTarget(EntityLivingBase entity) {
		if (entity == null || (entity == npc && (entity.isDead || entity.getHealth() <= 0.0))) {
			return target;
		}
		if (entity instanceof EntityLiving) {
			target = ((EntityLiving) entity).getAttackTarget();
			if (target != null && !target.isDead && target.getHealth() > 0.0f) { return target; }
		}
		target = entity.getRevengeTarget();
		if (target != null && !target.isDead && target.getHealth() > 0.0f) {
			return entity.getDistance(target) > distance ? null : target;
		}
		return null;
	}

	public boolean isOnCooldown() { return System.currentTimeMillis() < cooldown; }

	@Override
	public void killed() { reset(); }

	@Override
	public void clear() {
		for (int i = 0; i < 2; i++) {
			for (Entity entity : data.get(i == 0).spawned) { entity.isDead = true; }
			data.get(i == 0).spawned.clear();
		}
	}

	public void removeCompound(NBTTagCompound compound) {
		for (int i = 0; i < 2; i++) {
			String keyOld = "DataEntitysWhen" + (i == 0 ? "Alive" : "Dead");
			String key = "SettingWhen" + (i == 0 ? "Alive" : "Dead");
			NBTTagList list = compound.getTagList(keyOld, 10);
			for (int j = 0; j < list.tagCount(); j++) { list.getCompoundTagAt(j).removeTag("EntityNBT"); }
			list = compound.getCompoundTag(key).getTagList("DataEntitys", 10);
			for (int j = 0; j < list.tagCount(); j++) {
				NBTTagCompound nbt = list.getCompoundTagAt(j);
				if (!nbt.hasKey("tag", 3) && !nbt.hasKey("name", 8)) { list.removeTag(j); }
			}
		}
	}

	public void removeSpawned(int slot, boolean isDead) {
		NPCSpawnerSetting settings = data.get(isDead);
		if (slot >= 0 && slot < settings.dataEntitys.size()) {
			Map<Integer, IJobSpawner.IJobSpawnerData> newSData = new HashMap<>();
			for (int i = 0, j = 0; i < settings.dataEntitys.size(); i++) {
				if (i != slot) {
					newSData.put(j, settings.dataEntitys.get(i));
					j++;
				}
			}
			settings.dataEntitys.clear();
			settings.dataEntitys.putAll(newSData);
		}
	}

	@Override
	public void reset() {
		for (int i = 0; i < 2; i++) {
			data.get(i == 0).number = 0;
			if (data.get(i == 0).spawned.isEmpty()) { data.get(i == 0).spawned.addAll(getNearbySpawned(i == 0)); }
		}
		target = null;
		cooldown = 0L;
		checkSpawns();
	}

	@Override
	public void stop() { reset(); }

	public void setCooldown(int ticks) { cooldownSet = ValueUtil.onlyPositiveInt(ticks, 6000) * 50L; }

	public void setCooldown(long ticks) {
		if (ticks < 0L) { ticks *= -1; }
		if (ticks > 300000L) { ticks = 300000L; }
		cooldownSet = ticks;
	}

	public void setDespawnOnTargetLost(boolean isDead, boolean isLost) { data.get(isDead).despawnOnTargetLost = isLost; }

	public void setSpawnType(boolean isDead, int readInt) {
		if (readInt < 0) { readInt *= -1; }
		if (readInt > 2) { readInt = readInt % 3; }
		data.get(isDead).spawnType = readInt;
	}

	private void conveyTarget(Entity base, EntityLivingBase targetIn) {
		if (base instanceof EntityLiving) { ((EntityLiving) base).setAttackTarget(targetIn); }
		else if (base instanceof EntityLivingBase) { ((EntityLivingBase) base).setRevengeTarget(targetIn); }
		if (npc == base) { target = targetIn; }
	}

	public boolean shouldDelete(Entity entity) {
		IJobSpawner.IJobSpawnerData sp = null;
		boolean sets = false;
		boolean isDead = npc.getHealth() <= 0;
		// EntityData
		NBTTagCompound eNbt = entity.getEntityData();
		if (eNbt.hasKey("NpcSpawnerEntityId", 3) && eNbt.hasKey("NpcSpawnerSlot", 3)
				&& eNbt.hasKey("NpcSpawnerId", 8)
				&& eNbt.hasKey("NpcSpawnerDead", 1)) {
			if (resetUpdate && isDead != eNbt.getBoolean("NpcSpawnerDead")) { return true; }
			sets = eNbt.getString("NpcSpawnerId").equals(id) && eNbt.getInteger("NpcSpawnerEntityId") == npc.getEntityId();
			sp = data.get(eNbt.getBoolean("NpcSpawnerDead")).get(eNbt.getInteger("NpcSpawnerSlot"));
		}
		if (!sets || sp == null) { return true; }
		// Destination or Dead
		if (entity.isDead || (entity instanceof EntityLiving && ((EntityLiving) entity).getHealth() <= 0.0f)) { return true; }
		if (!npc.isInRange(entity, distance)) {
			if (entity instanceof EntityLivingBase) { ((EntityLivingBase) entity).setRevengeTarget(null); }
			entity.setPosition(npc.posX, npc.posY, npc.posZ);
			return false;
		}
		// Target
		if (!data.get(isDead).despawnOnTargetLost) { return false; }
		if (entity instanceof EntityLivingBase) {
			EntityLivingBase livb = (EntityLivingBase) entity;
			if (livb.getRevengeTarget() == null) { conveyTarget(livb, getTarget()); } // try set
			if (livb.getRevengeTarget() == null) { livb.setRevengeTarget(getTarget()); }
			return livb.getRevengeTarget() == null;
		}
		return false;
	}

	public int size(boolean isDead) { return data.get(isDead).dataEntitys.size(); }

	@Override
	public List<IEntity<?>> spawnEntity(int slotId, boolean isDead) {
		NPCSpawnerSetting settings = data.get(isDead);
		IJobSpawner.IJobSpawnerData sd = settings.get(slotId);
		List<IEntity<?>> list = new ArrayList<>();
		if (sd != null && sd.isValid()) {
			if (target == null) { target = npc.getAttackTarget(); }
			if (!isDead && (target == null || npc.getDistance(getTarget()) > npc.stats.aggroRange)) { return list; }
			for (int i = 0; i < sd.getCount(); i++) {
				Entity entity = sd.getEntity().getMCEntity();
				if (npc.world.getDifficulty() == EnumDifficulty.PEACEFUL && entity instanceof EntityMob) { continue; }
				int add = !exact && settings.spawnType == 1 ? 2 : 0;
				double x = npc.posX + (add + settings.offset[0]) * (exact ? 1 : npc.getRNG().nextFloat() * (npc.getRNG().nextFloat() < 0.5f ? -1 : 1)) - 0.5 + npc.getRNG().nextFloat();
				double y = npc.posY + (add + settings.offset[1]) * (exact ? 1 : npc.getRNG().nextFloat() * (npc.getRNG().nextFloat() < 0.5f ? -1 : 1));
				double z = npc.posZ + (add + settings.offset[2]) * (exact ? 1 : npc.getRNG().nextFloat() * (npc.getRNG().nextFloat() < 0.5f ? -1 : 1)) - 0.5 + npc.getRNG().nextFloat();
				Path path = npc.getNavigator().getPathToXYZ(x, y, z);
				if (path != null && path.getFinalPathPoint() != null) {
					x = path.getFinalPathPoint().x;
					y = path.getFinalPathPoint().y;
					z = path.getFinalPathPoint().z;
				} // Corrector
				else {
					x = npc.posX;
					y = npc.posY;
					z = npc.posZ;
				}
				entity.setPosition(x, y, z);
				npc.world.spawnEntity(entity);
				entity.getEntityData().setInteger("NpcSpawnerEntityId", npc.getEntityId());
				entity.getEntityData().setInteger("NpcSpawnerSlot", data.get(isDead).number);
				entity.getEntityData().setString("NpcSpawnerId", id);
				entity.getEntityData().setBoolean("NpcSpawnerDead", isDead);
				conveyTarget(entity, target);
				entity.setPosition(x, y, z);
				if (entity instanceof EntityNPCInterface) {
					EntityNPCInterface cnpc = (EntityNPCInterface) entity;
					cnpc.advanced.spawner = npc;
					cnpc.stats.spawnCycle = 4;
					cnpc.stats.respawnTime = 0;
					cnpc.ais.returnToStart = false;
					cnpc.ais.onAttack = 0;
				}
				data.get(isDead).spawned.add(entity);
				list.add(Objects.requireNonNull(NpcAPI.Instance()).getIEntity(entity));
			}
		}
		return list;
	}

	private boolean isEmpty(boolean isDead) {
		for (IJobSpawner.IJobSpawnerData sd : data.get(isDead).dataEntitys.values()) {
			if (!sd.isValid()) { return false; }
		}
		return true;
	}

}
