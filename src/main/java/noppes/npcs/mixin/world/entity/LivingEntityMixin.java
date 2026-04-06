package noppes.npcs.mixin.world.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.mixin.entity.ILivingEntityMixin;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 499)
public class LivingEntityMixin implements ILivingEntityMixin {

    @Unique
    private DamageSource npcs$currentDamageSource;

    @Inject(
            at = {@At("HEAD")},
            method = {"addAdditionalSaveData"}
    )
    private void cnpcs$renderToBuffer(CompoundTag compound, CallbackInfo callbackInfo) {
        LivingEntity e = (LivingEntity) (Object) this;
        if (!e.level().isClientSide()) { MarkData.get(e).save(); }
    }

    // change recoil force from mod settings
    @Inject(at = @At("HEAD"),
            method = "knockback(DDD)V",
            cancellable = true)
    private void npcs$knockback(double strength, double ratioX, double ratioZ, CallbackInfo ci) {
        LivingEntity parent = (LivingEntity) (Object) this;
        if (npcs$currentDamageSource != null && !npcs$currentDamageSource.getMsgId().toLowerCase().contains("explosion") && npcs$currentDamageSource.is(DamageTypeTags.IS_PROJECTILE)) {
            strength *= 0.375f * ((float) CustomNpcs.KnockBackBasePowerRanged / 100.0f);
        }
        else { strength *= 0.5f * ((float) CustomNpcs.KnockBackBasePower / 100.0f); }
        LivingKnockBackEvent event = ForgeHooks.onLivingKnockBack(parent, (float) strength, ratioX, ratioZ);
        if (!event.isCanceled()) {
            strength = event.getStrength();
            ratioX = event.getRatioX();
            ratioZ = event.getRatioZ();
            strength *= 1.0 - parent.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            if (!(strength <= 0.0)) {
                parent.hasImpulse = true;
                Vec3 vec3 = parent.getDeltaMovement();
                Vec3 vec31 = (new Vec3(ratioX, 0.0, ratioZ)).normalize().scale(strength);
                parent.setDeltaMovement(vec3.x / 2.0 - vec31.x, parent.onGround() ? Math.min(0.4, vec3.y / 2.0 + strength) : vec3.y, vec3.z / 2.0 - vec31.z);
            }
        }
        if (event.isCanceled()) { return; }
        npcs$currentDamageSource = null;
        ci.cancel();
    }

    // remember the source of damage
    @Inject(method = "hurt", at = @At("HEAD"))
    private void npcs$saveDamageSource(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        npcs$currentDamageSource = source;
    }

    // Replace knockback force when dealing damage
    @Redirect(
            method = "hurt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V")
    )
    private void npcs$hurt(LivingEntity instance, double strength, double xRatio, double zRatio) {
        if (instance instanceof EntityNPCInterface npc) {
            float f0, f1;
            if (npcs$currentDamageSource != null && npcs$currentDamageSource.is(DamageTypeTags.IS_PROJECTILE)) {
                f0 = 0.25f;
                f1 = 0.15f * (float) npc.stats.ranged.getKnockback();
            } else {
                f0 = 0.2f;
                f1 = 0.2f  * (float) npc.stats.melee.getKnockback();
            }
            if (f1 != 0.0f) { strength = f0 + f1; }
        }
        if (strength != 0) { instance.knockback(strength, xRatio, zRatio); }
    }

    @Override
    public void npcs$setCurrentDamageSource(DamageSource source) { npcs$currentDamageSource = source; }

}
