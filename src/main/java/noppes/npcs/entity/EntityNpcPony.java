package noppes.npcs.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomEntities;
import noppes.npcs.CustomNpcs;

public class EntityNpcPony extends EntityNPCInterface {

   public boolean isPegasus = false;
   public boolean isUnicorn = false;
   public ResourceLocation checked = null;

   public EntityNpcPony(EntityType<? extends EntityNPCInterface> type, Level world) {
      super(type, world);
      this.display.setSkinTexture(CustomNpcs.MODID + ":textures/entity/ponies/minelpderpyhooves.png");
   }

   public void tick() {
      this.discard();
      this.setNoAi(true);
      if (!this.level().isClientSide) {
         CompoundTag compound = new CompoundTag();
         this.addAdditionalSaveData(compound);
         EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, this.level());
         npc.readAdditionalSaveData(compound);
         npc.modelData.setEntity(ForgeRegistries.ENTITY_TYPES.getKey(CustomEntities.entityNpcPony));
         this.level().addFreshEntity(npc);
      }
      super.tick();
   }

}
