package noppes.npcs.api;

import noppes.npcs.api.interfaces.ParamName;

public interface IScoreboardObjective {

   String getName();

   String getDisplayName();

   void setDisplayName(@ParamName("name") String name);

   String getCriteria();

   boolean isReadyOnly();

   IScoreboardScore[] getScores();

   IScoreboardScore getScore(@ParamName("player") String player);

   boolean hasScore(@ParamName("player") String player);

   IScoreboardScore createScore(@ParamName("player") String player);

   void removeScore(@ParamName("player") String player);

}
