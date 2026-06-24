package noppes.npcs.api.wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.Multimap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.EntityType;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityLivingBase;
import noppes.npcs.api.entity.data.IMark;
import noppes.npcs.api.entity.data.INpcAttribute;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.mixin.entity.IEntityTrackerMixin;
import noppes.npcs.api.wrapper.data.AttributeWrapper;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.mixin.entity.ai.attributes.IAbstractAttributeMapMixin;
import noppes.npcs.util.ValueUtil;

public class EntityLivingBaseWrapper<T extends EntityLivingBase> extends EntityWrapper<T> implements IEntityLivingBase<T> {

	public EntityLivingBaseWrapper(T entity) { super(entity); }

	@Override
	public INpcAttribute addAttribute(INpcAttribute attribute) {
		if (attribute == null || hasAttribute(attribute)) { return null; }
		IAttribute attr = null;
		if (attribute.getMCAttribute() instanceof IAttribute) { attr = (IAttribute) attribute.getMCAttribute(); }
		else if (attribute.getMCBaseAttribute() != null) { attr = attribute.getMCBaseAttribute(); }
		if (attr == null) { return null; }
		entity.getAttributeMap().registerAttribute(attr);
		INpcAttribute npcAttr = getIAttribute(attribute.getName());
		if (npcAttr != null) { ((AttributeWrapper) npcAttr).setCustom(true); }
		return npcAttr;
	}

	@Override
	public INpcAttribute addAttribute(String attributeName, String displayName, double baseValue, double minValue, double maxValue) {
		if (attributeName == null || attributeName.isEmpty() || hasAttribute(attributeName)) { return null; }
		return addAttribute(new AttributeWrapper(entity, attributeName, displayName, baseValue, minValue, maxValue));
	}

	@Override
	public IMark addMark(int type) {
		MarkData data = MarkData.get(entity);
		return data.addMark(type);
	}

	public void addPotionEffect(String effect, int duration, int strength, boolean hideParticles) {
		addPotionEffect(ForgeRegistries.POTIONS.getValue(new ResourceLocation(effect)), duration, strength, hideParticles);
	}

	@Override
	public void addPotionEffect(int effect, int duration, int strength, boolean hideParticles) {
		addPotionEffect(Potion.getPotionById(effect), duration, strength, hideParticles);
	}

	public void addPotionEffect(Potion p, int duration, int strength, boolean hideParticles) {
		if (p != null) {
			if (!p.isInstant()) { duration *= 20; }
			strength = ValueUtil.correctInt(strength, 0, 255);
			duration = ValueUtil.correctInt(duration, 0, 1000000);
			if (duration == 0) { entity.removePotionEffect(p); }
			else { entity.addPotionEffect(new PotionEffect(p, duration, strength, false, hideParticles)); }
		}
	}

	@Override
	public boolean canSeeEntity(IEntity<T> entityIn) { return entity.canEntityBeSeen(entityIn.getMCEntity()); }

	@Override
	public void clearPotionEffects() { entity.clearActivePotions(); }

	@Override
	public IItemStack getArmor(int slot) {
		if (slot < 0 || slot > 3) { throw new CustomNPCsException("Wrong slot id:" + slot); }
		EntityEquipmentSlot s = getSlot(slot);
		if (s == null) { return ItemStackWrapper.AIR;}
		return Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(entity.getItemStackFromSlot(s));
	}

	private int getArmSwingAnimationEnd() {
		if (entity.isPotionActive(MobEffects.HASTE)) {
			return 6 - (1 + Objects.requireNonNull(entity.getActivePotionEffect(MobEffects.HASTE)).getAmplifier());
		}
		return entity.isPotionActive(MobEffects.MINING_FATIGUE)
				? 6 + (1 + Objects.requireNonNull(entity.getActivePotionEffect(MobEffects.MINING_FATIGUE)).getAmplifier()) * 2
				: 6;
	}

