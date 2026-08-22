package noppes.npcs.api;

import noppes.npcs.api.interfaces.ParamName;

@SuppressWarnings("all")
public interface IScoreboard {

	IScoreboardObjective addObjective(@ParamName("objective") String objective, @ParamName("criteria") String criteria);

	IScoreboardTeam addTeam(@ParamName("name") String name);

	void deletePlayerScore(@ParamName("player") String player, @ParamName("objective") String objective, @ParamName("datatag") String datatag);

	IScoreboardObjective getObjective(@ParamName("name") String name);

	IScoreboardObjective[] getObjectives();

	String[] getPlayerList();

	int getPlayerScore(@ParamName("player") String player, @ParamName("objective") String objective, @ParamName("datatag") String datatag);

	IScoreboardTeam getPlayerTeam(@ParamName("player") String player);

	IScoreboardTeam getTeam(@ParamName("name") String name);

	IScoreboardTeam[] getTeams();

	boolean hasObjective(@ParamName("objective") String objective);

	boolean hasPlayerObjective(@ParamName("player") String player, @ParamName("objective") String objective, @ParamName("datatag") String datatag);

	boolean hasTeam(@ParamName("name") String name);

	void removeObjective(@ParamName("objective") String objective);

	void removePlayerTeam(@ParamName("player") String player);

	void removeTeam(@ParamName("name") String name);

	void setPlayerScore(@ParamName("player") String player, @ParamName("objective") String objective, @ParamName("score") int score, @ParamName("datatag") String datatag);

}
