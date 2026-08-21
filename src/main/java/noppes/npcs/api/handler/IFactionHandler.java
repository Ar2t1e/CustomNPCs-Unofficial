package noppes.npcs.api.handler;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.handler.data.IFaction;

import java.util.List;

public interface IFactionHandler {

	IFaction create(@ParamName("name") String name, @ParamName("color") int color);

	IFaction delete(@ParamName("id") int id);

	IFaction get(@ParamName("id") int id);

	List<IFaction> list();

}
