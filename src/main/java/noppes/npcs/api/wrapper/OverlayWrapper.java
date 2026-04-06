package noppes.npcs.api.wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.INbt;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.gui.IGuiTimer;
import noppes.npcs.api.gui.IItemSlot;
import noppes.npcs.api.gui.ILabel;
import noppes.npcs.api.gui.ITexturedRect;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.overlay.IOverlay;
import noppes.npcs.api.wrapper.gui.*;

public class OverlayWrapper implements IOverlay {

	Map<Integer, ICustomGuiComponent> components = new TreeMap<>();
	private int id;

	public OverlayWrapper(int idIn) { id = idIn; }

	@Override
	public void load(INbt compound) {
		if (compound == null) { return; }
		id = compound.getInteger("id");
		Map<Integer, ICustomGuiComponent> newComponents = new TreeMap<>();
		NBTTagList list = compound.getMCNBT().getTagList("components", 10);
		for (int i = 0; i < list.tagCount(); i++) {
			ICustomGuiComponent component = CustomGuiComponentWrapper.createFromNBT(list.getCompoundTagAt(i));
			if (component != null) { newComponents.put(component.getId(), component); }
		}
		components = newComponents;
	}

	@Override
	public int getId() { return id; }

	@Override
	public INbt save() {
		INbt compound = new NBTWrapper(new NBTTagCompound());
		compound.setInteger("id", id);
		NBTTagList list = new NBTTagList();
		for (Map.Entry<Integer, ICustomGuiComponent> entry : components.entrySet()) { list.appendTag(entry.getValue().save()); }
		compound.mcSetTag("components", list);
		return compound;
	}

	@Override
	public IItemSlot addItemSlot(int id, int orientationType, int x, int y) { return addItemSlot(id, orientationType, x, y, ItemScriptedWrapper.AIR); }

	@Override
	public IItemSlot addItemSlot(int id, int orientationType, int x, int y, IItemStack stack) {
		CustomGuiItemSlotWrapper slot = new CustomGuiItemSlotWrapper(x, y, stack);
		slot.setOrientationType(orientationType);
		components.put(components.size(), slot);
		return slot;
	}

	@Override
	public ILabel addLabel(int id, int orientationType, String title, int x, int y, int width, int height) {
		if (width <= 0 || height <= 0) {
			throw new CustomNPCsException("Invalid component width or height: [" + width + ", " + height + "]");
		}
		CustomGuiLabelWrapper label = new CustomGuiLabelWrapper(id, title, x, y, width, height);
		components.put(id, label);
		return label;
	}

	@Override
	public ILabel addLabel(int id, int orientationType, String title, int x, int y, int width, int height, int color) {
		if (width <= 0 || height <= 0) {
			throw new CustomNPCsException("Invalid component width or height: [" + width + ", " + height + "]");
		}
		CustomGuiLabelWrapper label = new CustomGuiLabelWrapper(id, title, x, y, width, height, color);
		components.put(id, label);
		return label;
	}

	@Override
	public ITexturedRect addTexturedRect(int id, int orientationType, String texture, int x, int y, int width, int height) {
		if (width <= 0 || height <= 0) {
			throw new CustomNPCsException("Invalid component width or height: [" + width + ", " + height + "]");
		}
		CustomGuiTexturedRectWrapper txtr = new CustomGuiTexturedRectWrapper(id, texture, x, y, width, height);
		components.put(id, txtr);
		return txtr;
	}

	@Override
	public ITexturedRect addTexturedRect(int id, int orientationType, String texture, int x, int y, int width, int height, int textureX, int textureY) {
		if (width <= 0 || height <= 0) {
			throw new CustomNPCsException("Invalid component width or height: [" + width + ", " + height + "]");
		}
		CustomGuiTexturedRectWrapper txtr = new CustomGuiTexturedRectWrapper(id, texture, x, y, width, height, textureX, textureY);
		components.put(id, txtr);
		return txtr;
	}

	@Override
	public IGuiTimer addTimer(int id, int orientationType, long start, long end, int x, int y, int width, int height) {
		if (width == 0 || height == 0) {
			throw new CustomNPCsException("Invalid component width or height: [" + width + ", " + height + "]");
		}
		CustomGuiTimerWrapper timer = new CustomGuiTimerWrapper(id, start, end, x, y, width, height);
		components.put(id, timer);
		return timer;
	}

	@Override
	public IGuiTimer addTimer(int id, int orientationType, long start, long end, int x, int y, int width, int height, int color) {
		if (width == 0 || height == 0) {
			throw new CustomNPCsException("Invalid component width or height: [" + width + ", " + height + "]");
		}
		CustomGuiTimerWrapper timer = new CustomGuiTimerWrapper(id, start, end, x, y, width, height, color);
		components.put(id, timer);
		return timer;
	}

	@Override
	public void clear() { components.clear(); }

	@Override
	public ICustomGuiComponent getComponent(int componentId) { return components.get(componentId); }

	@Override
	public List<ICustomGuiComponent> getComponents() { return new ArrayList<>(components.values()); }

	@Override
	public List<ICustomGuiComponent> getComponents(int orientationType) {
		List<ICustomGuiComponent> list = new ArrayList<>();
		for (ICustomGuiComponent component : components.values()) {
			if (component.getOrientationType() == orientationType) { list.add(component); }
		}
		return list;
	}

	@Override
	public boolean removeComponent(int componentId) { return components.remove(componentId) != null; }


}
