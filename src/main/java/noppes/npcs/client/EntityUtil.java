package noppes.npcs.client;

import java.util.HashMap;
import java.util.List;

import net.minecraft.network.syncher.SynchedEntityData.DataItem;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.world.entity.IEntityMixin;
import noppes.npcs.mixin.world.entity.ILivingEntityMixin;
import noppes.npcs.mixin.world.entity.IWalkAnimationStateMixin;
import noppes.npcs.shared.common.util.LogWriter;

public class EntityUtil {

   private static HashMap<EntityType<? extends Entity>, Class<? extends Entity>> entityClasses = new HashMap<>();

   public static void Copy(LivingEntity copied, LivingEntity entity) {
      if (copied == null || entity == null) { return; }
      ((IEntityMixin) entity).setLevel(copied.level());
      entity.deathTime = copied.deathTime;
      entity.walkDist = copied.walkDist;
      entity.walkDistO = copied.walkDist;
      entity.moveDist = copied.moveDist;
      entity.zza = copied.zza;
      entity.xxa = copied.xxa;
      entity.setOnGround(copied.onGround());
      entity.fallDistance = copied.fallDistance;
      entity.setJumping(((ILivingEntityMixin) copied).getJumping());
      List<DataItem<Object>> copiedData = ((ISynchedEntityData) copied.getEntityData()).cnpcs$getAll();
      List<DataItem<Object>> data = ((ISynchedEntityData) entity.getEntityData()).cnpcs$getAll();

      for (DataItem<Object> entry : copiedData) {
         if (data.stream().anyMatch((e) -> e.getAccessor() == entry.getAccessor()) && entry.getValue() instanceof DataValue) {
            entity.getEntityData().set(entry.getAccessor(), ((DataValue<?>) entry.getValue()).value());
         }
      }

      entity.xo = copied.xo;
      entity.yo = copied.yo;
      entity.zo = copied.zo;
      entity.setPos(copied.getX(), copied.getY(), copied.getZ());
      entity.xOld = copied.xOld;
      entity.yOld = copied.yOld;
      entity.zOld = copied.zOld;
      entity.setDeltaMovement(copied.getDeltaMovement());
      entity.setXRot(copied.getXRot());
      entity.setYRot(copied.getYRot());
      entity.xRotO = copied.xRotO;
      entity.yRotO = copied.yRotO;
      entity.yHeadRot = copied.yHeadRot;
      entity.yHeadRotO = copied.yHeadRotO;
      entity.yBodyRot = copied.yBodyRot;
      entity.yBodyRotO = copied.yBodyRotO;
      ((ILivingEntityMixin) entity).setUseItemRemaining(copied.getUseItemRemainingTicks());
      ((IWalkAnimationStateMixin) entity.walkAnimation).setSpeedOld(copied.walkAnimation.position());
      ((ILivingEntityMixin) entity).setAnimStep(((ILivingEntityMixin) copied).getAnimStep());
      ((ILivingEntityMixin) entity).setAnimStepO(((ILivingEntityMixin) copied).getAnimStepO());
      ((ILivingEntityMixin) entity).setSwimAmount(((ILivingEntityMixin) copied).getSwimAmount());
      ((ILivingEntityMixin) entity).setSwimAmountO(((ILivingEntityMixin) copied).getSwimAmountO());
      entity.swinging = copied.swinging;
      entity.swingTime = copied.swingTime;
      entity.walkAnimation.setSpeed(copied.walkAnimation.speed());
      ((IWalkAnimationStateMixin) entity.walkAnimation).setSpeedOld(((IWalkAnimationStateMixin) copied.walkAnimation).getSpeedOld());
      entity.attackAnim = copied.attackAnim;
      entity.oAttackAnim = copied.oAttackAnim;
      entity.tickCount = copied.tickCount;
      entity.setHealth(Math.min(copied.getHealth(), entity.getMaxHealth()));
      entity.hurtTime = copied.hurtTime;
      entity.deathTime = copied.deathTime;
      entity.getPersistentData().merge(copied.getPersistentData());
      if (entity instanceof Player ePlayer && copied instanceof Player cPlayer) {
         ePlayer.bob = cPlayer.bob;
         ePlayer.oBob = cPlayer.oBob;
         ePlayer.xCloakO = cPlayer.xCloakO;
         ePlayer.yCloakO = cPlayer.yCloakO;
         ePlayer.zCloakO = cPlayer.zCloakO;
         ePlayer.xCloak = cPlayer.xCloak;
         ePlayer.yCloak = cPlayer.yCloak;
         ePlayer.zCloak = cPlayer.zCloak;
      }

      EquipmentSlot[] var9 = EquipmentSlot.values();
      int var12 = var9.length;

      int var6;
      EquipmentSlot slot;
      for(var6 = 0; var6 < var12; ++var6) {
         slot = var9[var6];
         entity.setItemSlot(slot, copied.getItemBySlot(slot));
      }

      if (entity instanceof EnderDragon) {
         entity.setXRot(entity.getXRot() + 180.0F);
      }
      ((IEntityMixin) entity).setRemoval(((IEntityMixin) copied).getRemoval());
      entity.deathTime = copied.deathTime;
      entity.tickCount = copied.tickCount;
      if (entity instanceof EnderDragon) {
         entity.setYRot(entity.getYRot() + 180.0F);
      }

      if (entity instanceof Chicken) {
         ((Chicken)entity).flap = copied.onGround() ? 0.0F : 1.0F;
      }

      var9 = EquipmentSlot.values();
      var12 = var9.length;

      for(var6 = 0; var6 < var12; ++var6) {
         slot = var9[var6];
         entity.setItemSlot(slot, copied.getItemBySlot(slot));
      }

      if (copied instanceof EntityNPCInterface npc && entity instanceof EntityNPCInterface target) {
         target.textureLocation = npc.textureLocation;
         target.textureGlowLocation = npc.textureGlowLocation;
         target.textureCloakLocation = npc.textureCloakLocation;
         target.display = npc.display;
         target.inventory = npc.inventory;
         if (npc.job.getType() == 9) {
            target.job = npc.job;
         }
         if (target.currentAnimation != npc.currentAnimation) {
            target.currentAnimation = npc.currentAnimation;
            npc.refreshDimensions();
         }
         target.setDataWatcher(npc.getEntityData());
      }

      if (entity instanceof EntityCustomNpc target && copied instanceof EntityCustomNpc npc) {
         target.modelData = npc.modelData.copy();
         target.modelData.setEntity(null);
      }

   }

