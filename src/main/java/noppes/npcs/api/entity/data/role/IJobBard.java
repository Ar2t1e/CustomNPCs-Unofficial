package noppes.npcs.api.entity.data.role;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.entity.data.INPCJob;

public interface IJobBard extends INPCJob {

   String getSong();

   void setSong(@ParamName("resourceSound") String resourceSound);

}
