package noppes.npcs.controllers.data;

import noppes.npcs.constants.EnumAvailabilityScoreboard;

public class AvailabilityScoreboardData {

	public EnumAvailabilityScoreboard scoreboardType;
	public int scoreboardValue;

	public AvailabilityScoreboardData(EnumAvailabilityScoreboard type, int value) {
		scoreboardType = type;
		scoreboardValue = value;
	}

}
