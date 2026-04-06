package noppes.npcs.api.gui;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.functions.gui.GuiComponentUpdate;

public interface ISlider extends ICustomGuiComponent {

   float getValue();

   ISlider setValue(@ParamName("value") float value);

   String getFormat();

   ISlider setFormat(@ParamName("format") String format);

   float getMin();

   ISlider setMin(@ParamName("min") float min);

   float getMax();

   ISlider setMax(@ParamName("max") float max);

   int getDecimals();

   ISlider setDecimals(@ParamName("decimals") int decimals);

   ISlider setOnChange(@ParamName("onChange") GuiComponentUpdate<ISlider> onChange);

}
