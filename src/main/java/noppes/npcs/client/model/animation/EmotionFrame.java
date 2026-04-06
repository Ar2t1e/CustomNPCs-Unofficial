package noppes.npcs.client.model.animation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
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

    public void readFromNBT(CompoundTag compound) {
        if (compound.isEmpty()) { return; }
        id = compound.getInt("Part");
        setSpeed(compound.getInt("Speed"));
        setEndDelay(compound.getInt("EndDelay"));
        smooth = compound.getBoolean("IsSmooth");
        disable = compound.getBoolean("IsDisable");
        blink = compound.getBoolean("IsBlink");
        endBlink = compound.getBoolean("IsEndBlink");
        rndMouth = compound.getBoolean("IsRandomMouth");
        showMouth = compound.getBoolean("ShowMouth");

        rotMouth = compound.getFloat("RotationMouth");
        ListTag listRotEye = compound.getList("RotationEye", 5);
        ListTag listOffEye = compound.getList("OffsetEye", 5);
        ListTag listScEye = compound.getList("ScaleEye", 5);
        ListTag listRotBrow = compound.getList("RotationBrow", 5);
        ListTag listOffBrow = compound.getList("OffsetBrow", 5);
        ListTag listScBrow = compound.getList("ScaleBrow", 5);
        ListTag listRotPupil = compound.getList("RotationPupil", 5);
        ListTag listOffPupil = compound.getList("OffsetPupil", 5);
        ListTag listScPupil = compound.getList("ScalePupil", 5);
        ListTag listOffMouth = compound.getList("OffsetMouth", 5);
        ListTag listScMouth = compound.getList("ScaleMouth", 5);

        int max = listRotEye.size();
        if (max < listOffEye.size()) { max = listOffEye.size(); }
        if (max < listScEye.size()) { max = listScEye.size(); }
        if (max < rotEye.length) { max = rotEye.length; }
        if (max < offsetEye.length) { max = offsetEye.length; }
        if (max < scaleEye.length) { max = scaleEye.length; }
        for (int i = 0; i < max; i++) {
            if (i < rotEye.length && i < listRotEye.size()) { rotEye[i] = listRotEye.getFloat(i); }
            if (i < offsetEye.length && i < listOffEye.size()) { offsetEye[i] = listOffEye.getFloat(i); }
            if (i < scaleEye.length && i < listScEye.size()) { scaleEye[i] = listScEye.getFloat(i); }
            if (i < rotBrow.length && i < listRotBrow.size()) { rotBrow[i] = listRotBrow.getFloat(i); }
            if (i < offsetBrow.length && i < listOffBrow.size()) { offsetBrow[i] = listOffBrow.getFloat(i); }
            if (i < scaleBrow.length && i < listScBrow.size()) { scaleBrow[i] = listScBrow.getFloat(i); }
            if (i < rotPupil.length && i < listRotPupil.size()) { rotPupil[i] = listRotPupil.getFloat(i); }
            if (i < offsetPupil.length && i < listOffPupil.size()) { offsetPupil[i] = listOffPupil.getFloat(i); }
            if (i < scalePupil.length && i < listScPupil.size()) { scalePupil[i] = listScPupil.getFloat(i); }
            if (i < offsetMouth.length && i < listOffMouth.size()) { offsetMouth[i] = listOffMouth.getFloat(i); }
            if (i < scaleMouth.length && i < listScMouth.size()) { scaleMouth[i] = listScMouth.getFloat(i); }
        }

    }

    public CompoundTag writeToNBT() {
        final CompoundTag compound = getCompound();
        ListTag listRotEye = new ListTag();
        ListTag listOffEye = new ListTag();
        ListTag listScEye = new ListTag();
        ListTag listRotBrow = new ListTag();
        ListTag listOffBrow = new ListTag();
        ListTag listScBrow = new ListTag();
        ListTag listRotPupil = new ListTag();
        ListTag listOffPupil = new ListTag();
        ListTag listScPupil = new ListTag();
        ListTag listRotMouth = new ListTag();
        ListTag listOffMouth = new ListTag();
        ListTag listScMouth = new ListTag();
        int max = rotEye.length;
        if (max < offsetEye.length) { max = offsetEye.length; }
        if (max < scaleEye.length) { max = scaleEye.length; }
        for (int i = 0; i < max; i++) {
            if (i < rotEye.length) { listRotEye.add(FloatTag.valueOf(rotEye[i])); }
            if (i < offsetEye.length) { listOffEye.add(FloatTag.valueOf(offsetEye[i])); }
            if (i < scaleEye.length) { listScEye.add(FloatTag.valueOf(scaleEye[i])); }
            if (i < rotBrow.length) { listRotBrow.add(FloatTag.valueOf(rotBrow[i])); }
            if (i < offsetBrow.length) { listOffBrow.add(FloatTag.valueOf(offsetBrow[i])); }
            if (i < scaleBrow.length) { listScBrow.add(FloatTag.valueOf(scaleBrow[i])); }
            if (i < rotPupil.length) { listRotPupil.add(FloatTag.valueOf(rotPupil[i])); }
            if (i < offsetPupil.length) { listOffPupil.add(FloatTag.valueOf(offsetPupil[i])); }
            if (i < scalePupil.length) { listScPupil.add(FloatTag.valueOf(scalePupil[i])); }
            if (i < offsetMouth.length) { listOffMouth.add(FloatTag.valueOf(offsetMouth[i])); }
            if (i < scaleMouth.length) { listScMouth.add(FloatTag.valueOf(scaleMouth[i])); }
        }
        compound.put("RotationEye", listRotEye);
        compound.put("OffsetEye", listOffEye);
        compound.put("ScaleEye", listScEye);
        compound.put("RotationBrow", listRotBrow);
        compound.put("OffsetBrow", listOffBrow);
        compound.put("ScaleBrow", listScBrow);
        compound.put("RotationPupil", listRotPupil);
        compound.put("OffsetPupil", listOffPupil);
        compound.put("ScalePupil", listScPupil);
        compound.put("RotationMouth", listRotMouth);
        compound.put("OffsetMouth", listOffMouth);
        compound.put("ScaleMouth", listScMouth);

        return compound;
    }

    private CompoundTag getCompound() {
        CompoundTag compound = new CompoundTag();
        compound.putInt("Part", id);
        compound.putInt("Speed", speed);
        compound.putInt("EndDelay", delay);
        compound.putBoolean("IsSmooth", smooth);
        compound.putBoolean("IsDisable", disable);
        compound.putBoolean("IsBlink", blink);
        compound.putBoolean("IsEndBlink", endBlink);
        compound.putBoolean("IsRandomMouth", rndMouth);
        compound.putBoolean("ShowMouth", showMouth);
        compound.putFloat("RotationMouth", rotMouth);
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
