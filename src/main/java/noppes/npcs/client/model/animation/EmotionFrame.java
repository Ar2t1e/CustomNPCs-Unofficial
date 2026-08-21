package noppes.npcs.client.model.animation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.api.entity.data.IEmotionPart;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.Map;

public class EmotionFrame
implements IEmotionPart {

	public static final EmotionFrame EMPTY = new EmotionFrame(0);
	public int id;
	public int speed = 20;
	public int delay = 0;
	public boolean smooth = true;
	public boolean disable = false;
	public boolean blink = false;
	public boolean endBlink = false;
	public float[] offsetEye = new float[] { 0.0f, 0.0f, 0.0f, 0.0f }; // [rightX, rightY, leftX, leftY]
	public float[] rotEye = new float[] { 0.0f, 0.0f }; // [right, left]
	public float[] scaleEye = new float[] { 1.0f, 1.0f, 1.0f, 1.0f }; // [rightX, rightY, leftX, leftY]
	
	public float[] offsetPupil = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
	public float[] rotPupil = new float[] { 0.0f, 0.0f };
	public float[] scalePupil = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	
	public float[] offsetBrow = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
	public float[] rotBrow = new float[] { 0.0f, 0.0f };
	public float[] scaleBrow = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

	public float[] offsetMouth = new float[] { 0.0f, 0.0f };
	public float rotMouth = 0.0f;
	public float[] scaleMouth = new float[] { 1.0f, 1.0f };
	public boolean rndMouth = false;
	public boolean showMouth = false;

	public EntityNPCInterface npc;
	
	public EmotionFrame(int idIn) { id = idIn; }

	@Override
	public boolean isBlink() { return blink; }
	
	@Override
	public boolean isEndBlink() { return endBlink; }

	@Override
	public void setBlink(boolean bo) { blink = bo; }
	@Override
	public void setEndBlink(boolean bo) { endBlink = bo; }
	
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasNoTags()) { return; }
		id = compound.getInteger("Part");
		setSpeed(compound.getInteger("Speed"));
		setEndDelay(compound.getInteger("EndDelay"));
		smooth = compound.getBoolean("IsSmooth");
		disable = compound.getBoolean("IsDisable");
		blink = compound.getBoolean("IsBlink");
		endBlink = compound.getBoolean("IsEndBlink");
		rndMouth = compound.getBoolean("IsRandomMouth");
		showMouth = compound.getBoolean("ShowMouth");
		
		rotMouth = compound.getFloat("RotationMouth");
		NBTTagList listRotEye = compound.getTagList("RotationEye", 5);
		NBTTagList listOffEye = compound.getTagList("OffsetEye", 5);
		NBTTagList listScEye = compound.getTagList("ScaleEye", 5);
		NBTTagList listRotBrow = compound.getTagList("RotationBrow", 5);
		NBTTagList listOffBrow = compound.getTagList("OffsetBrow", 5);
		NBTTagList listScBrow = compound.getTagList("ScaleBrow", 5);
		NBTTagList listRotPupil = compound.getTagList("RotationPupil", 5);
		NBTTagList listOffPupil = compound.getTagList("OffsetPupil", 5);
		NBTTagList listScPupil = compound.getTagList("ScalePupil", 5);
		NBTTagList listOffMouth = compound.getTagList("OffsetMouth", 5);
		NBTTagList listScMouth = compound.getTagList("ScaleMouth", 5);
		
		int max = listRotEye.tagCount();
		if (max < listOffEye.tagCount()) { max = listOffEye.tagCount(); }
		if (max < listScEye.tagCount()) { max = listScEye.tagCount(); }
		if (max < rotEye.length) { max = rotEye.length; }
		if (max < offsetEye.length) { max = offsetEye.length; }
		if (max < scaleEye.length) { max = scaleEye.length; }
		for (int i = 0; i < max; i++) {
			if (i < rotEye.length && i < listRotEye.tagCount()) { rotEye[i] = listRotEye.getFloatAt(i); }
			if (i < offsetEye.length && i < listOffEye.tagCount()) { offsetEye[i] = listOffEye.getFloatAt(i); }
			if (i < scaleEye.length && i < listScEye.tagCount()) { scaleEye[i] = listScEye.getFloatAt(i); }
			if (i < rotBrow.length && i < listRotBrow.tagCount()) { rotBrow[i] = listRotBrow.getFloatAt(i); }
			if (i < offsetBrow.length && i < listOffBrow.tagCount()) { offsetBrow[i] = listOffBrow.getFloatAt(i); }
			if (i < scaleBrow.length && i < listScBrow.tagCount()) { scaleBrow[i] = listScBrow.getFloatAt(i); }
			if (i < rotPupil.length && i < listRotPupil.tagCount()) { rotPupil[i] = listRotPupil.getFloatAt(i); }
			if (i < offsetPupil.length && i < listOffPupil.tagCount()) { offsetPupil[i] = listOffPupil.getFloatAt(i); }
			if (i < scalePupil.length && i < listScPupil.tagCount()) { scalePupil[i] = listScPupil.getFloatAt(i); }
			if (i < offsetMouth.length && i < listOffMouth.tagCount()) { offsetMouth[i] = listOffMouth.getFloatAt(i); }
			if (i < scaleMouth.length && i < listScMouth.tagCount()) { scaleMouth[i] = listScMouth.getFloatAt(i); }
		}
		
	}

	public NBTTagCompound writeToNBT() {
		final NBTTagCompound compound = getCompound();
		NBTTagList listRotEye = new NBTTagList();
		NBTTagList listOffEye = new NBTTagList();
		NBTTagList listScEye = new NBTTagList();
		NBTTagList listRotBrow = new NBTTagList();
		NBTTagList listOffBrow = new NBTTagList();
		NBTTagList listScBrow = new NBTTagList();
		NBTTagList listRotPupil = new NBTTagList();
		NBTTagList listOffPupil = new NBTTagList();
		NBTTagList listScPupil = new NBTTagList();
		NBTTagList listRotMouth = new NBTTagList();
		NBTTagList listOffMouth = new NBTTagList();
		NBTTagList listScMouth = new NBTTagList();
		int max = rotEye.length;
		if (max < offsetEye.length) { max = offsetEye.length; }
		if (max < scaleEye.length) { max = scaleEye.length; }
		for (int i = 0; i < max; i++) {
			if (i < rotEye.length) { listRotEye.appendTag(new NBTTagFloat(rotEye[i])); }
			if (i < offsetEye.length) { listOffEye.appendTag(new NBTTagFloat(offsetEye[i])); }
			if (i < scaleEye.length) { listScEye.appendTag(new NBTTagFloat(scaleEye[i])); }
			if (i < rotBrow.length) { listRotBrow.appendTag(new NBTTagFloat(rotBrow[i])); }
			if (i < offsetBrow.length) { listOffBrow.appendTag(new NBTTagFloat(offsetBrow[i])); }
			if (i < scaleBrow.length) { listScBrow.appendTag(new NBTTagFloat(scaleBrow[i])); }
			if (i < rotPupil.length) { listRotPupil.appendTag(new NBTTagFloat(rotPupil[i])); }
			if (i < offsetPupil.length) { listOffPupil.appendTag(new NBTTagFloat(offsetPupil[i])); }
			if (i < scalePupil.length) { listScPupil.appendTag(new NBTTagFloat(scalePupil[i])); }
			if (i < offsetMouth.length) { listOffMouth.appendTag(new NBTTagFloat(offsetMouth[i])); }
			if (i < scaleMouth.length) { listScMouth.appendTag(new NBTTagFloat(scaleMouth[i])); }
		}
		compound.setTag("RotationEye", listRotEye);
		compound.setTag("OffsetEye", listOffEye);
		compound.setTag("ScaleEye", listScEye);
		compound.setTag("RotationBrow", listRotBrow);
		compound.setTag("OffsetBrow", listOffBrow);
		compound.setTag("ScaleBrow", listScBrow);
		compound.setTag("RotationPupil", listRotPupil);
		compound.setTag("OffsetPupil", listOffPupil);
		compound.setTag("ScalePupil", listScPupil);
		compound.setTag("RotationMouth", listRotMouth);
		compound.setTag("OffsetMouth", listOffMouth);
		compound.setTag("ScaleMouth", listScMouth);
		
		return compound;
	}

	private NBTTagCompound getCompound() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("Part", id);
		compound.setInteger("Speed", speed);
		compound.setInteger("EndDelay", delay);
		compound.setBoolean("IsSmooth", smooth);
		compound.setBoolean("IsDisable", disable);
		compound.setBoolean("IsBlink", blink);
		compound.setBoolean("IsEndBlink", endBlink);
		compound.setBoolean("IsRandomMouth", rndMouth);
		compound.setBoolean("ShowMouth", showMouth);
		compound.setFloat("RotationMouth", rotMouth);
		return compound;
	}

	@Override
	public int getSpeed() { return speed; }

	@Override
	public int getEndDelay() { return delay; }

	@Override
	public boolean isSmooth() { return smooth; }

	@Override
	public void setEndDelay(int ticks) {
		if (ticks < 0) { ticks *= -1; }
		if (ticks > 1200) { ticks = 1200; }
		delay = ticks;
	}

	@Override
	public void setSmooth(boolean isSmooth) { smooth = isSmooth; }

	@Override
	public void setSpeed(int ticks) {
		if (ticks < 0) { ticks *= -1; }
		if (ticks > 1200) { ticks = 1200; }
		speed = ticks;
	}

	@Override
	public boolean isDisabled() { return disable; }

	@Override
	public void setDisable(boolean bo) { disable = bo; }

	public EmotionFrame copy() {
		EmotionFrame newEf = new EmotionFrame(0);
		newEf.readFromNBT(writeToNBT());
		return newEf;
	}

	public void clear() {
		speed = 20;
		delay = 0;
		smooth = true;
		disable = false;
		blink = false;
		endBlink = false;
		offsetEye = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
		rotEye = new float[] { 0.0f, 0.0f };
		scaleEye = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
		offsetPupil = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
		rotPupil = new float[] { 0.0f, 0.0f };
		scalePupil = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
		offsetBrow = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };
		rotBrow = new float[] { 0.0f, 0.0f };
		scaleBrow = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
		offsetMouth = new float[] { 0.0f, 0.0f};
		rotMouth = 0.0f;
		scaleMouth = new float[] { 1.0f, 1.0f };
		rndMouth = false;
		showMouth = false;
	}

	public void resetFrom(Map<Integer, Float[]> rotationAngles, EmotionFrame currentFrame) {
		speed = currentFrame.speed;
		smooth = currentFrame.smooth;
		disable = currentFrame.disable;
		blink = currentFrame.blink;
		rotMouth = currentFrame.rotMouth;
		rndMouth = currentFrame.rndMouth;
		showMouth = currentFrame.showMouth;

		Float[] eyeRight = rotationAngles.get(0); // ofsX, ofsY, scX, scY, rot
		offsetEye[0] = eyeRight[0];
		offsetEye[1] = eyeRight[1];
		scaleEye[0] = eyeRight[2];
		scaleEye[1] = eyeRight[3];
		rotEye[0] = eyeRight[4];
		Float[] eyeLeft = rotationAngles.get(1);
		offsetEye[2] = eyeLeft[0];
		offsetEye[3] = eyeLeft[1];
		scaleEye[2] = eyeLeft[2];
		scaleEye[3] = eyeLeft[3];
		rotEye[1] = eyeLeft[4];

		Float[] pupilRight = rotationAngles.get(2); // ofsX, ofsY, scX, scY, rot
		if (pupilRight != null) {
			offsetPupil[0] = pupilRight[0];
			offsetPupil[1] = pupilRight[1];
			scalePupil[0] = pupilRight[2];
			scalePupil[1] = pupilRight[3];
			rotPupil[0] = pupilRight[4];
		}
		Float[] pupilLeft = rotationAngles.get(3);
		if (pupilLeft != null) {
			offsetPupil[2] = pupilLeft[0];
			offsetPupil[3] = pupilLeft[1];
			scalePupil[2] = pupilLeft[2];
			scalePupil[3] = pupilLeft[3];
			rotPupil[1] = pupilLeft[4];
		}
		Float[] browRight = rotationAngles.get(4); // ofsX, ofsY, scX, scY, rot
		offsetBrow[0] = browRight[0];
		offsetBrow[1] = browRight[1];
		scaleBrow[0] = browRight[2];
		scaleBrow[1] = browRight[3];
		rotBrow[0] = browRight[4];
		Float[] browLeft = rotationAngles.get(5);
		offsetBrow[2] = browLeft[0];
		offsetBrow[3] = browLeft[1];
		scaleBrow[2] = browLeft[2];
		scaleBrow[3] = browLeft[3];
		rotBrow[1] = browLeft[4];

		Float[] mouth = rotationAngles.get(6); // ofsX, ofsY, scX, scY
		offsetMouth[0] = mouth[0];
		offsetMouth[1] = mouth[1];
		scaleMouth[0] = mouth[2];
		scaleMouth[1] = mouth[3];
		rotMouth = mouth[4];

	}
}
