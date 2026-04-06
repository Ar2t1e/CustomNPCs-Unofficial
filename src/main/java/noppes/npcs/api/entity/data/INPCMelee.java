package noppes.npcs.api.entity.data;

import noppes.npcs.api.interfaces.ParamName;

public interface INPCMelee {

   int getStrength();

   void setStrength(@ParamName("strength") int strength);

   int getDelay();

   void setDelay(@ParamName("speed") int speed);

   double getRange();

   void setRange(@ParamName("range") double range);

   int getKnockback();

   void setKnockback(@ParamName("knockback") int knockback);

   int getEffectType();

   int getEffectTime();

   int getEffectStrength();

   void setEffect(@ParamName("type") int type, @ParamName("strength") int strength, @ParamName("time") int time);

}
