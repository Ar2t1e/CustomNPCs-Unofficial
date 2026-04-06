package noppes.npcs.api.entity.data;

import noppes.npcs.api.interfaces.ParamName;

public interface ILine {

   String getText();

   void setText(@ParamName("text") String text);

   String getSound();

   void setSound(@ParamName("sound") String sound);

   boolean getShowText();

   void setShowText(@ParamName("show") boolean show);

}
