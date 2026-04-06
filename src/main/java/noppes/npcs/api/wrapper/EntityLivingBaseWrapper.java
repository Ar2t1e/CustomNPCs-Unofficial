package noppes.npcs.api.wrapper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityLiving;
import noppes.npcs.api.entity.data.IMark;
import noppes.npcs.api.entity.data.INpcAttribute;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.mixin.world.entity.ai.attributes.IAttributeMap;
import noppes.npcs.api.wrapper.data.AttributeWrapper;
import noppes.npcs.controllers.data.MarkData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntityLivingBaseWrapper<T extends LivingEntity>
        extends EntityWrapper<T>
        implements IEntityLiving<T> {

   public EntityLivingBaseWrapper(T entity) {
      super(entity);
   }

   public float getHealth() {
      return entity.getHealth();
   }

   public void setHealth(float health) {
      entity.setHealth(health);
   }

   public float getMaxHealth() {
      return entity.getMaxHealth();
   }

   public void setMaxHealth(float health) {
      if (!(health < 0.0F)) {
         Objects.requireNonNull(entity.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(health);
      }
   }

   public boolean isAttacking() {
      return entity.getLastHurtByMob() != null;
   }

   public void setAttackTarget(IEntityLiving<T> living) {
      if (living == null) { entity.setLastHurtByMob(null); }
      else { entity.setLastHurtByMob(living.getMCEntity()); }
   }

   @SuppressWarnings("unchecked")
   public IEntityLiving<T> getAttackTarget() {
      return (IEntityLiving<T>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(entity.getLastHurtByMob());
   }

   @SuppressWarnings("unchecked")
   public IEntityLiving<T> getLastAttacked() {
      return (IEntityLiving<T>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(entity.getLastHurtMob());
   }

   public int getLastAttackedTime() {
      return entity.getLastHurtMobTimestamp();
   }

   public boolean canSeeEntity(IEntity<?> iEntity) {
      return entity.hasLineOfSight(iEntity.getMCEntity());
   }

   public void swingMainhand() {
      entity.swing(InteractionHand.MAIN_HAND);
   }

   public void swingOffhand() {
      entity.swing(InteractionHand.OFF_HAND);
   }

   @SuppressWarnings("all")
   public void addPotionEffect(String effect, int duration, int strength, boolean hideParticles) {
      MobEffect p = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(effect));
      addPotionEffect(p, duration, strength, hideParticles);
   }

   public void addPotionEffect(int effect, int duration, int strength, boolean hideParticles) {
      MobEffect p = MobEffect.byId(effect);
      addPotionEffect(p, duration, strength, hideParticles);
   }

   public void addPotionEffect(MobEffect p, int duration, int strength, boolean hideParticles) {
      if (p != null) {
         if (strength < 0) { strength = 0; }
         else if (strength > 255) { strength = 255; }
         if (!p.isInstantenous()) { duration *= 20; }
         if (duration < 0) { duration = 0; }
         else if (duration > 1000000) { duration = 1000000; }
         if (duration == 0) { entity.removeEffect(p); }
         else { entity.addEffect(new MobEffectInstance(p, duration, strength, false, hideParticles)); }
      }
   }

   public void clearPotionEffects() {
      entity.removeAllEffects();
   }

   public int getPotionEffect(int effect) {
      MobEffectInstance pf = entity.getEffect(Objects.requireNonNull(MobEffect.byId(effect)));
      return pf == null ? -1 : pf.getAmplifier();
   }

   public IItemStack getMainhandItem() {
      return Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(entity.getMainHandItem());
   }

   public void setMainhandItem(IItemStack item) {
      entity.setItemInHand(InteractionHand.MAIN_HAND, item == null ? ItemStack.EMPTY : item.getMCItemStack());
   }

   public IItemStack getOffhandItem() {
      return Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(entity.getOffhandItem());
   }

   public void setOffhandItem(IItemStack item) {
      entity.setItemInHand(InteractionHand.OFF_HAND, item == null ? ItemStack.EMPTY : item.getMCItemStack());
   }

   public IItemStack getArmor(int slot) {
      EquipmentSlot s = getSlot(slot);
      if (s != null) {
          return Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(entity.getItemBySlot(s));
      } else {
         throw new CustomNPCsException("Wrong slot id:" + slot);
      }
   }

   public void setArmor(int slot, IItemStack item) {
      EquipmentSlot s = getSlot(slot);
      if (s != null) {
         entity.setItemSlot(s, item == null ? ItemStack.EMPTY : item.getMCItemStack());
      } else {
         throw new CustomNPCsException("Wrong slot id:" + slot);
      }
   }

   private EquipmentSlot getSlot(int slot) {
      return switch (slot) {
         case 1 -> EquipmentSlot.LEGS;
         case 2 -> EquipmentSlot.CHEST;
         case 3 -> EquipmentSlot.HEAD;
         case 0 -> EquipmentSlot.FEET;
         default -> null;
      };
   }

   public float getRotation() {
      return entity.yBodyRot;
   }

   public void setRotation(float rotation) {
      entity.yBodyRot = rotation;
   }

   public int getType() {
      return 5;
   }

   public boolean typeOf(int type) {
      return type == 5 || super.typeOf(type);
   }

   public boolean isChild() {
      return entity.isBaby();
   }

   public IMark addMark(int type) {
      MarkData data = MarkData.get(entity);
      return data.addMark(type);
   }

   public void removeMark(IMark mark) {
      MarkData data = MarkData.get(entity);
      data.marks.remove((MarkData.Mark) mark);
      data.syncClients();
   }

   public IMark[] getMarks() {
      MarkData data = MarkData.get(entity);
      return data.marks.toArray(new IMark[0]);
   }

   public float getMoveForward() {
      return entity.zza;
   }

   public void setMoveForward(float move) {
      entity.zza = move;
   }

   public float getMoveStrafing() {
      return entity.xxa;
   }

   public void setMoveStrafing(float move) {
      entity.xxa = move;
   }

   public float getMoveVertical() {
      return entity.yya;
   }

   public void setMoveVertical(float move) { entity.yya = move; }

   // New from Unofficial (BetaZavr)
   @Override
   public INpcAttribute addAttribute(INpcAttribute attribute) {
      if (attribute == null || hasAttribute(attribute)) { return null; }
      Attribute baseAttribute = attribute.getMCBaseAttribute();
      if (baseAttribute == null) { return null; }
      ((IAttributeMap) entity.getAttributes()).npcs$register(attribute.getMCAttribute());
      return attribute;
   }

   @Override
   public INpcAttribute addAttribute(String attributeName, double baseValue, double minValue, double maxValue) {
      if (attributeName == null || attributeName.isEmpty() || hasAttribute(attributeName)) { return null; }
      return addAttribute(new AttributeWrapper(this.entity, attributeName, baseValue, minValue, maxValue));
   }

   @Override
   public boolean hasAttribute(INpcAttribute attribute) {
      return entity.getAttributes().hasAttribute(attribute.getMCBaseAttribute());
   }

   @Override
   public boolean hasAttribute(String attributeName) {
      for (AttributeInstance attr : this.entity.getAttributes().getDirtyAttributes()) {
         if (attr.getAttribute().getDescriptionId().equals(attributeName)) { return true; }
      }
      return false;
   }

   @Override
   public INpcAttribute getIAttribute(String attributeName) {
      for (AttributeInstance attr : this.entity.getAttributes().getDirtyAttributes()) {
         if (attr.getAttribute().getDescriptionId().equals(attributeName)) {
            return Objects.requireNonNull(NpcAPI.Instance()).getIAttribute(attr);
         }
      }
      return null;
   }

   @Override
   public String[] getIAttributeNames() {
      List<String> list = new ArrayList<>();
      for (AttributeInstance attr : this.entity.getAttributes().getDirtyAttributes()) {
         list.add(attr.getAttribute().getDescriptionId());
      }
      return list.toArray(new String[0]);
   }

   @Override
   public INpcAttribute[] getIAttributes() {
      List<INpcAttribute> list = new ArrayList<>();
      for (AttributeInstance attr : this.entity.getAttributes().getDirtyAttributes()) {
         list.add(Objects.requireNonNull(NpcAPI.Instance()).getIAttribute(attr));
      }
      return list.toArray(new INpcAttribute[0]);
   }

   @Override
   public boolean removeAttribute(INpcAttribute attribute) {
      if (attribute == null || !attribute.isCustom() || !this.hasAttribute(attribute)) { return false; }
      ((IAttributeMap) entity.getAttributes()).npcs$remove(attribute.getMCAttribute());
      return true;
   }

   @Override
   public boolean removeAttribute(String attributeName) {
      return this.removeAttribute(getIAttribute(attributeName));
   }

}
