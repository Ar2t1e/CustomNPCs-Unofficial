package noppes.npcs.api.block;

import noppes.npcs.api.interfaces.ParamName;

public interface ITextPlane {

   String getText();

   void setText(@ParamName("text") String text);

   int getRotationX();

   int getRotationY();

   int getRotationZ();

   void setRotationX(@ParamName("x") int x);

   void setRotationY(@ParamName("y") int y);

   void setRotationZ(@ParamName("z") int z);

   float getOffsetX();

   float getOffsetY();

   float getOffsetZ();

   void setOffsetX(@ParamName("x") float x);

   void setOffsetY(@ParamName("y") float y);

   void setOffsetZ(@ParamName("z") float z);

   float getScale();

   void setScale(@ParamName("scale") float scale);

}
