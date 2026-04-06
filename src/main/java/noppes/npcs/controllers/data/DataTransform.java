package noppes.npcs.controllers.data;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import noppes.npcs.NBTTags;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobInterface;
import noppes.npcs.roles.RoleInterface;

public class DataTransform {

   private final EntityNPCInterface npc;
   public boolean isActive;
   public CompoundTag display;
   public CompoundTag ai;
   public CompoundTag advanced;
   public CompoundTag inv;
   public CompoundTag stats;
   public CompoundTag role;
   public CompoundTag job;
   public boolean hasDisplay;
   public boolean hasAi;
   public boolean hasAdvanced;
   public boolean hasInv;
   public boolean hasStats;
   public boolean hasRole;
   public boolean hasJob;
   public boolean editingModus = false;

   // New from Unofficial (BetaZavr)
   public CompoundTag animation;
   public boolean isDay;
   public boolean hasAnimations;

   public DataTransform(EntityNPCInterface npcIn) { npc = npcIn; }

   public CompoundTag save(CompoundTag compound) {
      compound.putBoolean("TransformIsActive", isActive);
      saveOptions(compound);
      if (hasDisplay) { compound.put("TransformDisplay", display); }
      if (hasAi) { compound.put("TransformAI", ai); }
      if (hasAdvanced) { compound.put("TransformAdvanced", advanced); }
      if (hasInv) { compound.put("TransformInv", inv); }
      if (hasStats) { compound.put("TransformStats", stats); }
      if (hasRole) { compound.put("TransformRole", role); }
      if (hasJob) { compound.put("TransformJob", job); }
      if (hasAnimations) { compound.put("TransformAnimations", animation); }
      return compound;
   }

   public CompoundTag saveOptions(CompoundTag compound) {
      compound.putBoolean("TransformHasDisplay", hasDisplay);
      compound.putBoolean("TransformHasAI", hasAi);
      compound.putBoolean("TransformHasAdvanced", hasAdvanced);
      compound.putBoolean("TransformHasInv", hasInv);
      compound.putBoolean("TransformHasStats", hasStats);
      compound.putBoolean("TransformHasRole", hasRole);
      compound.putBoolean("TransformHasJob", hasJob);
      compound.putBoolean("TransformEditingModus", editingModus);
      compound.putBoolean("TransformHasAnimations", hasAnimations);
      return compound;
   }

   public void load(CompoundTag compound) {
      isActive = compound.getBoolean("TransformIsActive");
      loadOptions(compound);
      display = hasDisplay ? compound.getCompound("TransformDisplay") : getDisplay();
      ai = hasAi ? compound.getCompound("TransformAI") : npc.ais.save(new CompoundTag());
      advanced = hasAdvanced ? compound.getCompound("TransformAdvanced") : getAdvanced();
      inv = hasInv ? compound.getCompound("TransformInv") : npc.inventory.save(new CompoundTag());
      stats = hasStats ? compound.getCompound("TransformStats") : npc.stats.save(new CompoundTag());
      job = hasJob ? compound.getCompound("TransformJob") : getJob();
      role = hasRole ? compound.getCompound("TransformRole") : getRole();
      animation = (hasAnimations ? compound.getCompound("TransformAnimations") : npc.animation.save(new CompoundTag()));
   }

   public void loadOptions(CompoundTag compound) {
      boolean oldHasDisplay = hasDisplay;
      boolean oldHasAi = hasAi;
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
      if (hasDisplay && !oldHasDisplay) { display = getDisplay(); }
      if (hasAi && !oldHasAi) { ai = npc.ais.save(new CompoundTag()); }
      if (hasStats && !oldHasStats) { stats = npc.stats.save(new CompoundTag()); }
      if (hasInv && !oldHasInv) { inv = npc.inventory.save(new CompoundTag()); }
      if (hasAdvanced && !oldHasAdvanced) { advanced = getAdvanced(); }
      if (hasJob && !oldHasJob) { job = getJob(); }
      if (hasRole && !oldHasRole) { role = getRole(); }
      if (hasAnimations && !oldHasAnimations) { animation = npc.animation.save(new CompoundTag()); }
   }

