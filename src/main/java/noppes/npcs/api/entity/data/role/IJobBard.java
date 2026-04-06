package noppes.npcs.api.entity.data.role;

import noppes.npcs.api.interfaces.ParamName;

public interface IJobBard {

	String getSong();

	void setSong(@ParamName("song") String song);

}
