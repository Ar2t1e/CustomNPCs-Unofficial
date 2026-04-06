package noppes.npcs.api.gui;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.functions.gui.GuiComponentClicked;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonWrapper;

public interface IButton extends ICustomGuiComponent {

   String getLabel();

   IButton setLabel(@ParamName("label") String label);

   ITexturedRect getTextureRect();

   void setTextureRect(@ParamName("rect") ITexturedRect rect);

   /** @deprecated */
   @Deprecated
   String getTexture();

   /** @deprecated */
   @Deprecated
   boolean hasTexture();

   /** @deprecated */
   @Deprecated
   IButton setTexture(@ParamName("texture") String texture);

   /** @deprecated */
   @Deprecated
   int getTextureX();

   /** @deprecated */
   @Deprecated
   int getTextureY();

   /** @deprecated */
   @Deprecated
   IButton setTextureOffset(@ParamName("textureX") int textureX, @ParamName("textureY") int textureY);

   int getTextureHoverOffset();

   IButton setTextureHoverOffset(@ParamName("height") int height);

   IItemStack getDisplayItem();

   IButton setDisplayItem(@ParamName("item") IItemStack item);

   IButton setOnPress(@ParamName("onPress") GuiComponentClicked<IButton> onPress);

   // New from Unofficial (BetaZavr)

   IComponent getMCLabel();

   IButton setMCLabel(@ParamName("component") IComponent component);

}
