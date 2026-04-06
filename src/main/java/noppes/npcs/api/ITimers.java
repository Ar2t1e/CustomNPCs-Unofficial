package noppes.npcs.api;

import noppes.npcs.api.interfaces.ParamName;

public interface ITimers {

   void start(@ParamName("id") int id, @ParamName("ticks") int ticks, @ParamName("repeat") boolean repeat);

   void forceStart(@ParamName("id") int id, @ParamName("ticks") int ticks,@ParamName("repeat")  boolean repeat);

   boolean has(@ParamName("id") int id);

   boolean stop(@ParamName("id") int id);

   void reset(@ParamName("id") int id);

   void clear();

}