   public CompoundTag getJob() {
      CompoundTag compound = new CompoundTag();
      compound.putInt("NpcJob", npc.job.getType());
      npc.job.save(compound);
      return compound;
   }

   public CompoundTag getRole() {
      CompoundTag compound = new CompoundTag();
      compound.putInt("Role", npc.role.getType());
      npc.role.save(compound);
      return compound;
   }

   public CompoundTag getDisplay() {
      CompoundTag compound = npc.display.save(new CompoundTag());
      if (npc instanceof EntityCustomNpc cNpc) { compound.put("ModelData", cNpc.modelData.save()); }
      return compound;
   }

   public CompoundTag getAdvanced() {
      JobInterface jopType = npc.job;
      RoleInterface roleType = npc.role;
      npc.job = JobInterface.NONE;
      npc.role = RoleInterface.NONE;
      CompoundTag compound = npc.advanced.save(new CompoundTag());
      npc.job = jopType;
      npc.role = roleType;
      return compound;
   }

   public boolean isValid() {
      return hasAdvanced || hasAi || hasDisplay || hasInv || hasStats || hasJob || hasRole || hasAnimations;
   }

   public CompoundTag processAdvanced(CompoundTag compoundAdv, CompoundTag compoundRole, CompoundTag compoundJob) {
      if (hasAdvanced) { compoundAdv = advanced; }
      if (hasRole) { compoundRole = role; }
      if (hasJob) { compoundJob = job; }
      Set<String> names = compoundRole.getAllKeys();
      for (String name : names) {
         Tag tag = compoundRole.get(name);
         if (tag != null) { compoundAdv.put(name, tag); }
      }
      names = compoundJob.getAllKeys();
      for (String name : names) {
         Tag tag = compoundJob.get(name);
         if (tag != null) { compoundAdv.put(name, tag); }
      }
      return compoundAdv;
   }

   public void transform(boolean isActiveIn) {
      if (isActive != isActiveIn) {
         CompoundTag compoundAdv;
         if (hasDisplay) {
            compoundAdv = getDisplay();
            npc.display.load(NBTTags.nbtMerge(compoundAdv, display));
            if (npc instanceof EntityCustomNpc cNpc) {
               cNpc.modelData.load(NBTTags.nbtMerge(compoundAdv.getCompound("ModelData"), display.getCompound("ModelData")));
            }
            display = compoundAdv;
         }
         if (hasStats) {
            compoundAdv = npc.stats.save(new CompoundTag());
            npc.stats.load(NBTTags.nbtMerge(compoundAdv, stats));
            stats = compoundAdv;
         }
         if (hasAdvanced || hasJob || hasRole) {
            compoundAdv = getAdvanced();
            CompoundTag compoundJob = getJob();
            CompoundTag compoundRole = getRole();
            CompoundTag compound = processAdvanced(compoundAdv, compoundRole, compoundJob);
            npc.advanced.load(compound);
            if (npc.role.getType() != 0) { npc.role.load(NBTTags.nbtMerge(compoundRole, compound)); }
            if (npc.job.getType() != 0) { npc.job.load(NBTTags.nbtMerge(compoundJob, compound)); }
            if (hasAdvanced) { advanced = compoundAdv; }
            if (hasRole) { role = compoundRole; }
            if (hasJob) {job = compoundJob; }
         }
         if (hasAi) {
            compoundAdv = npc.ais.save(new CompoundTag());
            npc.ais.load(NBTTags.nbtMerge(compoundAdv, ai));
            ai = compoundAdv;
            npc.setCurrentAnimation(0);
         }
         if (hasInv) {
            compoundAdv = npc.inventory.save(new CompoundTag());
            npc.inventory.load(NBTTags.nbtMerge(compoundAdv, inv));
            inv = compoundAdv;
         }
         if (hasAnimations) {
            compoundAdv = npc.animation.save(new CompoundTag());
            npc.animation.load(NBTTags.nbtMerge(compoundAdv, animation));
            animation = compoundAdv;
         }
         npc.updateAI = true;
         isActive = isActiveIn;
         npc.updateClient = true;
      }
   }

}
