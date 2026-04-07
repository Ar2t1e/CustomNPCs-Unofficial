package noppes.npcs.controllers.data;

import noppes.npcs.constants.EnumAvailabilityScoreboard;

public class AvailabilityScoreboardData {

    public EnumAvailabilityScoreboard scoreboardType;
    public int scoreboardValue;

    public AvailabilityScoreboardData(EnumAvailabilityScoreboard type, int value) {
        scoreboardType = type;
        scoreboardValue = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (obj instanceof AvailabilityScoreboardData sd) {
            return scoreboardType == sd.scoreboardType && scoreboardValue == sd.scoreboardValue;
        }
        return false;
    }

}
