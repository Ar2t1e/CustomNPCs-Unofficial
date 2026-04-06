package noppes.npcs;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import noppes.npcs.entity.EntityNPCInterface;

public class NpcDamageSource extends DamageSource {

   private static final ResourceKey<DamageType> NPC = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CustomNpcs.MODID, "npc"));
   private static Holder<DamageType> type = null;

   public static NpcDamageSource create(EntityNPCInterface npc) {
      if (type == null) { type = npc.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(NPC); }
      return new NpcDamageSource(type, npc);
   }

   private NpcDamageSource(Holder<DamageType> damageType, Entity source) {
      super(damageType, source);
   }

   @Override
   public boolean scalesWithDifficulty() { return false; } // None scaling based on difficulty

}
