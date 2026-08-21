package noppes.npcs.api;

import noppes.npcs.api.interfaces.ParamName;

public interface IScoreboardScore {

	String getPlayerName();

	int getValue();

	void setValue(@ParamName("value") int value);

}
