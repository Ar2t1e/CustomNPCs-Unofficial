package noppes.npcs.api.constants;

import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;

public enum PotionEffectType {

	NONE(0),
	FIRE(1),
	POISON(2),
	HUNGER(3),
	WEAKNESS(4),
	SLOWNESS(5),
	NAUSEA(6),
	BLINDNESS(7),
	WITHER(8),
	SPEED(9),
	HASTE(10),
	MINING_FATIGUE(11),
	STRENGTH(12),
	INSTANT_HEALTH(13),
	INSTANT_DAMAGE(14),
	JUMP_BOOST(15),
	REGENERATION(16),
	RESISTANCE(17),
	FIRE_RESISTANCE(18),
	WATER_BREATHING(19),
	INVISIBILITY(20),
	NIGHT_VISION(21),
	HEALTH_BOOST(22),
	ABSORPTION(23),
	SATURATION(24),
	GLOWING(25),
	LEVITATION(26),
	LUCK(27),
	UNLUCK(28);

	public static Potion getMCType(int effect) {
		switch (effect) {
			case 2: return MobEffects.POISON;
			case 3: return MobEffects.HUNGER;
			case 4: return MobEffects.WEAKNESS;
			case 5: return MobEffects.SLOWNESS;
			case 6: return MobEffects.NAUSEA;
			case 7: return MobEffects.BLINDNESS;
			case 8: return MobEffects.WITHER;
			case 9: return MobEffects.SPEED;
			case 10: return MobEffects.HASTE;
			case 11: return MobEffects.MINING_FATIGUE;
			case 12: return MobEffects.STRENGTH;
			case 13: return MobEffects.INSTANT_HEALTH;
			case 14: return MobEffects.INSTANT_DAMAGE;
			case 15: return MobEffects.JUMP_BOOST;
			case 16: return MobEffects.REGENERATION;
			case 17: return MobEffects.RESISTANCE;
			case 18: return MobEffects.FIRE_RESISTANCE;
			case 19: return MobEffects.WATER_BREATHING;
			case 20: return MobEffects.INVISIBILITY;
			case 21: return MobEffects.NIGHT_VISION;
			case 22: return MobEffects.HEALTH_BOOST;
			case 23: return MobEffects.ABSORPTION;
			case 24: return MobEffects.SATURATION;
			case 25: return MobEffects.GLOWING;
			case 26: return MobEffects.LEVITATION;
			case 27: return MobEffects.LUCK;
			case 28: return MobEffects.UNLUCK;
			default: return null;
		}
	}

	final int type;

	PotionEffectType(int t) { type = t; }

	public int get() { return type; }

}
