package noppes.npcs.api.entity.data.role;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.entity.data.INPCJob;

public interface IJobPuppet extends INPCJob {

    boolean getIsAnimated();

    void setIsAnimated(@ParamName("bo") boolean bo);

    int getAnimationSpeed();

    void setAnimationSpeed(@ParamName("speed") int speed);

    IJobPuppet.IJobPuppetPart getPart(@ParamName("part") int part);

    interface IJobPuppetPart {
        int getRotationX();

        int getRotationY();

        int getRotationZ();

        void setRotation(@ParamName("x") int x, @ParamName("y") int y, @ParamName("z") int z);
    }

}
