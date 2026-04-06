package noppes.npcs.api.constants;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

public enum PotionEffectType {

   NONE(0),
   SPEED(1),
   SLOWNESS(2),
   HASTE(3),
   MINING_FATIGUE(4),
   STRENGTH(5),
   INSTANT_HEALTH(6),
   INSTANT_DAMAGE(7),
   JUMP_BOOST(8),
   NAUSEA(9),
   REGENERATION(10),
   RESISTANCE(11),
   FIRE_RESISTANCE(12),
   WATER_BREATHING(13),
   INVISIBILITY(14),
   BLINDNESS(15),
   NIGHT_VISION(16),
   HUNGER(17),
   WEAKNESS(18),
   POISON(19),
   WITHER(20),
   HEALTH_BOOST(21),
   ABSORPTION(22),
   SATURATION(23),
   GLOWING(24),
   LEVITATION(25),
   LUCK(26),
   UNLUCK(27),
   SLOW_FALLING(28),
   CONDUIT_POWER(29),
   DOLPHINS_GRACE(30),
   BAD_OMEN(31),
   HERO_OF_THE_VILLAGE(32),
   FIRE(666);

   public static MobEffect getMCType(int effect) {
      return effect != 0 && effect != 666 ? BuiltInRegistries.MOB_EFFECT.byId(effect) : null;
   }

   final int type;

   PotionEffectType(int t) { type = t; }

   public int get() { return type; }

}
