package noppes.npcs.roles;

import java.util.*;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import noppes.npcs.NBTTags;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.entity.data.role.IHealerEffect;
import noppes.npcs.api.entity.data.role.IJobHealer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.data.HealerSettings;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.ValueUtil;

public class JobHealer extends JobInterface implements IJobHealer {

   // New from Unofficial (BetaZavr)
   private final Map<Integer, List<LivingEntity>> affected = new HashMap<>();
   private final Random rnd = new Random();
   public Map<Integer, HealerSettings> effects = new HashMap<>(); // [effect ID, settings]

   public JobHealer(EntityNPCInterface npc) {
      super(npc);
      type = JobType.HEALER;
   }

   @Override
   public CompoundTag save(CompoundTag compound) {
      super.save(compound);
      ListTag list = new ListTag();
      for (HealerSettings hs : effects.values()) { list.add(hs.save()); }
      compound.put("HealerData", list);
      return compound;
   }

   @Override
   public void load(CompoundTag compound) {
      super.load(compound);
      type = JobType.HEALER;
      effects.clear();
      if (compound.contains("HealerData", 9)) {
         for (int i = 0; i < compound.getList("HealerData", 10).size(); i++) {
            HealerSettings hs = new HealerSettings(compound.getList("HealerData", 10).getCompound(i));
            effects.put(hs.id, hs);
         }
      }
      else if (compound.contains("HealerRange", 3)) { // OLD
         int range = compound.getInt("HealerRange");
         int speed = ValueUtil.correctInt(compound.getInt("HealerSpeed"), 10, Integer.MAX_VALUE);
         byte type = compound.getByte("HealerType");
         HashMap<Integer, Integer> oldMap = NBTTags.getIntegerIntegerMap(compound.getList("BeaconEffects", 10));
         for (int id : oldMap.keySet()) {
            HealerSettings hs = new HealerSettings(id, range, speed, oldMap.get(id), type);
            effects.put(hs.id, hs);
         }
      }
   }

   @Override
   public boolean aiShouldExecute() {
      boolean shouldExecute = false;
      affected.clear();
      for (Integer id : effects.keySet()) {
         if (npc != null && npc.totalTicksAlive % effects.get(id).speed < 3) {
            shouldExecute = true;
            int r = effects.get(id).range;
            List<LivingEntity> list = new ArrayList<>();
            try { list = npc.level().getEntitiesOfClass(LivingEntity.class, npc.getBoundingBox().inflate(r, r / 2.0d, r)); }
            catch (Exception ignored) { }
            affected.put(id, list);
            if (!effects.get(id).onHimself) { affected.get(id).remove(npc); }
         }
      }
      return shouldExecute;
   }

   @Override
   public boolean aiContinueExecute() { return false; }

   @Override
   public void aiStartExecuting() {
      boolean activated = false;
      for (Integer id : affected.keySet()) {
         MobEffect potion = MobEffect.byId(id);
         if (potion == null) { continue; }
         HealerSettings hs = effects.get(id);
         if (!hs.isMassive) {
            if (affected.get(id).isEmpty()) { continue; }
            LivingEntity entity = null;
            try {
               List<LivingEntity> filteredEntities = affected.get(id).stream()
                       .filter(e -> e.getActiveEffects().stream().noneMatch(effect -> effect.getEffect() == potion))
                       .toList();
               if (!filteredEntities.isEmpty()) { entity = filteredEntities.get(rnd.nextInt(filteredEntities.size())); }
            }
            catch (Exception e) { LogWriter.error(e); }
            if (entity != null) {
               boolean isEnemy = isEnemy(entity);
               if (hs.type == 2 || (hs.type == 0 && !isEnemy) || (hs.type == 1 && isEnemy)) {
                  entity.addEffect(new MobEffectInstance(potion, hs.time, hs.amplifier));
                  activated = true;
                  if (npc != null && entity != npc) {
                     npc.getLookControl().setLookAt(entity, 10.0F, (float) npc.getMaxHeadXRot());
                  }
               }
            }
         }
         else {
            for (LivingEntity entity : affected.get(id)) {
               if ((entity instanceof Mob || entity instanceof Animal) && !hs.possibleOnMobs) { continue; }
               boolean isEnemy = isEnemy(entity);
               if (hs.type == 2 || (hs.type == 0 && !isEnemy) || (hs.type == 1 && isEnemy)) {
                  entity.addEffect(new MobEffectInstance(potion, hs.time, hs.amplifier));
                  activated = true;
               }
            }
         }
      }
      affected.clear();
      if (npc != null && activated) {
         if (!npc.getMainHandItem().isEmpty()) { npc.swing(InteractionHand.MAIN_HAND); }
         else { npc.swing(InteractionHand.OFF_HAND); }
      }
   }

   // New from Unofficial (BetaZavr)
   private boolean isEnemy(LivingEntity entity) {
      if (npc != null) {
         if (entity instanceof Player) { return npc.faction.isAggressiveToPlayer((Player) entity); }
         else if (entity instanceof EntityNPCInterface) { return npc.faction.isAggressiveToNpc((EntityNPCInterface) entity); }
      }
      return (entity instanceof Mob);
   }

   @Override
   public IHealerEffect[] getEffects() { return effects.values().toArray(new IHealerEffect[0]); }

   @Override
   public boolean removeEffect(int effectId) { return effects.remove(effectId) != null; }

   @Override
   public IHealerEffect addEffect(int effectId, int range, int speed, int amplifier, int type) {
      if (MobEffect.byId(effectId) == null) { return null; }
      if (!effects.containsKey(effectId)) { effects.put(effectId, new HealerSettings(effectId, range, speed, amplifier, (byte) type)); }
      return effects.get(effectId);
   }

}
