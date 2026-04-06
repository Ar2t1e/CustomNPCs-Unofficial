package noppes.npcs.api;

import noppes.npcs.api.interfaces.ParamName;

public interface IScoreboard {

   IScoreboardObjective[] getObjectives();

   IScoreboardObjective getObjective(@ParamName("name") String name);

   boolean hasObjective(@ParamName("objective") String objective);

   void removeObjective(@ParamName("objective") String objective);

   IScoreboardObjective addObjective(@ParamName("objective") String objective, @ParamName("criteria") String criteria);

   void setPlayerScore(@ParamName("player") String player, @ParamName("objective") String objective, @ParamName("score") int score);

   int getPlayerScore(@ParamName("player") String player, @ParamName("objective") String objective);

   boolean hasPlayerObjective(@ParamName("player") String player, @ParamName("objective") String objective);

   void deletePlayerScore(@ParamName("player") String player, @ParamName("objective") String objective);

   IScoreboardTeam[] getTeams();

   boolean hasTeam(@ParamName("name") String name);

   IScoreboardTeam addTeam(@ParamName("name") String name);

   IScoreboardTeam getTeam(@ParamName("name") String name);

   void removeTeam(@ParamName("name") String name);

   IScoreboardTeam getPlayerTeam(@ParamName("player") String player);

   void removePlayerTeam(@ParamName("player") String player);

   String[] getPlayerList();

}
