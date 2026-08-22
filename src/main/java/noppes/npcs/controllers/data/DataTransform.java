package noppes.npcs.controllers.data;

import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NBTTags;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobInterface;
import noppes.npcs.roles.RoleInterface;

public class DataTransform {

	private final EntityNPCInterface npc;
	public boolean isActive;
	public NBTTagCompound display;
	public NBTTagCompound ai;
	public NBTTagCompound advanced;
	public NBTTagCompound inv;
	public NBTTagCompound stats;
	public NBTTagCompound role;
	public NBTTagCompound job;
	public boolean hasDisplay;
	public boolean hasAi;
	public boolean hasAdvanced;
	public boolean hasInv;
	public boolean hasStats;
	public boolean hasRole;
	public boolean hasJob;
	public boolean editingModus = false;

	// New from Unofficial (BetaZavr)
	public NBTTagCompound animation;
	public boolean hasAnimations;

	public DataTransform(EntityNPCInterface npcIn) { npc = npcIn; }

	public NBTTagCompound save(NBTTagCompound compound) {
		compound.setBoolean("TransformIsActive", isActive);
		saveOptions(compound);
		if (hasDisplay) { compound.setTag("TransformDisplay", display); }
		if (hasAi) { compound.setTag("TransformAI", ai); }
		if (hasAdvanced) { compound.setTag("TransformAdvanced", advanced); }
		if (hasInv) { compound.setTag("TransformInv", inv); }
		if (hasStats) { compound.setTag("TransformStats", stats); }
		if (hasRole) { compound.setTag("TransformRole", role); }
		if (hasJob) { compound.setTag("TransformJob", job); }
		if (hasAnimations) { compound.setTag("TransformAnimations", animation); }
		return compound;
	}

	public NBTTagCompound saveOptions(NBTTagCompound compound) {
		compound.setBoolean("TransformHasDisplay", hasDisplay);
		compound.setBoolean("TransformHasAI", hasAi);
		compound.setBoolean("TransformHasAdvanced", hasAdvanced);
		compound.setBoolean("TransformHasInv", hasInv);
		compound.setBoolean("TransformHasStats", hasStats);
		compound.setBoolean("TransformHasRole", hasRole);
		compound.setBoolean("TransformHasJob", hasJob);
		compound.setBoolean("TransformEditingModus", editingModus);
		compound.setBoolean("TransformHasAnimations", hasAnimations);
		return compound;
	}

	public void load(NBTTagCompound compound) {
		isActive = compound.getBoolean("TransformIsActive");
		loadOptions(compound);
		display = (hasDisplay ? compound.getCompoundTag("TransformDisplay") : getDisplay());
		ai = (hasAi ? compound.getCompoundTag("TransformAI") : npc.ais.save(new NBTTagCompound()));
		advanced = (hasAdvanced ? compound.getCompoundTag("TransformAdvanced") : getAdvanced());
		inv = (hasInv ? compound.getCompoundTag("TransformInv") : npc.inventory.save(new NBTTagCompound()));
		stats = (hasStats ? compound.getCompoundTag("TransformStats") : npc.stats.save(new NBTTagCompound()));
		job = (hasJob ? compound.getCompoundTag("TransformJob") : getJob());
		role = (hasRole ? compound.getCompoundTag("TransformRole") : getRole());
		animation = (hasAnimations ? compound.getCompoundTag("TransformAnimations") : npc.animation.save(new NBTTagCompound()));
	}

	public void loadOptions(NBTTagCompound compound) {
		boolean oldHasDisplay = hasDisplay;
		boolean oldHasAI = hasAi;
		boolean oldHasAdvanced = hasAdvanced;
		boolean oldHasInv = hasInv;
		boolean oldHasStats = hasStats;
		boolean oldHasRole = hasRole;
		boolean oldHasJob = hasJob;
		boolean oldHasAnimations = hasAnimations;
		hasDisplay = compound.getBoolean("TransformHasDisplay");
		hasAi = compound.getBoolean("TransformHasAI");
		hasAdvanced = compound.getBoolean("TransformHasAdvanced");
		hasInv = compound.getBoolean("TransformHasInv");
		hasStats = compound.getBoolean("TransformHasStats");
		hasRole = compound.getBoolean("TransformHasRole");
		hasJob = compound.getBoolean("TransformHasJob");
		editingModus = compound.getBoolean("TransformEditingModus");
		hasAnimations = compound.getBoolean("TransformHasAnimations");
		if (hasDisplay && !oldHasDisplay) { display = getDisplay(); }
		if (hasAi && !oldHasAI) { ai = npc.ais.save(new NBTTagCompound()); }
		if (hasStats && !oldHasStats) { stats = npc.stats.save(new NBTTagCompound()); }
		if (hasInv && !oldHasInv) { inv = npc.inventory.save(new NBTTagCompound()); }
		if (hasAdvanced && !oldHasAdvanced) { advanced = npc.advanced.save(new NBTTagCompound()); }
		if (hasJob && !oldHasJob) { job = npc.job.save(new NBTTagCompound()); }
		if (hasRole && !oldHasRole) { role = npc.role.save(new NBTTagCompound()); }
		if (hasAnimations && !oldHasAnimations) { animation = npc.animation.save(new NBTTagCompound()); }
	}

