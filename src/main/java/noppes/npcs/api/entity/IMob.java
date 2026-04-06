package noppes.npcs.api.entity;

import net.minecraft.world.entity.Mob;
import noppes.npcs.api.IPos;
import noppes.npcs.api.interfaces.ParamName;

public interface IMob<T extends Mob> extends IEntityLiving<T> {
   boolean isNavigating();

   void clearNavigation();

   void navigateTo(@ParamName("x") double x, @ParamName("y") double y, @ParamName("z") double z, @ParamName("speed") double speed);

   void jump();

   T getMCEntity();

   IPos getNavigationPath();

   // New from Unofficial (BetaZavr)
   void navigateTo(@ParamName("posses") IPos[] posses, @ParamName("speed") double speed);

}
