package noppes.npcs.ability;

import noppes.npcs.constants.EnumAbilityType;
import noppes.npcs.entity.EntityNPCInterface;

public class AbilityPull extends AbstractAbility implements IAbilityUpdate {

   public AbilityPull(EntityNPCInterface entity) {
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
