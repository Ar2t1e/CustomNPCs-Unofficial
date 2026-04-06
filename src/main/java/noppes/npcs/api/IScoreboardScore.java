package noppes.npcs.api;

import noppes.npcs.api.interfaces.ParamName;

public interface IScoreboardScore {

   int getValue();

   void setValue(@ParamName("value") int value);

   String getPlayerName();

}
