package noppes.npcs.client.model.animation;

import java.util.Map;
import java.util.TreeMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.entity.data.IEmotion;
import noppes.npcs.api.entity.data.IEmotionPart;

public class EmotionConfig
        implements IEmotion {

    public static final EmotionConfig EMPTY;
    static {
        EMPTY = new EmotionConfig();
        EMPTY.frames.put(0, EmotionFrame.EMPTY);
        EMPTY.resetTicks();
    }

    public final Map<Integer, EmotionFrame> frames;
    public int id = 0;
    public int repeatLast = 0;
    public String name = "Default Emotion";
    public boolean canBlink = true;

    public boolean immutable = false;
    public boolean isEdit = false;
    public final Map<Integer, Integer> endingFrameTicks = new TreeMap<>(); // ticks info
    public int totalTicks = 0;
    public int editFrame = -1;
    public float scaleMoveX = 1.0f;
    public float scaleMoveY = 1.0f;

    public EmotionConfig() {
        frames = new TreeMap<>();
        frames.put(0, new EmotionFrame(0));
    }

    public void read(CompoundTag nbtEmotion) {
        frames.clear();
        for (int i = 0; i < nbtEmotion.getList("FrameConfigs", 10).size(); i++) {
            EmotionFrame ef = new EmotionFrame(i);
            ef.readFromNBT(nbtEmotion.getList("FrameConfigs", 10).getCompound(i));
            ef.id = i;
            frames.put(i, ef);
        }
        if (frames.isEmpty()) { frames.put(0, new EmotionFrame(0)); }
        id = nbtEmotion.getInt("ID");
        name = nbtEmotion.getString("Name");
        repeatLast = nbtEmotion.getInt("EmotionRepeat");
        canBlink = nbtEmotion.getBoolean("CanBlink");
        if (nbtEmotion.contains("ScaleMoveX", 5)) {
            scaleMoveX = Math.max(0.05f, Math.min(1.25f, nbtEmotion.getFloat("ScaleMoveX")));
        }
        if (nbtEmotion.contains("ScaleMoveY", 5)) {
            scaleMoveY = Math.max(0.05f, Math.min(1.25f, nbtEmotion.getFloat("ScaleMoveY")));
        }
    }

    public CompoundTag save() {
        CompoundTag nbtEmotion = new CompoundTag();
        ListTag list = new ListTag();
        for (EmotionFrame ef : frames.values()) { list.add(ef.writeToNBT()); }
        nbtEmotion.put("FrameConfigs", list);
        nbtEmotion.putInt("ID", id);
        nbtEmotion.putString("Name", name);
        nbtEmotion.putInt("EmotionRepeat", repeatLast);
        nbtEmotion.putBoolean("CanBlink", canBlink);
        if (repeatLast < 0) { repeatLast = frames.size() - 1; }
        nbtEmotion.putFloat("ScaleMoveX", scaleMoveX);
        nbtEmotion.putFloat("ScaleMoveY", scaleMoveY);
        return nbtEmotion;
    }

    public String getName() { return name; }

    public EmotionConfig copy() {
        EmotionConfig ec = new EmotionConfig();
        ec.read(save());
        ec.resetTicks();
        return ec;
    }

    public String getSettingName() {
        String c = "" + ((char) 167);
        return c + "7" + id + ": " + c + "r" + name;
    }

    @Override
    public int getId() { return id; }

    @Override
    public boolean canBlink() { return canBlink; }

    @Override
    public void setCanBlink(boolean bo) { canBlink = bo; }

    @Override
    public IEmotionPart addFrame() {
        int f = frames.size();
        frames.put(f, new EmotionFrame(f));
        return frames.get(f);
    }

    @Override
    public IEmotionPart addFrame(IEmotionPart frame) {
        if (frame == null) { return addFrame(); }
        int f = frames.size();
        frames.put(f, ((EmotionFrame) frame).copy());
        frames.get(f).id = f;
        return frames.get(f);
    }

    @Override
    public boolean removeFrame(IEmotionPart frame) {
        if (frame == null || frames.size() <= 1) { return false; }
        for (int f : frames.keySet()) {
            if (frames.get(f).equals(frame)) {
                removeFrame(f);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeFrame(int frameId) {
        if (frames.size() <= 1) { return false; }
        if (!frames.containsKey(frameId)) {
            throw new CustomNPCsException("Unknown frame ID:" + frameId);
        }
        Map<Integer, EmotionFrame> newData = new TreeMap<>();
        int i = 0;
        boolean isDel = false;
        for (int f : frames.keySet()) {
            if (f == frameId) {
                isDel = true;
                continue;
            }
            newData.put(i, frames.get(f).copy());
            newData.get(i).id = i;
            i++;
        }
        if (isDel) {
            frames.clear();
            if (newData.isEmpty()) {
                newData.put(0, new EmotionFrame(0));
            }
            frames.putAll(newData);
        }
        return isDel;
    }

    public void resetTicks() {
        totalTicks = 0;
        endingFrameTicks.clear();
        if (this == EMPTY) {
            totalTicks = EmotionFrame.EMPTY.speed + EmotionFrame.EMPTY.delay + 1;
            endingFrameTicks.put(0, totalTicks);
            return;
        }
        for (Integer id : frames.keySet()) {
            EmotionFrame frame = frames.get(id);
            if (frame.speed < 1) { frame.speed = 1; }
            totalTicks += frame.speed + frame.delay;
            endingFrameTicks.put(id, totalTicks);
        }
        if (totalTicks == 0) { totalTicks = 1; }
    }

    public int getEmotionFrameByTime(int ticks) {
        if (ticks >= 0) {
            if (endingFrameTicks.isEmpty() && !frames.isEmpty()) { resetTicks(); }
            for (int id : endingFrameTicks.keySet()) {
                if (ticks <= endingFrameTicks.get(id)) { return id; }
            }
            return frames.size();
        }
        return -1;
    }
}