	@Override
	@SuppressWarnings("unchecked")
	public IEntityLivingBase<T> getAttackTarget() {
		return (IEntityLivingBase<T>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(entity.getRevengeTarget());
	}

	@Override
	public float getHealth() { return entity.getHealth(); }

	@Override
	public INpcAttribute getIAttribute(String attributeName) {
		Map<String, IAttributeInstance> attributesByName = ((IAbstractAttributeMapMixin) entity.getAttributeMap()).getAttributesByName();
        if (attributesByName == null) { return null; }
        return Objects.requireNonNull(NpcAPI.Instance()).getIAttribute(attributesByName.get(attributeName));
	}

	@Override
	public String[] getIAttributeNames() {
		Map<String, IAttributeInstance> attributesByName = ((IAbstractAttributeMapMixin) entity.getAttributeMap()).getAttributesByName();
		if (attributesByName == null) { return new String[0]; }
        return attributesByName.keySet().toArray(new String[0]);
	}

	@Override
	public INpcAttribute[] getIAttributes() {
		List<INpcAttribute> list = new ArrayList<>();
		for (IAttributeInstance attr : entity.getAttributeMap().getAllAttributes()) {
			list.add(Objects.requireNonNull(NpcAPI.Instance()).getIAttribute(attr));
		}
		return list.toArray(new INpcAttribute[0]);
	}

	@Override
	@SuppressWarnings("unchecked")
	public IEntityLivingBase<T> getLastAttacked() {
		return (IEntityLivingBase<T>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(entity.getLastAttackedEntity());
	}

	@Override
	public int getLastAttackedTime() {
		return entity.getLastAttackedEntityTime();
	}

	@Override
	public IItemStack getMainhandItem() {
		return Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(entity.getHeldItemMainhand());
	}

	@Override
	public IMark[] getMarks() {
		MarkData data = MarkData.get(entity);
		return data.marks.toArray(new IMark[0]);
	}

	@Override
	public float getMaxHealth() { return entity.getMaxHealth(); }

	@Override
	public float getMoveForward() { return entity.moveForward; }

	@Override
	public float getMoveStrafing() { return entity.moveStrafing; }

	@Override
	public float getMoveVertical() { return entity.moveVertical; }

	@Override
	public IItemStack getOffhandItem() { return Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(entity.getHeldItemOffhand()); }

	@Override
	public int getPotionEffect(int effect) {
		PotionEffect pf = entity.getActivePotionEffect(Objects.requireNonNull(Potion.getPotionById(effect)));
		return pf == null ? -1 : pf.getAmplifier();
	}

	private EntityEquipmentSlot getSlot(int slot) {
		switch (slot) {
			case 0: return EntityEquipmentSlot.FEET;
			case 1: return EntityEquipmentSlot.LEGS;
			case 2: return EntityEquipmentSlot.CHEST;
			case 3: return EntityEquipmentSlot.HEAD;
		}
		return null;
	}

	@Override
	public int getType() {
		return EntityType.LIVING.get();
	}

	@Override
	public boolean hasAttribute(INpcAttribute attribute) {
		for (IAttributeInstance attr : entity.getAttributeMap().getAllAttributes()) {
			if (attr.equals(attribute.getMCAttribute())) { return true; }
		}
		return false;
	}

	@Override
	public boolean hasAttribute(String attributeName) {
		Map<String, IAttributeInstance> attributesByName = ((IAbstractAttributeMapMixin) entity.getAttributeMap()).getAttributesByName();
		if (attributesByName == null) { return false; }
		return attributesByName.containsKey(attributeName);
	}

	@Override
	public boolean isAttacking() { return entity.getRevengeTarget() != null; }

	@Override
	public boolean isChild() { return entity.isChild(); }

	@Override
	public boolean removeAttribute(INpcAttribute attribute) {
		if (attribute == null || !attribute.isCustom() || !hasAttribute(attribute)) { return false; }
		Map<IAttribute, IAttributeInstance> attributes = ((IAbstractAttributeMapMixin) entity.getAttributeMap()).getAttributes();
		Map<String, IAttributeInstance> attributesByName = ((IAbstractAttributeMapMixin) entity.getAttributeMap()).getAttributesByName();
		Multimap<IAttribute, IAttribute> descendantsByParent = ((IAbstractAttributeMapMixin) entity.getAttributeMap()).getDescendantsByParent();
		if (attributes == null || descendantsByParent == null || attributesByName == null) { return false; }
		IAttribute key = null;
		String name = null;
		IAttribute parent = null;
		for (IAttribute k : attributes.keySet()) {
			if (attributes.get(k).equals(attribute.getMCAttribute())) {
				key = k;
				break;
			}
		}
		if (key != null) {
			name = key.getName();
			for (IAttribute p : descendantsByParent.keySet()) {
				if (descendantsByParent.get(p).equals(key)) {
					parent = p;
					break;
				}
			}
		}
		attributes.remove(key);
		attributesByName.remove(name);
		if (parent != null) { descendantsByParent.remove(parent, key); }
		return true;
	}

	@Override
	public boolean removeAttribute(String attributeName) { return removeAttribute(getIAttribute(attributeName)); }

	@Override
	public void removeMark(IMark mark) {
		MarkData data = MarkData.get(entity);
		data.marks.remove((MarkData.Mark) mark);
		data.syncClients();
	}

	@Override
	public void setArmor(int slot, IItemStack item) {
		if (slot < 0 || slot > 3) { throw new CustomNPCsException("Wrong slot id:" + slot); }
		EntityEquipmentSlot s = getSlot(slot);
		if (s != null) { entity.setItemStackToSlot(s, item == null ? ItemStack.EMPTY : item.getMCItemStack()); }
	}

	@Override
	public void setAttackTarget(IEntityLivingBase<T> living) { entity.setRevengeTarget(living == null ? null : living.getMCEntity()); }

	@Override
	public void setHealth(float health) { entity.setHealth(health); }

	@Override
	public void setMainhandItem(IItemStack item) {
		entity.setHeldItem(EnumHand.MAIN_HAND, (item == null) ? ItemStack.EMPTY : item.getMCItemStack());
	}

	@Override
	public void setMaxHealth(float health) {
		if (health > 0.0f) { entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(health); }
	}

	@Override
	public void setMoveForward(float move) { entity.moveForward = move; }

	@Override
	public void setMoveStrafing(float move) { entity.moveStrafing = move; }

	@Override
	public void setMoveVertical(float move) { entity.moveVertical = move; }

	@Override
	public void setOffhandItem(IItemStack item) {
		entity.setHeldItem(EnumHand.OFF_HAND, (item == null) ? ItemStack.EMPTY : item.getMCItemStack());
	}

	private void swim(EnumHand hand) {
		if (!(entity instanceof EntityPlayerMP)) {
			entity.swingArm(hand);
			return;
		}
		ItemStack stack = entity.getHeldItem(hand);
		if (!stack.isEmpty()) {
			if (stack.getItem().onEntitySwing(entity, stack)) {
				return;
			}
		}
		if (!entity.isSwingInProgress || entity.swingProgressInt >= getArmSwingAnimationEnd() / 2
				|| entity.swingProgressInt < 0) {
			entity.swingProgressInt = -1;
			entity.isSwingInProgress = true;
			entity.swingingHand = hand;
			SPacketAnimation pack = new SPacketAnimation(entity, hand == EnumHand.MAIN_HAND ? 0 : 3);
			IntHashMap<EntityTrackerEntry> trackedEntityHashTable = ((IEntityTrackerMixin) ((WorldServer) entity.world).getEntityTracker()).npcs$getTrackedEntityHashTable();
			EntityTrackerEntry entitytrackerentry = trackedEntityHashTable.lookup(entity.getEntityId());
			if (entitytrackerentry != null) {
				for (EntityPlayerMP entityplayermp : entitytrackerentry.trackingPlayers) {
					entityplayermp.connection.sendPacket(pack);
				}
				if (entity instanceof EntityPlayerMP && !entitytrackerentry.trackingPlayers.contains((EntityPlayerMP) entity)) {
					((EntityPlayerMP) entity).connection.sendPacket(pack);
				}
			}
		}
	}

	@Override
	public void swingMainhand() { swim(EnumHand.MAIN_HAND); }

	@Override
	public void swingOffhand() { swim(EnumHand.OFF_HAND); }

	@Override
	public boolean typeOf(int type) { return type == EntityType.LIVING.get() || super.typeOf(type); }

}
