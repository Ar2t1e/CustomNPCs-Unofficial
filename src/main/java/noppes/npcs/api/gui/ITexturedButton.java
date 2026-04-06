package noppes.npcs.api.gui;

import noppes.npcs.api.interfaces.ParamName;

public interface ITexturedButton extends IButton {

   String getTexture();

   ITexturedButton setTexture(@ParamName("texture") String texture);

   int getTextureX();

   int getTextureY();

   ITexturedButton setTextureOffset(@ParamName("textureX") int textureX, @ParamName("textureY") int textureY);

}
