package noppes.npcs.entity.data;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.entity.data.INPCMelee;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;

public class DataMelee implements INPCMelee {

	private static final int version = 1;
	private final @Nonnull EntityNPCInterface npc;
	private double attackRange = 2.0d;
	private int attackSpeed = 20;
	private int attackStrength = 5;
	private int knockback = 0;
	private int potionAmp = 0;
	private int potionDuration = 5;
	private int potionType = 0;

	public DataMelee(@Nonnull EntityNPCInterface npcIn) { npc = npcIn; }

	public void load(NBTTagCompound compound) {
		attackSpeed = compound.getInteger("AttackSpeed");
		setStrength(compound.getInteger("AttackStrenght"));
		knockback = compound.getInteger("KnockBack");
		potionType = compound.getInteger("PotionEffect");
		potionDuration = compound.getInteger("PotionDuration");
		potionAmp = compound.getInteger("PotionAmp");

		// New from Unofficial (BetaZavr)
		if (compound.hasKey("AttackRange", 3)) { attackRange = compound.getInteger("AttackRange"); }
		else { attackRange = compound.getDouble("AttackRange"); }
		if (version != compound.getInteger("version")) {
			int v = compound.getInteger("version");
			if (v < 1) { knockback++; }
		}
	}

	public NBTTagCompound save(NBTTagCompound compound) {
		compound.setInteger("AttackStrenght", attackStrength);
		compound.setInteger("AttackSpeed", attackSpeed);
		compound.setInteger("KnockBack", knockback);
		compound.setInteger("PotionEffect", potionType);
		compound.setInteger("PotionDuration", potionDuration);
		compound.setInteger("PotionAmp", potionAmp);

		// New from Unofficial (BetaZavr)
		compound.setDouble("AttackRange", attackRange);
		compound.setInteger("version", version);
		return compound;
	}

	@Override
	public int getDelay() { return attackSpeed; }

	@Override
	public int getEffectStrength() { return potionAmp; }

	@Override
	public int getEffectTime() { return potionDuration; }

	@Override
	public int getEffectType() { return potionType; }

	@Override
	public int getKnockback() { return knockback; }

	@Override
	public double getRange() { return attackRange; }

	@Override
	public int getStrength() { return attackStrength; }

	@Override
	public void setDelay(int speed) { attackSpeed = speed; }

	@Override
	public void setEffect(int type, int strength, int time) {
		potionType = type;
		potionDuration = time;
		potionAmp = strength;
	}

	@Override
	public void setKnockback(int knockbackIn) { knockback = knockbackIn; }

	@Override
	public void setRange(double range) { attackRange = ValueUtil.correctDouble(range, 0.2d, 30.0d); }

	@Override
	public void setStrength(int strength) {
		attackStrength = strength;
		npc.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(attackStrength);
	}

	// New from Unofficial (BetaZavr)
	public int getDelayRNG() {
		int delay = attackSpeed;
		if (attackSpeed < 120 && attackSpeed > 10) {
			delay += npc.world.rand.nextInt((int) ((double) attackSpeed * 0.15d));
		}
		return delay;
	}

}
