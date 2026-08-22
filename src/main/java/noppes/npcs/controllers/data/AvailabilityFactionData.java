package noppes.npcs.controllers.data;

import noppes.npcs.constants.EnumAvailabilityFaction;
import noppes.npcs.constants.EnumAvailabilityFactionType;

public class AvailabilityFactionData {

	public EnumAvailabilityFactionType factionAvailable;
	public EnumAvailabilityFaction factionStance;

	public AvailabilityFactionData(EnumAvailabilityFactionType available, EnumAvailabilityFaction stance) {
		factionAvailable = available;
		factionStance = stance;
	}

}
