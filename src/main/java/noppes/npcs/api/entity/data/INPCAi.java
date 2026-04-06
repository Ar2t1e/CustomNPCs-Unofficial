package noppes.npcs.api.entity.data;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.constants.EnumSeeTarget;

public interface INPCAi {

   int getAnimation();

   void setAnimation(@ParamName("type") int type);

   int getCurrentAnimation();

   void setReturnsHome(@ParamName("bo") boolean bo);

   boolean getReturnsHome();

   int getRetaliateType();

   void setRetaliateType(@ParamName("type") int type);

   int getMovingType();

   void setMovingType(@ParamName("type") int type);

   int getNavigationType();

   void setNavigationType(@ParamName("type") int type);

   int getStandingType();

   void setStandingType(@ParamName("type") int type);

   boolean getAttackInvisible();

   void setAttackInvisible(@ParamName("attack") boolean attack);

   int getWanderingRange();

   void setWanderingRange(@ParamName("range") int range);

   boolean getInteractWithNPCs();

   void setInteractWithNPCs(@ParamName("interact") boolean interact);

   boolean getStopOnInteract();

   void setStopOnInteract(@ParamName("stopOnInteract") boolean stopOnInteract);

   int getWalkingSpeed();

   void setWalkingSpeed(@ParamName("speed") int speed);

   int getMovingPathType();

   boolean getMovingPathPauses();

   void setMovingPathType(@ParamName("type") int type, @ParamName("pauses") boolean pauses);

   int getDoorInteract();

   void setDoorInteract(@ParamName("type") int type);

   boolean getCanSwim();

   void setCanSwim(@ParamName("canSwim") boolean canSwim);

   int getSheltersFrom();

   void setSheltersFrom(@ParamName("type") int type);

   boolean getAvoidsWater();

   void setAvoidsWater(@ParamName("enabled") boolean enabled);

   boolean getLeapAtTarget();

   void setLeapAtTarget(@ParamName("leap") boolean leap);

   void setMountControl(@ParamName("enabled") boolean enabled);

   // New methods from Unofficial (BetaZavr)
   boolean isAIDisabled();

   void setIsAIDisabled(@ParamName("bo") boolean bo);

   float getOffsetX();

   float getOffsetY();

   float getOffsetZ();

   void setOffset(@ParamName("x") float x, @ParamName("y") float y, @ParamName("z") float z);

    int getMaxHurtResistantTime();

   void setMaxHurtResistantTime(@ParamName("ticks") int ticks);

    // in 1.12.2
    int getTacticalRange();

   void setTacticalRange(int range);

   int getTacticalType();

   void setTacticalType(int type);

   // New from Unofficial (BetaZavr)
   EnumSeeTarget getAttackLOS();

   void setAttackLOS(@ParamName("type") int type);

   boolean canBeCollide();

   void setCanBeCollide(boolean bo);
}
