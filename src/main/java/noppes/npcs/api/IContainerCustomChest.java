package noppes.npcs.api;

import noppes.npcs.api.interfaces.ParamName;

public interface IContainerCustomChest extends IContainer {

	String getName();

	void setName(@ParamName("name") String name);

}
