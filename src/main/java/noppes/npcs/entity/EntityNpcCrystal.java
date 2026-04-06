package noppes.npcs.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomEntities;
import noppes.npcs.CustomNpcs;

public class EntityNpcCrystal extends EntityNPCInterface {

   public EntityNpcCrystal(EntityType<? extends EntityNPCInterface> type, Level world) {
      super(type, world);
      this.scaleX = 0.7F;
      this.scaleY = 0.7F;
      this.scaleZ = 0.7F;
      this.display.setSkinTexture(CustomNpcs.MODID + ":textures/entity/crystal/endercrystal.png");
   }

   public void tick() {
      this.discard();
      this.setNoAi(true);
      if (!this.level().isClientSide) {
         CompoundTag compound = new CompoundTag();
         this.addAdditionalSaveData(compound);
         EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, this.level());
         npc.readAdditionalSaveData(compound);
         npc.modelData.setEntity(ForgeRegistries.ENTITY_TYPES.getKey(CustomEntities.entityNpcCrystal));
         this.level().addFreshEntity(npc);
      }
      super.tick();
   }

}
