package noppes.npcs.api.handler;

import java.util.List;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.handler.data.IFaction;

public interface IFactionHandler {

   List<IFaction> list();

   IFaction delete(@ParamName("id") int id);

   IFaction create(@ParamName("name") String name, @ParamName("color") int color);

   IFaction get(@ParamName("id") int id);

}
