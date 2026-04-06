package noppes.npcs.api.wrapper.gui;

import java.util.Objects;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.functions.gui.GuiComponentUpdate;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.api.gui.ITextField;

public class CustomGuiTextFieldWrapper extends CustomGuiComponentWrapper implements ITextField {

	private int color = 14737632;
	private int type = 0;
	private String text = "";
	private boolean focused = false;
	private GuiComponentUpdate<ITextField> onChange = null;
	private GuiComponentUpdate<ITextField> onFocusLost = null;
	private int min = Integer.MIN_VALUE;
	private int max = Integer.MAX_VALUE;

	public CustomGuiTextFieldWrapper() { }

	public CustomGuiTextFieldWrapper(int id, int x, int y, int width, int height) {
		setId(id);
		setPos(x, y);
		setSize(width, height);
	}

	@Override
	public String getText() { return text; }

	public CustomGuiTextFieldWrapper setText(Object obj) { return setText(obj == null ? "" : obj.toString() ); }

	@Override
	public CustomGuiTextFieldWrapper setText(String textIn) {
		String prevText = text;
		text = Objects.requireNonNull(textIn, "");
		if (!text.isEmpty() && (getCharacterType() == 1 || getCharacterType() == 2)) {
			try { setInteger(getInteger()); }
			catch (NumberFormatException var4) { text = prevText; }
		}
		return this;
	}

	@Override
	public int getInteger() {
		if (type == 0) { throw new CustomNPCsException("Character Type 0 doesnt convert to integer"); }
		if (text.isEmpty()) { return Math.max(min, 0); }
		return type == 1 ? Integer.parseInt(text) : Integer.parseInt(text, 16);
	}

	@Override
	public CustomGuiTextFieldWrapper setInteger(int value) {
		if (type == 0) { throw new CustomNPCsException("Character Type 0 doesnt support setInteger"); }
		value = Math.max(min, value);
		value = Math.min(max, value);
		if (type == 1 || type == 3) {
			text = "" + value;
		}
		if (type == 2) {
			text = String.format("%01x", value);
		}
		return this;
	}

	public float getFloat() {
		if (type == 0) {
			throw new CustomNPCsException("Character Type 0 doesnt convert to float");
		} else if (text.isEmpty()) {
			return (float)Math.max(min, 0);
		} else if (type == 1) {
			return (float)Integer.parseInt(text);
		} else {
			return type == 2 ? (float)Integer.parseInt(text, 16) : Float.parseFloat(text);
		}
	}

	public CustomGuiTextFieldWrapper setFloat(float value) {
		if (type != 0 && type != 2) {
			value = Math.max((float)min, value);
			value = Math.min((float)max, value);
			if (type == 1) {
				text = "" + value;
			}
			return this;
		} else {
			throw new CustomNPCsException("Character Type 0 doesnt support setFloat");
		}
	}

	public int getColor() { return color; }

	public CustomGuiTextFieldWrapper setColor(int colorIn) {
		color = colorIn;
		return this;
	}

	public CustomGuiTextFieldWrapper setFocused(boolean bo) {
		focused = bo;
		return this;
	}

	public boolean getFocused() { return focused; }

	public CustomGuiTextFieldWrapper setCharacterType(int typeIn) {
		type = typeIn;
		return this;
	}

	public int getCharacterType() { return type; }

	public CustomGuiTextFieldWrapper setMinMax(int minIn, int maxIn) {
		if (type == 0) { throw new CustomNPCsException("Character Type 0 doesnt support setInteger"); }
		min = minIn;
		max = maxIn;
		return this;
	}

	@Override
	public int getType() { return getElementType().get(); }

	public GuiComponentType getElementType() { return GuiComponentType.TEXT_FIELD; }

	@Override
	public NBTTagCompound toNBT(NBTTagCompound nbt) {
		super.toNBT(nbt);
		nbt.setString("default", text);
		nbt.setBoolean("focused", focused);
		nbt.setInteger("color", color);
		nbt.setInteger("character_type", type);
		nbt.setInteger("min", min);
		nbt.setInteger("max", max);
		return nbt;
	}

	@Override
	public CustomGuiComponentWrapper fromNBT(NBTTagCompound nbt) {
		super.fromNBT(nbt);
		setText(nbt.getString("default"));
		setFocused(nbt.getBoolean("focused"));
		setColor(nbt.getInteger("color"));
		setCharacterType(nbt.getInteger("character_type"));
		min = nbt.getInteger("min");
		max = nbt.getInteger("max");
		return this;
	}

	@Override
	public CustomGuiTextFieldWrapper setOnChange(GuiComponentUpdate<ITextField> onChangeIn) {
		onChange = onChangeIn;
		return this;
	}

	@Override
	public CustomGuiTextFieldWrapper setOnFocusLost(GuiComponentUpdate<ITextField> onFocusChange) {
		onFocusLost = onFocusChange;
		return this;
	}

	public final void onChange(ICustomGui gui) {
		if (onChange != null) { onChange.onChange(gui, this); }
	}

	public final void onFocusLost(ICustomGui gui) {
		if (onFocusLost != null) { onFocusLost.onChange(gui, this); }
	}

	public void tick() { }

}
