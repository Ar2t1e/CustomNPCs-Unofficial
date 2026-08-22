package noppes.npcs;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.ability.IAbilityDamaged;
import noppes.npcs.api.event.NpcEvent;
import noppes.npcs.constants.EnumAbilityType;

public class AbilityEventHandler {

	@SubscribeEvent
	public void invoke(NpcEvent.DamagedEvent event) {
		IAbilityDamaged ab = (IAbilityDamaged) event.npc.getMCEntity().abilities.getAbility(EnumAbilityType.ATTACKED);
		if (ab != null) { ab.handleEvent(event); }
	}

}
