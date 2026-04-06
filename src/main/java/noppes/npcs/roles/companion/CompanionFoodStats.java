package noppes.npcs.roles.companion;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.entity.EntityNPCInterface;

public class CompanionFoodStats {

   private int foodLevel = 20;
   private float foodSaturationLevel = 5.0F;
   private float foodExhaustionLevel;
   private int foodTimer;
   private int prevFoodLevel = 20;

   private void addStats(int foodLevel, float saturationLevel) {
      this.foodLevel = Math.min(foodLevel + this.foodLevel, 20);
      this.foodSaturationLevel = Math.min(this.foodSaturationLevel + (float)foodLevel * saturationLevel * 2.0F, (float)this.foodLevel);
   }

   @SuppressWarnings("all")
   public void onFoodEaten(FoodProperties food, ItemStack itemstack) {
      this.addStats(food.getNutrition(), food.getSaturationModifier());
   }

   public void onUpdate(EntityNPCInterface npc) {
      Difficulty enumdifficulty = npc.level().getDifficulty();
      this.prevFoodLevel = this.foodLevel;
      if (this.foodExhaustionLevel > 4.0F) {
         this.foodExhaustionLevel -= 4.0F;
         if (this.foodSaturationLevel > 0.0F) {
            this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
         } else if (enumdifficulty != Difficulty.PEACEFUL) {
            this.foodLevel = Math.max(this.foodLevel - 1, 0);
         }
      }

      if (npc.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION) && this.foodLevel >= 18 && npc.getHealth() > 0.0F && npc.getHealth() < npc.getMaxHealth()) {
         ++this.foodTimer;
         if (this.foodTimer >= 80) {
            npc.heal(1.0F);
            this.addExhaustion(3.0F);
            this.foodTimer = 0;
         }
      } else if (this.foodLevel <= 0) {
         ++this.foodTimer;
         if (this.foodTimer >= 80) {
            if (npc.getHealth() > 10.0F || enumdifficulty == Difficulty.HARD || npc.getHealth() > 1.0F && enumdifficulty == Difficulty.NORMAL) {
               npc.hurt(npc.damageSources().starve(), 1.0F);
            }

            this.foodTimer = 0;
         }
      } else {
         this.foodTimer = 0;
      }

   }

   public void load(CompoundTag compound) {
      if (compound.contains("foodLevel", 99)) {
         this.foodLevel = compound.getInt("foodLevel");
         this.foodTimer = compound.getInt("foodTickTimer");
         this.foodSaturationLevel = compound.getFloat("foodSaturationLevel");
         this.foodExhaustionLevel = compound.getFloat("foodExhaustionLevel");
      }

   }

   public void save(CompoundTag compound) {
      compound.putInt("foodLevel", this.foodLevel);
      compound.putInt("foodTickTimer", this.foodTimer);
      compound.putFloat("foodSaturationLevel", this.foodSaturationLevel);
      compound.putFloat("foodExhaustionLevel", this.foodExhaustionLevel);
   }

   public int getFoodLevel() {
      return this.foodLevel;
   }

   @SuppressWarnings("all")
   @OnlyIn(Dist.CLIENT)
   public int getPrevFoodLevel() {
      return this.prevFoodLevel;
   }

   public boolean needFood() {
      return this.foodLevel < 20;
   }

   public void addExhaustion(float exhaustionLevel) {
      this.foodExhaustionLevel = Math.min(this.foodExhaustionLevel + exhaustionLevel, 40.0F);
   }

   public float getSaturationLevel() {
      return this.foodSaturationLevel;
   }

   @OnlyIn(Dist.CLIENT)
   public void setFoodLevel(int foodLevel) {
      this.foodLevel = foodLevel;
   }

   @SuppressWarnings("all")
   @OnlyIn(Dist.CLIENT)
   public void setFoodSaturationLevel(float saturationLevel) {
      this.foodSaturationLevel = saturationLevel;
   }

}
