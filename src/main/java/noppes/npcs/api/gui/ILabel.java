package noppes.npcs.api.gui;

import noppes.npcs.api.interfaces.ParamName;

public interface ILabel extends ICustomGuiComponent {

	String getText();

	ILabel setText(@ParamName("label") String label);

	int getColor();

	ILabel setColor(@ParamName("color") int color);

	float getScale();

	ILabel setScale(@ParamName("scale") float scale);

	boolean getCentered();

	ILabel setCentered(@ParamName("bo") boolean bo);

	// New from Unofficial (BetaZavr)
	boolean isShadow();

	void setShadow(@ParamName("showShadow") boolean showShadow);

	IComponent getMCText();

	ILabel setMCText(@ParamName("component") IComponent component);

}
