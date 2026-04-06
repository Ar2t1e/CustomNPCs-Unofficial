package noppes.npcs.entity.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import noppes.npcs.api.entity.data.INPCMelee;
import noppes.npcs.entity.EntityNPCInterface;

import javax.annotation.Nonnull;
import java.util.Objects;

public class DataMelee implements INPCMelee {

   private static final int version = 1;
   private final @Nonnull EntityNPCInterface npc;
   private double attackRange = 2.0d;
   private int attackStrength = 5;
   private int attackSpeed = 20;
   private int knockback = 0;
   private int potionType = 0;
   private int potionDuration = 5;
   private int potionAmp = 0;

   public DataMelee(@Nonnull EntityNPCInterface npcIn) {
      npc = npcIn;
   }

   public void load(CompoundTag compound) {
      attackSpeed = compound.getInt("AttackSpeed");
      setStrength(compound.getInt("AttackStrenght"));
      knockback = compound.getInt("KnockBack");
      potionType = compound.getInt("PotionEffect");
      potionDuration = compound.getInt("PotionDuration");
      potionAmp = compound.getInt("PotionAmp");

      // New from Unofficial (BetaZavr)
      if (compound.contains("AttackRange", 3)) { attackRange = compound.getInt("AttackRange"); }
      else { attackRange = compound.getDouble("AttackRange"); }
      if (version != compound.getInt("version")) {
         int v = compound.getInt("version");
         if (v < 1) { knockback++; }
      }
   }

   public CompoundTag save(CompoundTag compound) {
      compound.putInt("AttackStrenght", attackStrength);
      compound.putInt("AttackSpeed", attackSpeed);
      compound.putInt("KnockBack", knockback);
      compound.putInt("PotionEffect", potionType);
      compound.putInt("PotionDuration", potionDuration);
      compound.putInt("PotionAmp", potionAmp);

      // New from Unofficial (BetaZavr)
      compound.putDouble("AttackRange", attackRange);
      compound.putInt("version", version);
      return compound;
   }

   @Override
   public int getStrength() { return attackStrength; }

   @Override
   public void setStrength(int strength) {
      attackStrength = strength;
      AttributeInstance attribute = npc.getAttribute(Attributes.ATTACK_DAMAGE);
      if (attribute != null) { attribute.setBaseValue(attackStrength); }
   }

   @Override
   public int getDelay() { return attackSpeed; }

   @Override
   public void setDelay(int speed) { attackSpeed = speed; }

   @Override
   public double getRange() { return attackRange; }

   @Override
   public void setRange(double range) { attackRange = range; }

   @Override
   public int getKnockback() { return knockback; }

   @Override
   public void setKnockback(int knockbackIn) { knockback = knockbackIn; }

   @Override
   public int getEffectType() { return potionType; }

   @Override
   public int getEffectTime() { return potionDuration; }

   @Override
   public int getEffectStrength() { return potionAmp; }

   @Override
   public void setEffect(int type, int strength, int time) {
      potionType = type;
      potionDuration = time;
      potionAmp = strength;
   }

   // New from Unofficial (BetaZavr)
   public int getDelayRNG() {
      int delay = attackSpeed;
      if (attackSpeed < 120 && attackSpeed > 10) {
         delay += npc.getRandom().nextInt((int) ((double) attackSpeed * 0.15d));
      }
      return delay;
   }

}
