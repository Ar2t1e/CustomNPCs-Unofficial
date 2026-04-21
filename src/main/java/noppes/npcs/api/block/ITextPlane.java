package noppes.npcs.api.block;

import noppes.npcs.api.INbt;
import noppes.npcs.api.interfaces.ParamName;

public interface ITextPlane {

   String getText();

   void setText(@ParamName("text") String text);

   @SuppressWarnings("unused")
   int getRotationX();

   @SuppressWarnings("unused")
   int getRotationY();

   @SuppressWarnings("unused")
   int getRotationZ();

   @SuppressWarnings("unused")
   void setRotationX(@ParamName("x") int x);

   @SuppressWarnings("unused")
   void setRotationY(@ParamName("y") int y);

   @SuppressWarnings("unused")
   void setRotationZ(@ParamName("z") int z);

   float getOffsetX();

   float getOffsetY();

   @SuppressWarnings("unused")
   float getOffsetZ();

   void setOffsetX(@ParamName("x") float x);

   void setOffsetY(@ParamName("y") float y);

   @SuppressWarnings("unused")
   void setOffsetZ(@ParamName("z") float z);

   float getScale();

   void setScale(@ParamName("scale") float scale);

   INbt getNbt();

   void setNbt(@ParamName("nbt") INbt nbt);

}