   public static HashMap<EntityType<? extends Entity>, Class<? extends Entity>> getAllEntitiesClasses(Level level) {
      if (!entityClasses.isEmpty()) { return entityClasses; }
      HashMap<EntityType<? extends Entity>, Class<? extends Entity>> data = new HashMap<>();
      for (EntityType<?> ent : ForgeRegistries.ENTITY_TYPES.getValues()) {
         try {
            Entity e = ent.create(level);
            if (e != null) {
               if (LivingEntity.class.isAssignableFrom(e.getClass())) { data.put(ent, e.getClass()); }
               e.discard();
            }
         } catch (Exception ignored) {}
      }
      entityClasses = data;
      return data;
   }

   public static HashMap<String, ResourceLocation> getAllEntities(Level level, boolean withNpcs) {
      HashMap<String, ResourceLocation> data = new HashMap<>();
      for (EntityType<?> ent : ForgeRegistries.ENTITY_TYPES.getValues()) {
         try {
            Entity e = ent.create(level);
            if (e != null) {
               if (LivingEntity.class.isAssignableFrom(e.getClass()) && (withNpcs || !EntityNPCInterface.class.isAssignableFrom(e.getClass()))) {
                  data.put(ent.getDescriptionId(), ForgeRegistries.ENTITY_TYPES.getKey(ent));
               }
               e.discard();
            }
         } catch (Throwable var6) {
            LogWriter.except(var6);
         }
      }
      return data;
   }

}
