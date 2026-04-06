package noppes.npcs.ability;

import noppes.npcs.constants.EnumAbilityType;
import noppes.npcs.entity.EntityNPCInterface;

public class AbilityTeleport extends AbstractAbility implements IAbilityUpdate {

   public AbilityTeleport(EntityNPCInterface entity) {
      super(entity);
   }

   public boolean isActive() {
      return false;
   }

   public void update() {
   }

   public boolean isType(EnumAbilityType type) {
      return type == EnumAbilityType.UPDATE;
   }

}
