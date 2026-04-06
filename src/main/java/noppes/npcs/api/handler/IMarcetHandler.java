package noppes.npcs.api.handler;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.handler.data.IDeal;
import noppes.npcs.api.handler.data.IMarcet;

@SuppressWarnings("all")
public interface IMarcetHandler {

    IDeal addDeal();

    IMarcet addMarcet();

    IDeal getDeal(@ParamName("dealId") int dealID);

    int[] getDealIDs();

    IMarcet getMarcet(@ParamName("marcetId") int marcetId);

    IMarcet getMarcet(@ParamName("name") String name);

    int[] getMarketIDs();

    void removeDeal(@ParamName("dealId") int dealId);

    void removeMarcet(@ParamName("marcetId") int marcetId);

}
