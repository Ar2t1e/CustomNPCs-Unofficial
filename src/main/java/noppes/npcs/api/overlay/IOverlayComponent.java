package noppes.npcs.api.overlay;

import noppes.npcs.api.INbt;
import noppes.npcs.api.interfaces.ParamName;

public interface IOverlayComponent {

   int getId();

   int getPosX();

   int getPosY();

   IOverlayComponent setPos(@ParamName("x") int x, @ParamName("y") int y);

   int getType();

   void toNbt(@ParamName("nbt") INbt nbt);

   void fromNbt(@ParamName("nbt") INbt nbt);

}
