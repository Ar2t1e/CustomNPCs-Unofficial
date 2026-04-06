package noppes.npcs.api.block;

import noppes.npcs.api.ITimers;
import noppes.npcs.api.interfaces.ParamName;

public interface IBlockScriptedDoor extends IBlock {

   ITimers getTimers();

   boolean getOpen();

   void setOpen(@ParamName("open") boolean open);

   void setBlockModel(@ParamName("name") String name);

   String getBlockModel();

   float getHardness();

   void setHardness(@ParamName("hardness") float hardness);

   float getResistance();

   void setResistance(@ParamName("resistance") float resistance);

   String executeCommand(@ParamName("command") String command);

}
