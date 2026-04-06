package noppes.npcs.api.gui;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.wrapper.gui.CustomGuiTexturedRectWrapper;

public interface ITexturedRect extends ICustomGuiComponent {

	String getTexture();

	ITexturedRect setTexture(@ParamName("texture") String texture);

	float getScale();

	ITexturedRect setScale(@ParamName("scale") float scale);

	int getTextureX();

	int getTextureY();

	CustomGuiTexturedRectWrapper setTextureMaxSize(@ParamName("width") int width, @ParamName("height") int height);

	ITexturedRect setTextureOffset(@ParamName("offsetX") int offsetX, @ParamName("offsetY") int offsetY);

	ITexturedRect setRepeatingTexture(@ParamName("width") int width, @ParamName("height") int height, @ParamName("borderSize") int borderSize);

	int getTextureMaxX();

	int getTextureMaxY();

}
