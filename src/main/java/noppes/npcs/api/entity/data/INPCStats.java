package noppes.npcs.api.entity.data;

import noppes.npcs.api.interfaces.ParamName;

import java.util.List;

@SuppressWarnings("all")
public interface INPCStats {

   int getMaxHealth();

   void setMaxHealth(@ParamName("maxHealth") int maxHealth);

   List<String> getResistanceKeys();

   float getResistance(@ParamName("type") String type);

   void setResistance(@ParamName("type") String type, @ParamName("value") float value);

   int getCombatRegen();

   void setCombatRegen(@ParamName("regen") int regen);

   int getHealthRegen();

   void setHealthRegen(@ParamName("regen") int regen);

   INPCMelee getMelee();

   INPCRanged getRanged();

   boolean getImmune(@ParamName("type") int type);

   void setImmune(@ParamName("type") int type, @ParamName("bo") boolean bo);

   void setCreatureType(@ParamName("type") int type);

   int getCreatureType();

   int getRespawnType();

   void setRespawnType(@ParamName("type") int type);

   int getRespawnTime();

   void setRespawnTime(@ParamName("seconds") int seconds);

   boolean getHideDeadBody();

   void setHideDeadBody(@ParamName("hide") boolean hide);

   int getAggroRange();

   void setAggroRange(@ParamName("range") int range);

   // New from Unofficial (BetaZavr)
    int getLevel();

   void setLevel(@ParamName("level") int level);

   int getRarity();

   void setRarity(@ParamName("rarity") int rarityIn);

   String getRarityTitle();

   void setRarityTitle(@ParamName("rarityTitle") String rarityTitle);

    float getChanceBlockDamage();

   void setChanceBlockDamage(float chance);

    // New from Unofficial (BetaZavr)
    boolean isCalmdown();

   void setCalmdown(boolean range);
}
