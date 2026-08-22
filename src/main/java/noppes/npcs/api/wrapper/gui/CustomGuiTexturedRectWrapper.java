package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.gui.ITexturedRect;

public class CustomGuiTexturedRectWrapper
		extends CustomGuiComponentWrapper
		implements ITexturedRect {

	int textureX = -1;
	int textureY = -1;
	float scale = 1.0F;
	String texture = "";
	public boolean hasRepeatingTexture = false;
	public int texRepWidth;
	public int texRepHeight;
	public int texRepBorderSize = 0;
	public int textureMaxX = -1;
	public int textureMaxY = -1;

	public CustomGuiTexturedRectWrapper() { }

	public CustomGuiTexturedRectWrapper(int id, String textureIn, int x, int y, int width, int height) {
		setId(id);
		setTexture(textureIn);
		setPos(x, y);
		setSize(width, height);
	}

	public CustomGuiTexturedRectWrapper(int id, String texture, int x, int y, int width, int height, int textureX, int textureY) {
		this(id, texture, x, y, width, height);
		setTextureOffset(textureX, textureY);
	}

	public CustomGuiTexturedRectWrapper(int id, String texture, int x, int y, int width, int height, int textureX, int textureY, int maxTextureX, int maxTextureY) {
		this(id, texture, x, y, width, height, textureX, textureY);
		setTextureMaxSize(maxTextureX, maxTextureY);
	}

	@Override
	public String getTexture() { return texture; }

	@Override
	public CustomGuiTexturedRectWrapper setTexture(String textureIn) {
		texture = textureIn;
		return this;
	}

	@Override
	public float getScale() { return scale; }

	@Override
	public CustomGuiTexturedRectWrapper setScale(float scaleIn) {
		scale = scaleIn;
		return this;
	}

	@Override
	public int getTextureX() { return textureX; }

	@Override
	public int getTextureY() { return textureY; }

	@Override
	public int getTextureMaxX() { return textureMaxX; }

	@Override
	public int getTextureMaxY() { return textureMaxY; }

	@Override
	public CustomGuiTexturedRectWrapper setTextureOffset(int offsetX, int offsetY) {
		textureX = offsetX;
		textureY = offsetY;
		return this;
	}

	@Override
	public CustomGuiTexturedRectWrapper setTextureMaxSize(int textureMaxXIn, int textureMaxYIn) {
		textureMaxX = textureMaxXIn;
		textureMaxY = textureMaxYIn;
		return this;
	}

	@Override
	public CustomGuiTexturedRectWrapper setRepeatingTexture(int width, int height, int borderSize) {
		hasRepeatingTexture = true;
		texRepWidth = width;
		texRepHeight = height;
		texRepBorderSize = borderSize;
		return this;
	}

	@Override
	public int getType() { return GuiComponentType.TEXTURED_RECT.get(); }

	@Override
	public NBTTagCompound toNBT(NBTTagCompound compound) {
		super.toNBT(compound);
		compound.setFloat("scale", scale);
		compound.setString("texture", texture);
		if (textureX >= 0 && textureY >= 0) {
			compound.setIntArray("texPos", new int[]{textureX, textureY});
		}
		if (textureMaxX >= 0 && textureMaxY >= 0) {
			compound.setIntArray("texPosMax", new int[]{textureMaxX, textureMaxY});
		}
		compound.setBoolean("hasRepeatingTexture", hasRepeatingTexture);
		if (hasRepeatingTexture) {
			compound.setIntArray("repeatingTexture", new int[]{texRepWidth, texRepHeight, texRepBorderSize});
		}
		return compound;
	}

	@Override
	public CustomGuiComponentWrapper fromNBT(NBTTagCompound compound) {
		super.fromNBT(compound);
		setScale(compound.getFloat("scale"));
		setTexture(compound.getString("texture"));
		int[] arr;
		if (compound.hasKey("texPos", 11)) {
			arr = compound.getIntArray("texPos");
			setTextureOffset(arr[0], arr[1]);
		}
		if (compound.hasKey("texPosMax", 11)) {
			arr = compound.getIntArray("texPosMax");
			setTextureMaxSize(arr[0], arr[1]);
		}
		hasRepeatingTexture = compound.getBoolean("hasRepeatingTexture");
		if (hasRepeatingTexture) {
			arr = compound.getIntArray("repeatingTexture");
			setRepeatingTexture(arr[0], arr[1], arr[2]);
		}
		return this;
	}

}