	public NBTTagCompound getJob() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("NpcJob", npc.job.getType());
		npc.job.save(compound);
		return compound;
	}

	public NBTTagCompound getRole() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("Role", npc.role.getType());
		npc.role.save(compound);
		return compound;
	}

	public NBTTagCompound getDisplay() {
		NBTTagCompound compound = npc.display.save(new NBTTagCompound());
		if (npc instanceof EntityCustomNpc) {
			compound.setTag("ModelData", ((EntityCustomNpc) npc).modelData.save());
		}
		return compound;
	}

	public NBTTagCompound getAdvanced() {
		JobInterface jopType = npc.job;
		RoleInterface roleType = npc.role;
		npc.job = JobInterface.NONE;
		npc.role = RoleInterface.NONE;
		NBTTagCompound compound = npc.advanced.save(new NBTTagCompound());
		npc.job = jopType;
		npc.role = roleType;
		return compound;
	}

	public boolean isValid() {
		return hasAdvanced || hasAi || hasDisplay || hasInv || hasStats || hasJob || hasRole || hasAnimations;
	}

	public NBTTagCompound processAdvanced(NBTTagCompound compoundAdv, NBTTagCompound compoundRole, NBTTagCompound compoundJob) {
		if (hasAdvanced) { compoundAdv = advanced; }
		if (hasRole) { compoundRole = role; }
		if (hasJob) { compoundJob = job; }
		Set<String> names = compoundRole.getKeySet();
		for (String name : names) { compoundAdv.setTag(name, compoundRole.getTag(name)); }
		names = compoundJob.getKeySet();
		for (String name : names) { compoundAdv.setTag(name, compoundJob.getTag(name)); }
		return compoundAdv;
	}

	public void transform(boolean isActiveIn) {
		if (isActive == isActiveIn) { return; }
		NBTTagCompound compoundAdv;
		if (hasDisplay) {
			compoundAdv = getDisplay();
			npc.display.load(NBTTags.nbtMerge(compoundAdv, display));
			if (npc instanceof EntityCustomNpc) {
				((EntityCustomNpc) npc).modelData.load(NBTTags.nbtMerge(compoundAdv.getCompoundTag("ModelData"), display.getCompoundTag("ModelData")));
			}
			display = compoundAdv;
		}
		if (hasStats) {
			compoundAdv = npc.stats.save(new NBTTagCompound());
			npc.stats.load(NBTTags.nbtMerge(compoundAdv, stats));
			stats = compoundAdv;
		}
		if (hasAdvanced || hasJob || hasRole) {
			compoundAdv = npc.advanced.save(new NBTTagCompound());
			NBTTagCompound compoundJob = getJob();
			NBTTagCompound compoundRole = getRole();
			NBTTagCompound compound = processAdvanced(compoundAdv, compoundRole, compoundJob);
			npc.advanced.load(compound);
			if (npc.role.getType() != 0) { npc.role.load(NBTTags.nbtMerge(compoundRole, compound)); }
			if (npc.job.getType() != 0) { npc.job.load(NBTTags.nbtMerge(compoundJob, compound)); }
			if (hasAdvanced) { advanced = compoundAdv; }
			if (hasRole) { role = compoundRole; }
			if (hasJob) {job = compoundJob; }
		}
		if (hasAi) {
			compoundAdv = npc.ais.save(new NBTTagCompound());
			npc.ais.load(NBTTags.nbtMerge(compoundAdv, ai));
			ai = compoundAdv;
			npc.setCurrentAnimation(npc.ais.animationType);
		}
		if (hasInv) {
			compoundAdv = npc.inventory.save(new NBTTagCompound());
			npc.inventory.load(NBTTags.nbtMerge(compoundAdv, inv));
			inv = compoundAdv;
		}
		if (hasAnimations) {
			compoundAdv = npc.animation.save(new NBTTagCompound());
			npc.animation.load(NBTTags.nbtMerge(compoundAdv, animation));
			animation = compoundAdv;
		}
		npc.updateAI = true;
		isActive = isActiveIn;
		npc.updateClient = true;
	}

}
