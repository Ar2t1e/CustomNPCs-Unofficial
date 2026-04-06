package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.api.gui.ICustomGuiComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class CustomGuiComponentWrapper implements ICustomGuiComponent {

	public static CustomGuiComponentWrapper createFromNBT(NBTTagCompound nbt) {
		switch (nbt.getInteger("type")) {
			case 0: return (new CustomGuiButtonWrapper()).fromNBT(nbt);
			case 1: return (new CustomGuiLabelWrapper()).fromNBT(nbt);
			case 2: return (new CustomGuiTexturedRectWrapper()).fromNBT(nbt);
			case 3: return (new CustomGuiTextFieldWrapper()).fromNBT(nbt);
			case 4: return (new CustomGuiScrollWrapper()).fromNBT(nbt);
			case 5: return (new CustomGuiItemSlotWrapper()).fromNBT(nbt);
			case 6: return (new CustomGuiTextAreaWrapper()).fromNBT(nbt);
			case 7: return (new CustomGuiButtonListWrapper()).fromNBT(nbt);
			case 8: return (new CustomGuiSliderWrapper()).fromNBT(nbt);
			case 9: return (new CustomGuiEntityDisplayWrapper()).fromNBT(nbt);
			case 10: return (new CustomGuiAssetsSelectorWrapper()).fromNBT(nbt);
			case 11: return (new CustomGuiColoredLineWrapper()).fromNBT(nbt);
			case 12: return (new CustomGuiItemRendererWrapper()).fromNBT(nbt);
			default: return null;
		}
	}

	protected int id;
	protected int posX;
	protected int posY;
	protected int width;
	protected int height;
	protected List<Component> hoverText = new ArrayList<>();
	protected boolean enabled = true;
	protected boolean visible = true;
	protected UUID uniqueId = UUID.randomUUID();
	public boolean disablePackets = false;

	// New from Unofficial (BetaZavr)
	private int offsetType = 0;
	private final int[] offsets = new int[] { 0, 0 };

	public CustomGuiComponentWrapper setDisablePackets() {
		disablePackets = true;
		return this;
	}

	@Override
	public int getId() { return id; }

	@Override
	public CustomGuiComponentWrapper setId(int idIn) {
		id = idIn;
		return this;
	}

	@Override
	public boolean getEnabled() {
		return enabled;
	}

	@Override
	public CustomGuiComponentWrapper setEnabled(boolean bo) {
		enabled = bo;
		return this;
	}

	@Override
	public boolean getVisible() { return visible; }

	public CustomGuiComponentWrapper setVisible(boolean bo) {
		visible = bo;
		return this;
	}

	@Override
	public UUID getUniqueID() {
		return uniqueId;
	}

	@Override
	public int getPosX() {
		return posX;
	}

	@Override
	public int getPosY() {
		return posY;
	}

	@Override
	public CustomGuiComponentWrapper setPos(int x, int y) {
		posX = x;
		posY = y;
		return this;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public CustomGuiComponentWrapper setSize(int widthIn, int heightIn) {
		width = widthIn;
		height = heightIn;
		return this;
	}

	@Override
	public boolean hasHoverText() { return !hoverText.isEmpty(); }

	@Override
	public String[] getHoverText() {
		String[] ht = new String[hoverText.size()];
		for(int i = 0; i < hoverText.size(); ++i) { ht[i] = hoverText.get(i).getFormattedText(); }
		return ht;
	}

	public List<Component> getHoverTextList() { return hoverText; }

	@Override
	public CustomGuiComponentWrapper setHoverText(String text) {
		hoverText = new ArrayList<>();
		hoverText.add(Component.translatable(text));
		return this;
	}

	public CustomGuiComponentWrapper setHoverText(String[] text) {
		hoverText = new ArrayList<>();
		for (String obj : text) { hoverText.add(Component.translatable(obj)); }
		return this;
	}

	public CustomGuiComponentWrapper setHoverText(List<Object> list) {
		hoverText = new ArrayList<>();
		for (Object obj : list) {
			if (obj instanceof Component) { hoverText.add((Component) obj); }
			else { hoverText.add(Component.translatable(String.valueOf(obj))); }
		}
		return this;
	}

	public NBTTagCompound toNBT(NBTTagCompound nbt) {
		nbt.setInteger("id", id);
		nbt.setBoolean("enabled", enabled);
		nbt.setBoolean("visible", visible);
		nbt.setUniqueId("uniqueId", uniqueId);
		nbt.setIntArray("pos", new int[]{ posX, posY });
		nbt.setIntArray("size", new int[]{ width, height });
		if (hoverText != null) {
			NBTTagList list = new NBTTagList();
			for (Component component : hoverText) { list.appendTag(new NBTTagString(ITextComponent.Serializer.componentToJson(component))); }
			if (list.tagCount() > 0) { nbt.setTag("hover", list); }
		}
		nbt.setInteger("type", getType());
		return nbt;
	}

	public CustomGuiComponentWrapper fromNBT(NBTTagCompound nbt) {
		setId(nbt.getInteger("id"));
		setEnabled(nbt.getBoolean("enabled"));
		setVisible(nbt.getBoolean("visible"));
		uniqueId = nbt.getUniqueId("uniqueId");
		setPos(nbt.getIntArray("pos")[0], nbt.getIntArray("pos")[1]);
		setSize(nbt.getIntArray("size")[0], nbt.getIntArray("size")[1]);
		if (nbt.hasKey("hover")) {
			NBTTagList list = nbt.getTagList("hover", 8);
			String[] hoverText = new String[list.tagCount()];
			for (int i = 0; i < list.tagCount(); ++i) { hoverText[i] = ((NBTTagString) list.get(i)).getString(); }
			setHoverText(hoverText);
		}
		if (nbt.hasKey("hover", 9)) {
			NBTTagList list = nbt.getTagList("hover", 8);
			for (int i = 0; i < list.tagCount(); ++i) { hoverText.add(new Component(ITextComponent.Serializer.jsonToComponent(list.getStringTagAt(i)))); }
		}
		return this;
	}

	// New from Unofficial (BetaZavr)
	@Override
	public int getOffsetType() { return offsetType; }

	@Override
	public void offSet(int offsetType, double[] windowSize) {
		this.offsetType = offsetType;
		switch (offsetType) {
			case 1: { // left down
				this.offsets[0] = 0;
				this.offsets[1] = (int) windowSize[1];
				break;
			}
			case 2: { // right up
				this.offsets[0] = (int) windowSize[0];
				this.offsets[1] = 0;
				break;
			}
			case 3: { // right down
				this.offsets[0] = (int) windowSize[0];
				this.offsets[1] = (int) windowSize[1];
				break;
			}
			case 4: { // center
				this.offsets[0] = (int) (windowSize[0] / 2.0d);
				this.offsets[1] = (int) (windowSize[1] / 2.0d);
				break;
			}
			default: { // left up
				this.offsets[0] = 0;
				this.offsets[1] = 0;
			}
		}
	}

}
