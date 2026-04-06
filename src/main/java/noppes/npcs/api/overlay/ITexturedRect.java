package noppes.npcs.api.overlay;

import noppes.npcs.api.interfaces.ParamName;

public interface ITexturedRect extends IOverlayComponent {

   String getTexture();

   ITexturedRect setTexture(@ParamName("texture") String texture);

   int getWidth();

   ITexturedRect setWidth(@ParamName("width") int width);

   int getHeight();

   ITexturedRect setHeight(@ParamName("height") int height);

   float[] getUV();

   ITexturedRect setUV(@ParamName("x1") float x1, @ParamName("y1") float y1, @ParamName("x2") float x2, @ParamName("y2") float y2);

   ITexturedRect setRGB(@ParamName("red") float red, @ParamName("green") float green, @ParamName("blue") float blue, @ParamName("alpha") float alpha);

   float[] getRGB();

   int getTextureX();

   int getTextureY();

   int getTextureMaxX();

   int getTextureMaxY();

   ITexturedRect setTextureOffset(@ParamName("offsetX") int offsetX, @ParamName("offsetY") int offsetY);

   ITexturedRect setTextureMaxSize(@ParamName("textureMaxX") int textureMaxX, @ParamName("textureMaxY") int textureMaxY);

}
