package noppes.npcs.api.overlay;

import noppes.npcs.api.interfaces.ParamName;

public interface IOverlayLabel extends IOverlayComponent {

   String getText();

   IOverlayLabel setText(@ParamName("label") String label);

   IOverlayLabel setCentered(@ParamName("bo") boolean bo);

   boolean isCentered();

   float getScale();

   void setScale(@ParamName("scale") float scale);

}
