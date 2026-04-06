package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.NBTTags;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.entity.data.role.IJobGuard;
import noppes.npcs.entity.EntityNPCInterface;

public class JobGuard extends JobInterface implements IJobGuard {

   /**
    * Entity description IDs
    */
   public final List<String> targets = new ArrayList<>();

   public JobGuard(EntityNPCInterface npc) {
      super(npc);
      type = JobType.GUARD;
   }

   public boolean isEntityApplicable(Entity entity) {
      return !(entity instanceof Player) && !(entity instanceof EntityNPCInterface) && targets.contains(entity.getType().getDescriptionId());
   }

   @Override
   public CompoundTag save(CompoundTag compound) {
      super.save(compound);
      compound.put("GuardTargets", NBTTags.nbtStringList(targets));
      return compound;
   }

   @Override
   public void load(CompoundTag compound) {
      super.load(compound);
      type = JobType.GUARD;
      targets.clear();
      targets.addAll(NBTTags.getStringList(compound.getList("GuardTargets", 10)));
      // OLD loads
      if (npc != null) {
         if (compound.getBoolean("GuardAttackAnimals")) {
            for (EntityType<?> ent : ForgeRegistries.ENTITY_TYPES.getValues()) {
               String name = ent.getDescriptionId();
               Entity entityO = ent.create(npc.level());
               if (entityO != null && entityO.getClass().isAssignableFrom(Animal.class) && !targets.contains(name)) {
                  targets.add(name);
               }
            }
         }
         if (compound.getBoolean("GuardAttackMobs")) {
            for (EntityType<?> ent : ForgeRegistries.ENTITY_TYPES.getValues()) {
               String name = ent.getDescriptionId();
               Entity entityO = ent.create(npc.level());
               if (entityO != null && entityO.getClass().isAssignableFrom(Mob.class) &&
                       !ent.getClass().isAssignableFrom(Creeper.class) &&
                       !targets.contains(name)) {
                  targets.add(name);
               }
            }
         }
         if (compound.getBoolean("GuardAttackCreepers")) {
            for (EntityType<?> ent : ForgeRegistries.ENTITY_TYPES.getValues()) {
               String name = ent.getDescriptionId();
               Entity entityO = ent.create(npc.level());
               if (entityO != null && entityO.getClass().isAssignableFrom(Creeper.class) && !targets.contains(name)) {
                  targets.add(name);
               }
            }
         }
      }
   }

   // New from Unofficial (BetaZavr)
   @Override
   public boolean isWorking() { return !targets.isEmpty() && npc != null && npc.isAttacking(); }

   @Override
   public String[] getTargets() { return targets.toArray(new String[0]); }

   @Override
   public void setTargets(String... targetsIn) {
      targets.clear();
      targets.addAll(Arrays.asList(targetsIn));
   }

}
