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

public class EntityNpcSlime extends EntityNPCInterface {

   public EntityNpcSlime(EntityType<? extends EntityNPCInterface> type, Level world) {
      super(type, world);
      this.scaleX = 2.0F;
      this.scaleY = 2.0F;
      this.scaleZ = 2.0F;
      this.display.setSkinTexture(CustomNpcs.MODID + ":textures/entity/slime/slime.png");
      this.baseSize = new EntityDimensions(0.8F, 0.8F, false);
   }

   public @NotNull EntityDimensions getDimensions(@NotNull Pose pos) {
      return new EntityDimensions(0.8F, 0.8F, false);
   }

   public void tick() {
      this.discard();
      this.setNoAi(true);
      if (!this.level().isClientSide) {
         CompoundTag compound = new CompoundTag();
         this.addAdditionalSaveData(compound);
         EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, this.level());
         npc.readAdditionalSaveData(compound);
         npc.modelData.setEntity(ForgeRegistries.ENTITY_TYPES.getKey(CustomEntities.entityNpcSlime));
         this.level().addFreshEntity(npc);
      }
      super.tick();
   }

}
