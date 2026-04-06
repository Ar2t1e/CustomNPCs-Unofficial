package noppes.npcs.api.gui;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.functions.gui.GuiComponentUpdate;

public interface ITextField extends ICustomGuiComponent {

	String getText();

	ITextField setText(@ParamName("text") String text);

	int getColor();

	ITextField setColor(@ParamName("color") int color);

	ITextField setOnChange(@ParamName("onChange") GuiComponentUpdate<ITextField> onChange);

	ITextField setOnFocusLost(@ParamName("onFocusChange") GuiComponentUpdate<ITextField> onFocusChange);

	ITextField setFocused(@ParamName("bo") boolean bo);

	boolean getFocused();

	ITextField setCharacterType(@ParamName("type") int type);

	int getCharacterType();

	int getInteger();

	ITextField setInteger(@ParamName("value") int value);

	float getFloat();

	ITextField setFloat(@ParamName("value") float value);

	ITextField setMinMax(@ParamName("min") int min, @ParamName("max") int max);

}
