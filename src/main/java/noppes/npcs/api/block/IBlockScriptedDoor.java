package noppes.npcs.api.block;

import noppes.npcs.api.ITimers;
import noppes.npcs.api.interfaces.ParamName;

public interface IBlockScriptedDoor extends IBlock {

   ITimers getTimers();

   boolean getOpen();

   void setOpen(@ParamName("open") boolean open);

   @SuppressWarnings("unused")
   void setBlockModel(@ParamName("name") String name);

   String getBlockModel();

   @SuppressWarnings("unused")
   float getHardness();

   @SuppressWarnings("unused")
   void setHardness(@ParamName("hardness") float hardness);

   float getResistance();

   void setResistance(@ParamName("resistance") float resistance);

   // New from Unofficial (GoodBird)
   String executeCommand(@ParamName("command") String command);

   // New from Unofficial (BetaZavr)
   String getSound(@ParamName("isOpen") boolean isOpen);

   void setSound(@ParamName("isOpen") boolean isOpen, @ParamName("song") String song);

}
