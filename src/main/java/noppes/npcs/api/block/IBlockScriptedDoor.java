package noppes.npcs.api.block;

import noppes.npcs.api.ITimers;
import noppes.npcs.api.interfaces.ParamName;

@SuppressWarnings("all")
public interface IBlockScriptedDoor extends IBlock {

	String getBlockModel();

	float getHardness();

	boolean getOpen();

	float getResistance();

	ITimers getTimers();

	void setBlockModel(@ParamName("name") String name);

	void setHardness(@ParamName("hardness") float hardness);

	void setOpen(@ParamName("open") boolean open);

	void setResistance(@ParamName("resistance") float resistance);


	// New from Unofficial (GoodBird)
	String executeCommand(@ParamName("command") String command);

	// New from Unofficial (BetaZavr)
	String getSound(@ParamName("isOpen") boolean isOpen);

	void setSound(@ParamName("isOpen") boolean isOpen, @ParamName("song") String song);

}
