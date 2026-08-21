package noppes.npcs.api.entity;

import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.entity.data.IMark;
import noppes.npcs.api.entity.data.INpcAttribute;
import noppes.npcs.api.item.IItemStack;

public interface IEntityLivingBase<T extends EntityLivingBase> extends IEntity<T> {

	INpcAttribute addAttribute(@ParamName("attribute") INpcAttribute attribute);

	@SuppressWarnings("unused")
	INpcAttribute addAttribute(@ParamName("attributeName") String attributeName, @ParamName("displayName") String displayName,
							   @ParamName("baseValue") double baseValue, @ParamName("minValue") double minValue, @ParamName("maxValue") double maxValue);

	@SuppressWarnings("unused")
	IMark addMark(@ParamName("type") int type);

	void addPotionEffect(@ParamName("entity") int effect, @ParamName("duration") int duration,
						 @ParamName("strength") int strength, @ParamName("hideParticles") boolean hideParticles);

	@SuppressWarnings("unused")
	boolean canSeeEntity(@ParamName("entity") IEntity<T> entity);

	@SuppressWarnings("unused")
	void clearPotionEffects();

	@SuppressWarnings("unused")
	IItemStack getArmor(@ParamName("slot") int slot);

	IEntityLivingBase<T> getAttackTarget();

	float getHealth();

	INpcAttribute getIAttribute(@ParamName("attributeName") String attributeName);

	@SuppressWarnings("unused")
	String[] getIAttributeNames();

	@SuppressWarnings("unused")
	INpcAttribute[] getIAttributes();

	@SuppressWarnings("unused")
	IEntityLivingBase<T> getLastAttacked();

	@SuppressWarnings("unused")
	int getLastAttackedTime();

	@SuppressWarnings("unused")
	IItemStack getMainhandItem();

	IMark[] getMarks();

	float getMaxHealth();

	T getMCEntity();

	float getMoveForward();

	float getMoveStrafing();

	float getMoveVertical();

	@SuppressWarnings("unused")
	IItemStack getOffhandItem();

	@SuppressWarnings("unused")
	int getPotionEffect(@ParamName("effect") int effect);

	boolean hasAttribute(@ParamName("attribute") INpcAttribute attribute);

	boolean hasAttribute(@ParamName("attributeName") String attributeName);

	boolean isAttacking();

	boolean isChild();

	boolean removeAttribute(@ParamName("attribute") INpcAttribute attribute);

	@SuppressWarnings("unused")
	boolean removeAttribute(@ParamName("attributeName") String attributeName);

	@SuppressWarnings("unused")
	void removeMark(@ParamName("mark") IMark mark);

	@SuppressWarnings("unused")
	void setArmor(@ParamName("slot") int slot, @ParamName("item") IItemStack item);

	void setAttackTarget(@ParamName("living") IEntityLivingBase<T> living);

	void setHealth(@ParamName("health") float health);

	@SuppressWarnings("unused")
	void setMainhandItem(@ParamName("item") IItemStack item);

	void setMaxHealth(@ParamName("health") float health);

	void setMoveForward(@ParamName("move") float move);

	void setMoveStrafing(@ParamName("move") float move);

	void setMoveVertical(@ParamName("move") float move);

	@SuppressWarnings("unused")
	void setOffhandItem(@ParamName("item") IItemStack item);

	@SuppressWarnings("unused")
	void swingMainhand();

	@SuppressWarnings("unused")
	void swingOffhand();

}
