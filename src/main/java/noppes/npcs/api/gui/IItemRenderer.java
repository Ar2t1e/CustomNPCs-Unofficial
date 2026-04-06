package noppes.npcs.api.gui;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.item.IItemStack;

public interface IItemRenderer extends ICustomGuiComponent {

   boolean hasStack();

   IItemStack getStack();

   IItemRenderer setStack(@ParamName("stack") IItemStack stack);

   int getWidth();

   int getHeight();

   IItemRenderer setHoverBox(@ParamName("width") int width, @ParamName("height") int height);

   float getScale();

   IItemRenderer setScale(@ParamName("scale") float scale);

}
