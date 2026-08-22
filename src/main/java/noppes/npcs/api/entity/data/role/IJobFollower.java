package noppes.npcs.api.entity.data.role;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.data.INPCJob;

@SuppressWarnings("unused")
public interface IJobFollower extends INPCJob {

	String getFollowing();

	ICustomNpc<?> getFollowingNpc();

	boolean isFollowing();

	void setFollowing(@ParamName("name") String name);

}
