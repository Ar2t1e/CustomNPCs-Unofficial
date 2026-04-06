package noppes.npcs.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomEntities;
import noppes.npcs.CustomNpcs;
import org.jetbrains.annotations.NotNull;

public class EntityNpcDragon extends EntityNPCInterface {

   private final EntityDimensions size = new EntityDimensions(1.8F, 1.4F, false);
   public double[][] field_40162_d = new double[64][3];
   public int field_40164_e = -1;
   public float prevAnimTime = 0.0F;
   public float animTime = 0.0F;
   private boolean exploded = false;

   public EntityNpcDragon(EntityType<? extends EntityNPCInterface> type, Level world) {
      super(type, world);
      scaleX = 0.4F;
      scaleY = 0.4F;
      scaleZ = 0.4F;
      display.setSkinTexture(CustomNpcs.MODID + ":textures/entity/dragon/blackdragon.png");
   }

   public double getPassengersRidingOffset() {
      return 1.1D;
   }

   public double[] getMovementOffsets(int i, float f) {
      f = 1.0F - f;
      int j = field_40164_e - i & 63;
      int k = field_40164_e - i - 1 & 63;
      double[] ad = new double[3];
      double d = field_40162_d[j][0];

      double d1 = field_40162_d[k][0] - d;
      while (d1 < -180.0D) { d1 += 360.0D; }
      while(d1 >= 180.0D) { d1 -= 360.0D; }

      ad[0] = d + d1 * (double)f;
      d = field_40162_d[j][1];
      d1 = field_40162_d[k][1] - d;
      ad[1] = d + d1 * (double)f;
      ad[2] = field_40162_d[j][2] + (field_40162_d[k][2] - field_40162_d[j][2]) * (double)f;
      return ad;
   }

   public void tick() {
      discard();
      setNoAi(true);
      if (!level().isClientSide) {
         CompoundTag compound = new CompoundTag();
         addAdditionalSaveData(compound);
         EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, level());
         npc.readAdditionalSaveData(compound);
         npc.modelData.setEntity(ForgeRegistries.ENTITY_TYPES.getKey(CustomEntities.entityNpcDragon));
         level().addFreshEntity(npc);
      }
      prevAnimTime = animTime;
      exploded = false;
      float f1 = 0.045F;
      f1 *= (float)Math.pow(2.0D, getDeltaMovement().y);
      animTime += f1 * 0.5F;
      super.tick();
   }

   public void aiStep() {
      prevAnimTime = animTime;
      float f;
      if (level().isClientSide && getHealth() <= 0.0F) {
         if (!exploded) {
            exploded = true;
            f = (random.nextFloat() - 0.5F) * 8.0F;
            float f2 = (random.nextFloat() - 0.5F) * 4.0F;
            float f4 = (random.nextFloat() - 0.5F) * 8.0F;
            level().addParticle(ParticleTypes.EXPLOSION, getX() + (double)f, getY() + 2.0D + (double)f2, getZ() + (double)f4, 0.0D, 0.0D, 0.0D);
         }
      } else {
         exploded = false;
         f = 0.045F;
         f *= (float)Math.pow(2.0D, getDeltaMovement().y);
         animTime += f * 0.5F;
      }

      super.aiStep();
   }

   public @NotNull EntityDimensions getDimensions(@NotNull Pose pos) {
      return size;
   }

}
