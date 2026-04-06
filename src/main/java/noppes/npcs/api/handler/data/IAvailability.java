package noppes.npcs.api.handler.data;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.item.IItemStack;

public interface IAvailability {

   boolean hasMoneyData(@ParamName("type") int type);

   boolean isAvailable(@ParamName("player") IPlayer<?> player);

   void removeMoneyData(@ParamName("type") int type);

   void setDaytime(@ParamName("type") int type);

   int getMinPlayerLevel();

   void setMinPlayerLevel(@ParamName("level") int level);

   void removeDialog(@ParamName("id") int id);

   void removeQuest(@ParamName("id") int id);

   void removeFaction(@ParamName("id") int id);

   // New from Unofficial (BetaZavr)
   int[] getDaytime();

   int getHealth();

   int getHealthType();

   String[] getPlayerNames();

   String getStoredDataValue(@ParamName("key") String key);

    int getMoneyValue(@ParamName("type") int type);

    boolean hasDialog(@ParamName("id") int id);

   boolean hasFaction(@ParamName("id") int id);

   boolean hasPlayerName(@ParamName("name") String name);

   boolean hasQuest(@ParamName("id") int id);

   boolean hasScoreboard(@ParamName("objective") String objective);

   boolean hasStoredData(@ParamName("key") String key);

   void removePlayerName(@ParamName("name") String name);

   void removeScoreboard(@ParamName("objective") String objective);

   void removeStoredData(@ParamName("key") String key);

   void setDaytime(@ParamName("minHour") int minHour, @ParamName("maxHour") int maxHour);

   void setDialog(@ParamName("id") int id, @ParamName("type") int type);

   void setFaction(@ParamName("id") int id, @ParamName("type") int type, @ParamName("stance") int stance);

   void setHealth(@ParamName("value") int value, @ParamName("type") int type);

   void setPlayerName(@ParamName("name") String name, @ParamName("type") int type);

   void setQuest(@ParamName("id") int id, @ParamName("type") int type);

   void setScoreboard(@ParamName("objective") String objective, @ParamName("type") int type, @ParamName("value") int value);

   void setStoredData(@ParamName("key") String key, @ParamName("value") String value, @ParamName("type") int type);

   void setMoneyData(@ParamName("type") int type, @ParamName("equal") int equal, @ParamName("value") int value);

   boolean getGMOnly();

   void setGMOnly(@ParamName("gmOnly") boolean gmOnly);

   IItemStack getIItemStack(@ParamName("slotId") int slotId);

   IItemStack[] getIItemStacks();

   void setIItemStack(@ParamName("slotId") int slotId, @ParamName("item") IItemStack item);

   void removeIItemStack(@ParamName("slotId") int slotId);

   /* Removed:
   int getDialog(int slotId);
   void setDialog(int slotId, int id, int type);
   int getQuest(int slotId);
   void setQuest(int slotId, int id, int type);
   void setFaction(int slotId, int id, int type, int stance);
   void setScoreboard(int slotId, String objective, int type, int value);
   */

}
