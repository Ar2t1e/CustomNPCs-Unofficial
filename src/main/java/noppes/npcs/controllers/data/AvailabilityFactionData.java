package noppes.npcs.controllers.data;

import noppes.npcs.constants.EnumAvailabilityFaction;
import noppes.npcs.constants.EnumAvailabilityFactionType;

public class AvailabilityFactionData {

    public EnumAvailabilityFactionType factionAvailable;
    public EnumAvailabilityFaction factionStance;

    public AvailabilityFactionData(EnumAvailabilityFactionType availableType, EnumAvailabilityFaction stance) {
        factionAvailable = availableType;
        factionStance = stance;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) { return true; }
        if (obj instanceof AvailabilityFactionData fd) {
            return factionAvailable == fd.factionAvailable && factionStance == fd.factionStance;
        }
        return false;
    }

}
