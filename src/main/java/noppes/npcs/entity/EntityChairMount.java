package noppes.npcs.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class EntityChairMount extends Entity {

   public EntityChairMount(EntityType type, Level world) {
      super(type, world);
   }

   public double getPassengersRidingOffset() {
      return 0.5D;
   }

   protected void defineSynchedData() {
   }

   public void baseTick() {
      super.baseTick();
      this.level();
      if (!this.level().isClientSide && this.getPassengers().isEmpty()) {
         this.discard();
      }
   }

   public boolean isInvulnerableTo(@NotNull DamageSource source) {
      return true;
   }

   public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }

   public boolean isInvisible() {
      return true;
   }

   public void move(@NotNull MoverType type, @NotNull Vec3 vec) {
   }

   public void load(@NotNull CompoundTag tagCompound) {
   }

   protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
   }

   protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
   }

   public @NotNull CompoundTag saveWithoutId(@NotNull CompoundTag compound) {
      return compound;
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean isPushable() {
      return false;
   }

   public boolean causeFallDamage(float distance, float damageMultiplier, @NotNull DamageSource source) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public void lerpTo(double x, double y, double z, float yaw, float pitch, int type, boolean bo) {
      this.setPos(x, y, z);
      this.setRot(yaw, pitch);
   }

}
