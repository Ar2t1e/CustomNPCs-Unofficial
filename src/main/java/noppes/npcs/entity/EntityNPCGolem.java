package noppes.npcs.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomEntities;
import noppes.npcs.CustomNpcs;
import org.jetbrains.annotations.NotNull;

public class EntityNPCGolem extends EntityNPCInterface {

   public EntityNPCGolem(EntityType<? extends EntityNPCInterface> type, Level world) {
      super(type, world);
      this.display.setSkinTexture(CustomNpcs.MODID + ":textures/entity/golem/irongolem.png");
      this.baseSize = new EntityDimensions(1.4F, 2.5F, false);
   }

   public @NotNull EntityDimensions getDimensions(@NotNull Pose pos) {
      this.currentAnimation = this.entityData.get(Animation);
      if (this.currentAnimation == 2) {
         return new EntityDimensions(0.5F, 0.5F, false);
      } else {
         return this.currentAnimation == 1 ? new EntityDimensions(1.4F, 2.0F, false) : new EntityDimensions(1.4F, 2.5F, false);
      }
   }

   public void tick() {
      this.discard();
      this.setNoAi(true);
      if (!this.level().isClientSide) {
         CompoundTag compound = new CompoundTag();
         this.addAdditionalSaveData(compound);
         EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, this.level());
         npc.readAdditionalSaveData(compound);
         npc.modelData.setEntity(ForgeRegistries.ENTITY_TYPES.getKey(CustomEntities.entityNPCGolem));
         this.level().addFreshEntity(npc);
      }

      super.tick();
   }
}
