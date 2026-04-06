package noppes.npcs.api.constants;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public enum ParticleType {

   NONE(0),
   SMOKE(1),
   PORTAL(2),
   REDSTONE(3),
   LIGHTNING(4),
   LARGE_SMOKE(5),
   MAGIC(6),
   ENCHANT(7),
   CRIT(8);

   public static ParticleOptions getMCType(int type) {
      return switch (type) {
         case 1 -> ParticleTypes.SMOKE;
         case 2 -> ParticleTypes.PORTAL;
         case 3 -> new ParticleType.RedstoneParticleType();
         case 4 -> ParticleTypes.ENCHANTED_HIT;
         case 5 -> ParticleTypes.LARGE_SMOKE;
         case 6 -> ParticleTypes.WITCH;
         case 7 -> ParticleTypes.ENCHANT;
         case 8 -> ParticleTypes.CRIT;
         default -> null;
      };
   }

   static class RedstoneParticleType extends DustParticleOptions {

      protected RedstoneParticleType() {
         super(DustParticleOptions.REDSTONE_PARTICLE_COLOR, 1.0F);
      }

      public void writeToNetwork(@NotNull FriendlyByteBuf buffer) { }

      public @NotNull String writeToString() {
         ResourceLocation registerName = ForgeRegistries.PARTICLE_TYPES.getKey(ParticleTypes.DUST);
         return registerName != null ? registerName.toString() : "null";
      }

   }

   final int type;

   ParticleType(int t) { type = t; }

   public int get() { return type; }

}
