package noppes.npcs.api.overlay;

import java.util.Collection;

import noppes.npcs.api.INbt;
import noppes.npcs.api.gui.ILabel;
import noppes.npcs.api.gui.ITexturedRect;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.item.IItemStack;

public interface IOverlay {

	int getId();

	void setLinkSide(@ParamName("type") int type);

	int getLinkSide();

	ILabel addLabel(@ParamName("id") int id, @ParamName("text") String text, @ParamName("x") int x, @ParamName("y") int y);

	ITexturedRect addTexturedRect(@ParamName("id") int id, @ParamName("y") String texture, @ParamName("x") int x, @ParamName("y") int y,
								  @ParamName("width") int width, @ParamName("height") int height);

	ITexturedRect addTexturedRectCrop(@ParamName("id") int id, @ParamName("texture") String texture, @ParamName("x") int x, @ParamName("y") int y,
									  @ParamName("width") int width, @ParamName("height") int height, @ParamName("textureX") int textureX, @ParamName("textureY") int textureY);

	ITexturedRect addTexturedRectCrop(@ParamName("id") int id, @ParamName("texture") String texture, @ParamName("x") int x, @ParamName("y") int y,
									  @ParamName("width") int width, @ParamName("height") int height, @ParamName("textureX") int textureX, @ParamName("textureY") int textureY,
									  @ParamName("textureMaxX") int textureMaxX, @ParamName("textureMaxY") int textureMaxY);

	IOverlayComponent getComponent(@ParamName("id") int id);

	IRenderItemOverlay addRenderItem(@ParamName("id") int id, @ParamName("x") int x, @ParamName("y") int y, @ParamName("item") IItemStack item);

	Collection<IOverlayComponent> getComponents();

	void removeComponent(@ParamName("id") int id);

	void clear();

	INbt save();

	void load(@ParamName("nbt") INbt nbt);

}
