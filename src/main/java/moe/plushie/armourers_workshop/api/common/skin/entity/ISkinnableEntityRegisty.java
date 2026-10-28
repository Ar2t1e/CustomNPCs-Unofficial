package moe.plushie.armourers_workshop.api.common.skin.entity;

import net.minecraft.entity.Entity;

@SuppressWarnings("unused")
public interface ISkinnableEntityRegisty {

	ISkinnableEntity getSkinnableEntity(Entity entity);

	boolean isValidEntity(Entity entity);

	void registerEntity(ISkinnableEntity skinnableEntity);

}
