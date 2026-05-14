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
      foodLevel = Math.min(foodLevel + foodLevel, 20);
      foodSaturationLevel = Math.min(foodSaturationLevel + (float)foodLevel * saturationLevel * 2.0F, (float)foodLevel);
   }

   public void onFoodEaten(FoodProperties food, ItemStack ignoredItemstack) {
      addStats(food.getNutrition(), food.getSaturationModifier());
   }

   public void onUpdate(EntityNPCInterface npc) {
      Difficulty enumdifficulty = npc.level().getDifficulty();
      prevFoodLevel = foodLevel;
      if (foodExhaustionLevel > 4.0F) {
         foodExhaustionLevel -= 4.0F;
         if (foodSaturationLevel > 0.0F) { foodSaturationLevel = Math.max(foodSaturationLevel - 1.0F, 0.0F); }
         else if (enumdifficulty != Difficulty.PEACEFUL) { foodLevel = Math.max(foodLevel - 1, 0); }
      }
      if (npc.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION) && foodLevel >= 18 && npc.getHealth() > 0.0F && npc.getHealth() < npc.getMaxHealth()) {
         ++foodTimer;
         if (foodTimer >= 80) {
            npc.heal(1.0F);
            addExhaustion(3.0F);
            foodTimer = 0;
         }
      }
      else if (foodLevel <= 0) {
         ++foodTimer;
         if (foodTimer >= 80) {
            if (npc.getHealth() > 10.0F || enumdifficulty == Difficulty.HARD || npc.getHealth() > 1.0F && enumdifficulty == Difficulty.NORMAL) {
               npc.hurt(npc.damageSources().starve(), 1.0F);
            }
            foodTimer = 0;
         }
      }
      else { foodTimer = 0; }
   }

   public void load(CompoundTag compound) {
      if (compound.contains("foodLevel", 99)) {
         foodLevel = compound.getInt("foodLevel");
         foodTimer = compound.getInt("foodTickTimer");
         foodSaturationLevel = compound.getFloat("foodSaturationLevel");
         foodExhaustionLevel = compound.getFloat("foodExhaustionLevel");
      }
   }

   public void save(CompoundTag compound) {
      compound.putInt("foodLevel", foodLevel);
      compound.putInt("foodTickTimer", foodTimer);
      compound.putFloat("foodSaturationLevel", foodSaturationLevel);
      compound.putFloat("foodExhaustionLevel", foodExhaustionLevel);
   }

   public int getFoodLevel() { return foodLevel; }

   @SuppressWarnings("unused")
   @OnlyIn(Dist.CLIENT)
   public int getPrevFoodLevel() { return prevFoodLevel; }

   public boolean needFood() { return foodLevel < 20; }

   public void addExhaustion(float exhaustionLevel) { foodExhaustionLevel = Math.min(foodExhaustionLevel + exhaustionLevel, 40.0F); }

   public float getSaturationLevel() { return foodSaturationLevel; }

   @OnlyIn(Dist.CLIENT)
   public void setFoodLevel(int foodLevelIn) { foodLevel = foodLevelIn; }

   @SuppressWarnings("unused")
   @OnlyIn(Dist.CLIENT)
   public void setFoodSaturationLevel(float saturationLevel) { foodSaturationLevel = saturationLevel; }

}
