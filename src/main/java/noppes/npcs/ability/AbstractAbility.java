package noppes.npcs.ability;

import net.minecraft.world.entity.LivingEntity;
import noppes.npcs.constants.EnumAbilityType;
import noppes.npcs.entity.EntityNPCInterface;

public abstract class AbstractAbility implements IAbility {

   private long cooldown = 0L;
   protected EntityNPCInterface npc;
   public float maxHP = 1.0F;
   public float minHP = 0.0F;

   public AbstractAbility(EntityNPCInterface npcIn) { npc = npcIn; }

   private boolean onCooldown() { return System.currentTimeMillis() < cooldown; }

   public int getRNG() { return 0; }

   public boolean canRun(LivingEntity target) {
      if (onCooldown()) { return false; }
      float f = npc.getHealth() / npc.getMaxHealth();
      if (!(f < minHP) && !(f > maxHP)) {
         return (getRNG() <= 1 || npc.getRandom().nextInt(getRNG()) == 0) && npc.canSee(target);
      }
      return false;
   }

   public void endAbility() {
       cooldown = System.currentTimeMillis() + (long) npc.ais.getMaxHurtResistantTime() * 1000L;
   }

   public abstract boolean isType(EnumAbilityType var1);

   public void startCombat() {
       cooldown = System.currentTimeMillis() + (long) npc.ais.getMaxHurtResistantTime() * 1000L;
   }

}
