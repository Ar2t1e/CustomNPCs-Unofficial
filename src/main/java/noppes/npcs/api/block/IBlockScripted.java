package noppes.npcs.api.block;

import noppes.npcs.api.ITimers;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.item.IItemStack;

@SuppressWarnings("all")
public interface IBlockScripted extends IBlock {

   void setModel(@ParamName("item") IItemStack item);

   void setModel(@ParamName("name") String name);

   IItemStack getModel();

   ITimers getTimers();

   void setRedstonePower(@ParamName("strength") int strength);

   int getRedstonePower();

   void setIsLadder(@ParamName("bo") boolean bo);

   boolean getIsLadder();

   void setIsWaterlogged(@ParamName("bo") boolean bo);

   boolean getIsWaterlogged();

   void setLight(@ParamName("value") int value);

   int getLight();

   void setScale(@ParamName("x") float x, @ParamName("y") float y, @ParamName("z") float z);

   float getScaleX();

   float getScaleY();

   float getScaleZ();

   void setRotation(@ParamName("x") int x, @ParamName("y") int y, @ParamName("z") int z);

   int getRotationX();

   int getRotationY();

   int getRotationZ();

   String executeCommand(@ParamName("command") String command);

   boolean getIsPassible();

   void setIsPassible(@ParamName("bo") boolean bo);

   float getHardness();

   void setHardness(@ParamName("hardness") float hardness);

   float getResistance();

   void setResistance(@ParamName("resistance") float resistance);

   ITextPlane getTextPlane();

   ITextPlane getTextPlane2();

   ITextPlane getTextPlane3();

   ITextPlane getTextPlane4();

   ITextPlane getTextPlane5();

   ITextPlane getTextPlane6();

   void trigger(@ParamName("id") int id, @ParamName("arguments") Object... arguments);

}
