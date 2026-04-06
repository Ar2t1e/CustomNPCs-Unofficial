package noppes.npcs.api;

import noppes.npcs.api.interfaces.ParamName;

public interface IScoreboardTeam {

   String getName();

   String getDisplayName();

   void setDisplayName(@ParamName("name") String name);

   void addPlayer(@ParamName("player") String player);

   boolean hasPlayer(@ParamName("player") String player);

   void removePlayer(@ParamName("player") String player);

   String[] getPlayers();

   void clearPlayers();

   boolean getFriendlyFire();

   void setFriendlyFire(@ParamName("bo") boolean bo);

   void setColor(@ParamName("color") String color);

   String getColor();

   void setSeeInvisibleTeamPlayers(@ParamName("bo") boolean bo);

   boolean getSeeInvisibleTeamPlayers();

}
