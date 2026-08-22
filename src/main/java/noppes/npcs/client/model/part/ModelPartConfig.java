package noppes.npcs.client.model.part;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.util.ValueUtil;

public class ModelPartConfig {

	public boolean notShared = false;
	public float scaleX = 1.0F;
	public float scaleY = 1.0F;
	public float scaleZ = 1.0F;
	public float transX = 0.0F;
	public float transY = 0.0F;
	public float transZ = 0.0F;

	public NBTTagCompound save() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setFloat("ScaleX", scaleX);
		compound.setFloat("ScaleY", scaleY);
		compound.setFloat("ScaleZ", scaleZ);
		compound.setFloat("TransX", transX);
		compound.setFloat("TransY", transY);
		compound.setFloat("TransZ", transZ);
		compound.setBoolean("NotShared", notShared);
		return compound;
	}

	public void load(NBTTagCompound compound) {
		scaleX = ValueUtil.correctFloat(compound.getFloat("ScaleX"), 0.5f, 1.5f);
		scaleY = ValueUtil.correctFloat(compound.getFloat("ScaleY"), 0.5f, 1.5f);
		scaleZ = ValueUtil.correctFloat(compound.getFloat("ScaleZ"), 0.5f, 1.5f);
		transX = ValueUtil.correctFloat(compound.getFloat("TransX"), -1.0f, 1.0f);
		transY = ValueUtil.correctFloat(compound.getFloat("TransY"), -1.0f, 1.0f);
		transZ = ValueUtil.correctFloat(compound.getFloat("TransZ"), -1.0f, 1.0f);
		notShared = compound.getBoolean("NotShared");
	}

	public void setScale(float x, float y) {
		scaleX = ValueUtil.correctFloat(x, 0.5f, 1.5f);
		scaleY = ValueUtil.correctFloat(x, 0.5f, 1.5f);
		scaleZ = ValueUtil.correctFloat(y, 0.5f, 1.5f);
	}

	public void setScale(float x, float y, float z) {
		scaleX = ValueUtil.correctFloat(x, 0.5f, 1.5f);
		scaleY = ValueUtil.correctFloat(y, 0.5f, 1.5f);
		scaleZ = ValueUtil.correctFloat(z, 0.5f, 1.5f);
	}

	public void setTranslate(float x, float y, float z) {
		transX = ValueUtil.correctFloat(x, -1.0f, 1.0f);
		transY = ValueUtil.correctFloat(y, -1.0f, 1.0f);
		transZ = ValueUtil.correctFloat(z, -1.0f, 1.0f);
	}

	public void copyValues(ModelPartConfig config) {
		scaleX = config.scaleX;
		scaleY = config.scaleY;
		scaleZ = config.scaleZ;
		transX = config.transX;
		transY = config.transY;
		transZ = config.transZ;
	}

	public String toString() { return "ModelPartConfig {ScaleX: " + scaleX + "; ScaleY: " + scaleY + "; ScaleZ: " + scaleZ +
			"; transX: " + transX + "; transY: " + transY + "; transZ: " + transZ + "; notShared: " + notShared + "}"; }

}
