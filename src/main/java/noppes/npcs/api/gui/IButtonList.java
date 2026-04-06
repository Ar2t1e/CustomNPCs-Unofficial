package noppes.npcs.api.gui;

import noppes.npcs.api.interfaces.ParamName;

public interface IButtonList extends IButton {

   IButtonList setValues(@ParamName("values") String... values);

   String[] getValues();

   IButtonList setSelected(@ParamName("selected") int selected);

   int getSelected();

   ITexturedRect getLeftTexture();

   ITexturedRect getRightTexture();

}
