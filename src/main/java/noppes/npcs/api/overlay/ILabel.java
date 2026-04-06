package noppes.npcs.api.overlay;

import noppes.npcs.api.interfaces.ParamName;

public interface ILabel extends IOverlayComponent {

   String getText();

   ILabel setText(@ParamName("label") String label);

   ILabel setCentered(@ParamName("bo") boolean bo);

   boolean isCentered();

   float getScale();

   void setScale(@ParamName("scale") float scale);

}
