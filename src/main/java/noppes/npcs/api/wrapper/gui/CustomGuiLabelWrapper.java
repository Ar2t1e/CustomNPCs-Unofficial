package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.gui.IComponent;
import noppes.npcs.api.gui.ILabel;

import java.awt.*;

public class CustomGuiLabelWrapper extends CustomGuiComponentWrapper implements ILabel {

	protected IComponent label = ComponentWrapper.of("");
	protected int color = new Color(0x404040).getRGB();
	protected float scale = 1.0F;
	protected boolean centered = false;

	// New from Unofficial (BetaZavr)
	boolean showShadow = false;

	public CustomGuiLabelWrapper() { }

	public CustomGuiLabelWrapper(int id, String label, int x, int y, int width, int height) {
		setId(id);
		setText(label);
		setPos(x, y);
		setSize(width, height);
	}

	public CustomGuiLabelWrapper(int id, String label, int x, int y, int width, int height, int color) {
		this(id, label, x, y, width, height);
		setColor(color);
	}

	@Override
	public String getText() { return label.getString(); }

	@Override
	public CustomGuiLabelWrapper setText(String labelIn) {
		label = ComponentWrapper.of(labelIn);
		return this;
	}

	@Override
	public int getColor() { return color; }

	@Override
	public CustomGuiLabelWrapper setColor(int colorIn) {
		color = colorIn;
		return this;
	}

	@Override
	public float getScale() { return scale; }

	@Override
	public CustomGuiLabelWrapper setScale(float scaleIn) {
		scale = scaleIn;
		return this;
	}

	@Override
	public boolean getCentered() { return centered; }

	@Override
	public CustomGuiLabelWrapper setCentered(boolean bo) {
		centered = bo;
		return this;
	}

	@Override
	public int getType() { return GuiComponentType.LABEL.get(); }

	@Override
	public NBTTagCompound toNBT(NBTTagCompound compound) {
		super.toNBT(compound);
		compound.setString("label", label.toJson());
		compound.setInteger("color", color);
		compound.setFloat("scale", scale);
		compound.setBoolean("centered", centered);
		compound.setBoolean("shadow", showShadow);
		return compound;
	}

	@Override
	public CustomGuiComponentWrapper fromNBT(NBTTagCompound compound) {
		super.fromNBT(compound);
		setText(compound.getString("label"));
		setColor(compound.getInteger("color"));
		setScale(compound.getFloat("scale"));
		setCentered(compound.getBoolean("centered"));
		showShadow = compound.getBoolean("shadow");
		return this;
	}

	// New from Unofficial (BetaZavr)
	@Override
	public boolean isShadow() { return showShadow; }

	@Override
	public void setShadow(boolean showShadowIn) { showShadow = showShadowIn; }

	@Override
	public IComponent getMCText() { return label; }

	@Override
	public CustomGuiLabelWrapper setMCText(IComponent component) {
		if (component == null) { label = ComponentWrapper.of(""); }
		else { label = component; }
		return this;
	}

}
