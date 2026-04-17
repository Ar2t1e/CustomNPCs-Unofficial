package noppes.npcs.api.wrapper;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Collection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.api.INbt;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.overlay.IOverlayLabel;
import noppes.npcs.api.overlay.IOverlay;
import noppes.npcs.api.overlay.IOverlayComponent;
import noppes.npcs.api.overlay.IRenderItemOverlay;
import noppes.npcs.api.overlay.IOverlayTexturedRect;

public class OverlayWrapper implements IOverlay {

	private final Int2ObjectOpenHashMap<IOverlayComponent> components = new Int2ObjectOpenHashMap<>();
	private int id;
	private int linkSide = 5;

	public OverlayWrapper(int idIn) { id = idIn; }

	@Override
	public Collection<IOverlayComponent> getComponents() { return components.values(); }

	@Override
	public IOverlayLabel addLabel(int id, String text, int x, int y) {
		IOverlayLabel label = new OverlayLabelWrapper(id, x, y, text);
		components.put(id, label);
		return label;
	}

	@Override
	public IOverlayTexturedRect addTexturedRect(int id, String texture, int x, int y, int width, int height) {
		IOverlayTexturedRect rect = new OverlayTexturedRectWrapper(id, x, y, texture, width, height);
		components.put(id, rect);
		return rect;
	}

	@Override
	public IOverlayTexturedRect addTexturedRectCrop(int id, String texture, int x, int y, int width, int height, int textureX, int textureY) {
		IOverlayTexturedRect rect = new OverlayTexturedRectWrapper(id, x, y, texture, width, height, textureX, textureY);
		components.put(id, rect);
		return rect;
	}

	@Override
	public IOverlayTexturedRect addTexturedRectCrop(int id, String texture, int x, int y, int width, int height, int textureX, int textureY, int textureMaxX, int textureMaxY) {
		IOverlayTexturedRect rect = new OverlayTexturedRectWrapper(id, x, y, texture, width, height, textureX, textureY, textureMaxX, textureMaxY);
		components.put(id, rect);
		return rect;
	}

	@Override
	public IOverlayComponent getComponent(int id) { return components.get(id); }

	@Override
	public IRenderItemOverlay addRenderItem(int id, int x, int y, IItemStack item) {
		IRenderItemOverlay itemOverlay = new OverlayRenderItemWrapper(id, x, y, item);
		components.put(id, itemOverlay);
		return itemOverlay;
	}

	@Override
	public void removeComponent(int id) { components.remove(id); }

	@Override
	public void clear() { components.clear(); }

	@Override
	public int getId() { return id; }

	@Override
	public void setLinkSide(int side) { linkSide = Math.min(9, Math.max(1, side)); }

	@Override
	public int getLinkSide() { return linkSide; }

	@Override
	public void load(INbt iNbt) {
		id = iNbt.getInteger("id");
		linkSide = iNbt.getInteger("linkSide");
		components.clear();
		NBTTagList list = iNbt.getMCNBT().getTagList("components", 10);
		for(int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			int type = compound.getInteger("type");
			IOverlayComponent component;
			switch(type) {
				case 0:
					component = new OverlayLabelWrapper(0, 0, 0, "");
					break;
				case 1:
					component = new OverlayTexturedRectWrapper(0, 0, 0, "", 0, 0);
					break;
				case 2:
					component = new OverlayRenderItemWrapper(0, 0, 0, null);
					break;
				default:
					continue;
			}

			component.fromNbt(iNbt);
			components.put(component.getId(), component);
		}

	}

	@Override
	public INbt save() {
		INbt compound = new NBTWrapper(new NBTTagCompound());
		compound.setInteger("id", id);
		compound.setInteger("linkSide", linkSide);
		NBTTagList list = new NBTTagList();
		for (IOverlayComponent component : components.values()) {
			INbt iNbt = new NBTWrapper(new NBTTagCompound());
			component.toNbt(iNbt);
			list.appendTag(iNbt.getMCNBT());
		}
		compound.mcSetTag("components", list);
		return compound;
	}

}
