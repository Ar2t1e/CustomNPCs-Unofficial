package noppes.npcs.api.gui;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;

public interface ICustomGui extends IComponentsWrapper {

	int getId();

	int getWidth();

	int getHeight();

	void setSize(@ParamName("width") int width, @ParamName("height") int height);

	void setDoesPauseGame(@ParamName("pauseGame") boolean pauseGame);

	void setClosesOnEsc(@ParamName("bo") boolean bo);

	void setBackgroundTexture(@ParamName("resourceLocation") String resourceLocation);

	void update();

	void update(@ParamName("component") ICustomGuiComponent component);

	IComponentsScrollableWrapper getScrollingPanel();

	void openSubGui(@ParamName("gui") ICustomGui gui);

	CustomGuiWrapper getSubGuiWrapper();

	ICustomGui closeSubGui();

	void close();

	ICustomGui getParentGui();

	ICustomGui getRootGui();

	ICustomGui getActiveGui();

	IPlayer<?> getPlayer();

}
