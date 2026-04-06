package noppes.npcs.api.handler.data;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IPlayer;

public interface IFaction {

   int getId();

   String getName();

   int getDefaultPoints();

   void setDefaultPoints(@ParamName("points") int points);

   int getColor();

   int playerStatus(@ParamName("player") IPlayer<?> player);

   boolean hostileToNpc(@ParamName("npc") ICustomNpc<?> npc);

   boolean hostileToFaction(@ParamName("factionId") int factionId);

   int[] getHostileList();

   void addHostile(@ParamName("id") int id);

   void removeHostile(@ParamName("id") int id);

   boolean hasHostile(@ParamName("id") int id);

   boolean getIsHidden();

   void setIsHidden(@ParamName("bo") boolean bo);

   boolean getAttackedByMobs();

   void setAttackedByMobs(@ParamName("bo") boolean bo);

   void save();

    // New from Unofficial (BetaZavr)
    String getDescription();

   String getFlag();

   void setDescription(@ParamName("description") String description);

   void setFlag(@ParamName("flagPath") String flagPath);
}
