package noppes.npcs.api.wrapper.gui;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.functions.gui.GuiComponentClicked;
import noppes.npcs.api.gui.IButton;
import noppes.npcs.api.gui.IComponent;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.api.gui.ITexturedRect;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;

import java.util.Objects;

public class CustomGuiButtonWrapper extends CustomGuiComponentWrapper implements IButton {

	protected IComponent label = ComponentWrapper.of("");
	protected int textureHoverOffset = -1;
	protected IItemStack item = ItemStackWrapper.AIR;
	protected CustomGuiTexturedRectWrapper texture = new CustomGuiTexturedRectWrapper();
	protected GuiComponentClicked<IButton> onPress = null;

	public CustomGuiButtonWrapper() { }

	public CustomGuiButtonWrapper(int id, String label, int x, int y) {
		setId(id);
		setLabel(label);
		setPos(x, y);
		texture.setId(id);
		texture.setSize(getWidth(), getHeight());
		texture.setRepeatingTexture(200, 20, 3);
		texture.setTexture("textures/gui/widgets.png");
		texture.setTextureOffset(0, 46);
		setTextureHoverOffset(20);
	}

	public CustomGuiButtonWrapper(int id, String label, int x, int y, int width, int height) {
		this(id, label, x, y);
		setSize(width, height);
	}

	public CustomGuiButtonWrapper(int id, String label, int x, int y, int width, int height, String textureIn) {
		this(id, label, x, y, width, height);
		setTexture(textureIn);
		texture.setRepeatingTexture(width, height, 3);
		texture.setTextureOffset(0, 0);
		setTextureHoverOffset(height);
	}

	public CustomGuiButtonWrapper(int id, String label, int x, int y, int width, int height, String texture, int textureX, int textureY) {
		this(id, label, x, y, width, height, texture);
		setTextureOffset(textureX, textureY);
	}

	@Override
	public CustomGuiButtonWrapper setSize(int width, int height) {
		super.setSize(width, height);
		texture.setSize(width, height);
		if (textureHoverOffset <= 0) { textureHoverOffset = height; }
		return this;
	}

	@Override
	public int getTextureHoverOffset() { return textureHoverOffset; }

	@Override
	public IButton setTextureHoverOffset(int height) {
		textureHoverOffset = height;
		return this;
	}

	@Override
	public String getLabel() { return label.getString(); }

	@Override
	public IButton setLabel(String labelIn) {
		label = ComponentWrapper.of(labelIn);
		return this;
	}

	@Override
	public CustomGuiTexturedRectWrapper getTextureRect() { return texture; }

	@Override
	public void setTextureRect(ITexturedRect rect) { texture = (CustomGuiTexturedRectWrapper)rect; }

	@Override
	public String getTexture() { return texture.getTexture(); }

	@Override
	public boolean hasTexture() { return texture != null; }

	@Override
	public IButton setTexture(String textureIn) {
		texture.setTexture(textureIn);
		return this;
	}

	@Override
	public int getTextureX() { return texture.getTextureX(); }

	@Override
	public int getTextureY() { return texture.getTextureY(); }

	@Override
	public IButton setTextureOffset(int textureX, int textureY) {
		texture.setTextureOffset(textureX, textureY);
		return this;
	}

	@Override
	public int getType() { return GuiComponentType.BUTTON.get(); }

	@Override
	public IItemStack getDisplayItem() { return item; }

	@Override
	public IButton setDisplayItem(IItemStack itemIn) {
		if (itemIn == null) { item = ItemStackWrapper.AIR; }
		else { item = itemIn; }
		return this;
	}

	@Override
	public NBTTagCompound toNBT(NBTTagCompound nbt) {
		super.toNBT(nbt);
		nbt.setTag("texture", texture.toNBT(new NBTTagCompound()));
		nbt.setInteger("textureHoverOffset", textureHoverOffset);
		nbt.setString("label", label.toJson());
		nbt.setTag("item", item.getItemNbt().getMCNBT());
		return nbt;
	}

	@Override
	public CustomGuiComponentWrapper fromNBT(NBTTagCompound nbt) {
		super.fromNBT(nbt);
		setSize(nbt.getIntArray("size")[0], nbt.getIntArray("size")[1]);
		setTextureHoverOffset(nbt.getInteger("textureHoverOffset"));
		setLabel(nbt.getString("label"));
		texture.fromNBT(nbt.getCompoundTag("texture"));
		ItemStack it = new ItemStack(nbt.getCompoundTag("item"));
		item = Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(it);
		return this;
	}

	@Override
	public CustomGuiButtonWrapper setOnPress(GuiComponentClicked<IButton> onPressIn) {
		onPress = onPressIn;
		return this;
	}

	public final void onPress(ICustomGui gui) {
		if (onPress != null) { onPress.onClick(gui, this); }
	}

	// New from Unofficial (BetaZavr)
	@Override
	public IComponent getMCLabel() { return label; }

	@Override
	public CustomGuiButtonWrapper setMCLabel(IComponent component) {
		if (component == null) { label = ComponentWrapper.of(""); }
		else { label = component; }
		return this;
	}

}
