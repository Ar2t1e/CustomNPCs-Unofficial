package noppes.npcs;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import noppes.npcs.ability.IAbilityDamaged;
import noppes.npcs.api.event.NpcEvent;
import noppes.npcs.constants.EnumAbilityType;
import noppes.npcs.entity.EntityNPCInterface;

public class AbilityEventHandler {

   @SubscribeEvent
   public void cnpcNpcEventDamaged(NpcEvent.DamagedEvent event) {
      CustomNpcs.debugData.start(event.npc);
      IAbilityDamaged ab = (IAbilityDamaged)((EntityNPCInterface)event.npc.getMCEntity()).abilities.getAbility(EnumAbilityType.ATTACKED);
      if (ab != null) { ab.handleEvent(event); }
      CustomNpcs.debugData.end(event.npc);
   }

}
