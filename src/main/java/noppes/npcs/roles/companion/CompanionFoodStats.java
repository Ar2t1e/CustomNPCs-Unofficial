package noppes.npcs.roles.companion;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.entity.EntityNPCInterface;

public class CompanionFoodStats {

	private float foodExhaustionLevel;
	private int foodLevel = 20;
	private float foodSaturationLevel = 5.0f;
	private int foodTimer;
	private int prevFoodLevel = 20;

	public void addExhaustion(float exhaustion) {
		foodExhaustionLevel = Math.min(foodExhaustionLevel + exhaustion, 40.0f);
	}

	private void addStats(int foodLevelIn, float foodSaturationModifier) {
		foodLevel = Math.min(foodLevelIn + foodLevel, 20);
		foodSaturationLevel = Math.min(foodSaturationLevel + foodLevelIn * foodSaturationModifier * 2.0f, foodLevel);
	}

	public int getFoodLevel() { return foodLevel; }

	@SideOnly(Side.CLIENT)
	public int getPrevFoodLevel() {return prevFoodLevel; }

	public boolean needFood() { return foodLevel < 20; }

	public void onFoodEaten(ItemFood food, ItemStack itemstack) { addStats(food.getHealAmount(itemstack), food.getSaturationModifier(itemstack)); }

	public void onUpdate(EntityNPCInterface npc) {
		EnumDifficulty enumdifficulty = npc.world.getDifficulty();
		prevFoodLevel = foodLevel;
		if (foodExhaustionLevel > 4.0f) {
			foodExhaustionLevel -= 4.0f;
			if (foodSaturationLevel > 0.0f) { foodSaturationLevel = Math.max(foodSaturationLevel - 1.0f, 0.0f); }
			else if (enumdifficulty != EnumDifficulty.PEACEFUL) { foodLevel = Math.max(foodLevel - 1, 0); }
		}
		if (npc.world.getGameRules().getBoolean("naturalRegeneration") && foodLevel >= 18 && npc.getHealth() > 0.0f
				&& npc.getHealth() < npc.getMaxHealth()) {
			++foodTimer;
			if (foodTimer >= 80) {
				npc.heal(1.0f);
				addExhaustion(3.0f);
				foodTimer = 0;
			}
		} else if (foodLevel <= 0) {
			++foodTimer;
			if (foodTimer >= 80) {
				if (npc.getHealth() > 10.0f || enumdifficulty == EnumDifficulty.HARD
						|| (npc.getHealth() > 1.0f && enumdifficulty == EnumDifficulty.NORMAL)) {
					npc.attackEntityFrom(DamageSource.STARVE, 1.0f);
				}
				foodTimer = 0;
			}
		}
		else { foodTimer = 0; }
	}

	public void load(NBTTagCompound compound) {
		if (compound.hasKey("foodLevel", 99)) {
			foodLevel = compound.getInteger("foodLevel");
			foodTimer = compound.getInteger("foodTickTimer");
			foodSaturationLevel = compound.getFloat("foodSaturationLevel");
			foodExhaustionLevel = compound.getFloat("foodExhaustionLevel");
		}
	}

	@SideOnly(Side.CLIENT)
	public void setFoodLevel(int foodLevelIn) { foodLevel = foodLevelIn; }

	@SideOnly(Side.CLIENT)
	public void setFoodSaturationLevel(float foodSaturationLevelIn) {
		foodSaturationLevel = foodSaturationLevelIn;
	}

	public void save(NBTTagCompound compound) {
		compound.setInteger("foodLevel", foodLevel);
		compound.setInteger("foodTickTimer", foodTimer);
		compound.setFloat("foodSaturationLevel", foodSaturationLevel);
		compound.setFloat("foodExhaustionLevel", foodExhaustionLevel);
	}

}
