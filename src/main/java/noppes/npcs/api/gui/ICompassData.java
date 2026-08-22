package noppes.npcs.api.gui;

import noppes.npcs.api.IPos;
import noppes.npcs.api.interfaces.ParamName;

@SuppressWarnings("all")
public interface ICompassData {

	int getDimensionID();

	String getName();

	String getNPCName();

	IPos getPos();

	int getRange();

	String getTitle();

	int getTaskType();

	boolean isCustomPoint();

	boolean getShowOfPlayer();

	boolean isShowDial();

	boolean isFlat();

	void setDimensionID(@ParamName("dimensionId") int dimensionId);

	void setName(@ParamName("name") String name);

	void setNPCName(@ParamName("npcName") String npcName);

	void setPos(@ParamName("X") int x, @ParamName("y") int y, @ParamName("Z") int z);

	void setPos(@ParamName("pos") IPos pos);

	void setRange(@ParamName("range") int range);

	void setIsCustomPoint(@ParamName("show") boolean show);

	void setShowOfPlayer(@ParamName("show") boolean show);

	void setShowDial(@ParamName("show") boolean show);

	void setIsFlat(@ParamName("flat") boolean flat);

	void setTitle(@ParamName("title") String title);

	void setType(@ParamName("type") int type);

}
