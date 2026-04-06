package noppes.npcs.api.gui;

import noppes.npcs.api.interfaces.ParamName;

import java.util.UUID;

public interface ICustomGuiComponent {

   int getId();

   ICustomGuiComponent setId(@ParamName("id") int id);

   UUID getUniqueID();

   int getPosX();

   int getPosY();

   ICustomGuiComponent setPos(@ParamName("x") int x, @ParamName("y") int y);

   int getWidth();

   int getHeight();

   ICustomGuiComponent setSize(@ParamName("width") int width, @ParamName("height") int height);

   boolean hasHoverText();

   String[] getHoverText();

   ICustomGuiComponent setHoverText(@ParamName("text") String text);

   ICustomGuiComponent setHoverText(@ParamName("texts") String[] texts);

   boolean getEnabled();

   ICustomGuiComponent setEnabled(@ParamName("bo") boolean bo);

   boolean getVisible();

   ICustomGuiComponent setVisible(@ParamName("bo") boolean bo);

   int getType();

   // New from Unofficial (BetaZavr)
   int getOffsetType();

   void offSet(@ParamName("offsetType") int offsetType, @ParamName("windowSize") double[] windowSize);

}
