package noppes.npcs.api.entity.data;

import noppes.npcs.api.interfaces.ParamName;

public interface INPCRanged {

   int getStrength();

   void setStrength(@ParamName("strength") int strength);

   int getSpeed();

   void setSpeed(@ParamName("speed") int speed);

   int getBurst();

   void setBurst(@ParamName("count") int count);

   int getBurstDelay();

   void setBurstDelay(@ParamName("delay") int delay);

   int getKnockback();

   void setKnockback(@ParamName("punch") int punch);

   int getSize();

   void setSize(@ParamName("size") int size);

   boolean getRender3D();

   void setRender3D(@ParamName("render3d") boolean render3d);

   boolean getSpins();

   void setSpins(@ParamName("spins") boolean spins);

   boolean getSticks();

   void setSticks(@ParamName("sticks") boolean sticks);

   boolean getHasGravity();

   void setHasGravity(@ParamName("hasGravity") boolean hasGravity);

   boolean getAccelerate();

   void setAccelerate(@ParamName("accelerate") boolean accelerate);

   int getExplodeSize();

   void setExplodeSize(@ParamName("size") int size);

   int getEffectType();

   int getEffectTime();

   int getEffectStrength();

   void setEffect(@ParamName("type") int type, @ParamName("strength") int strength, @ParamName("time") int time);

   boolean getGlows();

   void setGlows(@ParamName("glows") boolean glows);

   int getParticle();

   void setParticle(@ParamName("type") int type);

   String getSound(@ParamName("type") int type);

   void setSound(@ParamName("type") int type, @ParamName("sound") String sound);

   int getShotCount();

   void setShotCount(@ParamName("count") int count);

   boolean getHasAimAnimation();

   void setHasAimAnimation(@ParamName("aim") boolean aim);

   int getAccuracy();

   void setAccuracy(@ParamName("accuracy") int accuracy);

   float getRange();

   void setRange(@ParamName("range") float range);

   int getDelayMin();

   int getDelayMax();

   int getDelayRNG();

   void setDelay(@ParamName("min") int min, @ParamName("max") int max);

   int getFireType();

   void setFireType(@ParamName("type") int type);

   int getMeleeRange();

   void setMeleeRange(@ParamName("range") int range);

}
