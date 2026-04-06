package noppes.npcs.api.entity.data;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.handler.data.IAvailability;

public interface IMark {

   IAvailability getAvailability();

   int getColor();

   void setColor(@ParamName("color") int color);

   int getType();

   void setType(@ParamName("type") int type);

    boolean is3D();

    boolean isRotate();

    void set3D(@ParamName("bo") boolean bo);

    void setRotate(@ParamName("rotateIn") boolean rotateIn);

    void update();

}
