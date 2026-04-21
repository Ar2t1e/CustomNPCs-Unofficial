package noppes.npcs.api.entity;

import net.minecraft.entity.EntityLiving;
import noppes.npcs.api.IPos;
import noppes.npcs.api.interfaces.ParamName;

public interface IEntityLiving<T extends EntityLiving> extends IEntityLivingBase<T> {

	@SuppressWarnings("unused")
	void clearNavigation();

	T getMCEntity();

	@SuppressWarnings("unused")
	IPos getNavigationPath();

	boolean isNavigating();

	void jump();

	@SuppressWarnings("unused")
	void navigateTo(@ParamName("x") double x, @ParamName("y") double y, @ParamName("z") double z, @ParamName("speed") double speed);

	@SuppressWarnings("unused")
	void navigateTo(@ParamName("posses") IPos[] posses, @ParamName("speed") double speed);

}
