package noppes.npcs.client.model.animation;

import java.util.*;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.INbt;
import noppes.npcs.api.constants.AnimationKind;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.data.IAnimation;
import noppes.npcs.api.entity.data.IAnimationFrame;
import noppes.npcs.api.wrapper.NBTWrapper;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketNpcCustomAnimation;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.ValueUtil;

public class AnimationConfig implements IAnimation {

    public static final AnimationConfig EMPTY;
    static {
        EMPTY = new AnimationConfig();
        EMPTY.frames.put(0, AnimationFrameConfig.EMPTY);
        EMPTY.resetTicks();
    }

    public String name = "Default Animation";
    public int repeatLast = 0;
    public final Map<Integer, AnimationFrameConfig> frames = new TreeMap<>(); // [ Frame ID, Frame setting ]
    public final Map<Integer, List<AddedPartConfig>> addParts = new TreeMap<>(); // [ Parent Frame ID, added Part setting list ]
    public final Map<Integer, Integer> endingFrameTicks = new TreeMap<>(); // ticks info
    public int totalTicks = 0;

    public int id = -1;
    public AnimationKind type = AnimationKind.STANDING;
    public float chance = 1.0f;
    public boolean immutable;
    public int editTick = 0;
    public int editFrame = 0;

    public AnimationConfig() { frames.put(0, new AnimationFrameConfig(0)); }

    @Override
    public IAnimationFrame addFrame() {
        int f = frames.size();
        frames.put(f, new AnimationFrameConfig(f));
        if (f == 0) { frames.get(f).isNowDamage = true; }
        return frames.get(f);
    }

    @Override
    public IAnimationFrame addFrame(int frameId, IAnimationFrame frame) {
        if (frame == null) { return addFrame(); }
        if (frameId < 0) {
            frameId = frames.size();
            frames.put(frameId, ((AnimationFrameConfig) frame).copy());
            frames.get(frameId).id = frameId;
        } else {
            Map<Integer, AnimationFrameConfig> newFrames = new TreeMap<>();
            int j = 0;
            for (int i : frames.keySet()) {
                if (i == frameId) {
                    newFrames.put(j, ((AnimationFrameConfig) frame).copy());
                    newFrames.get(j).id = j;
                    j++;
                }
                newFrames.put(j, frames.get(i));
                newFrames.get(j).id = j;
                j++;
            }
            frames.clear();
            frames.putAll(newFrames);
        }
        frames.get(frameId).isNowDamage = frames.size() == 1;
        return frames.get(frameId);
    }

    public AnimationConfig copy() {
        AnimationConfig ac = new AnimationConfig();
        ac.load(save());
        ac.resetTicks();
        return ac;
    }

    @Override
    public IAnimationFrame getFrame(int frameId) {
        if (!frames.containsKey(frameId)) {
            throw new CustomNPCsException("Unknown frame " + frameId);
        }
        return frames.get(frameId);
    }

    @Override
    public IAnimationFrame[] getFrames() {
        IAnimationFrame[] fs = new IAnimationFrame[frames.size()];
        for (int id : frames.keySet()) {
            fs[id] = frames.get(id);
        }
        return fs;
    }

    @Override
    public int getId() { return id; }

    @Override
    public float getChance() { return chance; }

    @Override
    public String getName() { return name; }

    @Override
    public INbt getNbt() { return new NBTWrapper(save()); }

    @Override
    public int getRepeatLast() { return repeatLast; }

    public MutableComponent getSettingName() {
        return Component.empty()
                .append(Component.literal("ID:"))
                .append(Component.literal(id + " ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(name).withStyle(ChatFormatting.RESET));
    }

    @Override
    public boolean hasFrame(int frameId) { return frames.containsKey(frameId); }

    public void load(CompoundTag compound) {
        frames.clear();

        id = compound.getInt("ID");
        name = compound.getString("Name");
        boolean hasDelayAttack = false;
        for (int i = 0; i < compound.getList("FrameConfigs", 10).size(); i++) {
            AnimationFrameConfig afc = new AnimationFrameConfig();
            afc.load(compound.getList("FrameConfigs", 10).getCompound(i));
            afc.id = i;
            if (!hasDelayAttack && afc.isNowDamage()) { hasDelayAttack = true; }
            frames.put(i, afc);
        }
        if (frames.isEmpty()) { frames.put(0, new AnimationFrameConfig(0)); }
        if (!hasDelayAttack) { frames.get(0).isNowDamage = true; }

        addParts.clear();
        for (int i = 0, id = 8; i < compound.getList("AddedParts", 10).size(); i++, id++) {
            AddedPartConfig addPart = new AddedPartConfig();
            addPart.load(compound.getList("AddedParts", 10).getCompound(i));
            addPart.id = id;
            if (!addParts.containsKey(addPart.parentPart)) { addParts.put(addPart.parentPart, new ArrayList<>()); }
            addParts.get(addPart.parentPart).add(addPart);
        }

        if (compound.contains("Chance", 5)) { setChance(compound.getFloat("Chance")); }
        if (compound.contains("Immutable", 1)) { immutable = compound.getBoolean("Immutable"); }
        if (compound.contains("Type", 3)) { type = AnimationKind.get(compound.getInt("Type")); }
        if (compound.contains("RepeatLast", 3)) { setRepeatLast(compound.getInt("RepeatLast")); }

        if (compound.contains("DamageHitbox", 9) && compound.getList("DamageHitbox", 6).size() == 6) { // OLD
            AnimationDamageHitbox aDHB = new AnimationDamageHitbox(0);

            ListTag listO = compound.getList("OffsetHitbox", 5);
            for (int j = 0; j < 3 && j < listO.size(); j++) { aDHB.offset[j] = listO.getFloat(j); }
            ListTag listS = compound.getList("ScaleHitbox", 5);
            for (int j = 0; j < 3 && j < listS.size(); j++) { aDHB.scale[j] = listS.getFloat(j); }
            int tTicks = 0;
            for (AnimationFrameConfig aFC : frames.values()) {
                tTicks += aFC.speed;
                if (aFC.isNowDamage()) {
                    aFC.damageDelay = tTicks;
                    if (aFC.damageHitboxes.isEmpty()) { aFC.damageHitboxes.put(0, aDHB); }
                    break;
                }
                tTicks += aFC.delay;
            }
        }
        CustomNpcs.proxy.loadAnimationModel(this);
    }

    @Override
    public void removeFrame(IAnimationFrame frame) {
        if (frame == null || frames.size() <= 1) {
            return;
        }
        for (int f : frames.keySet()) {
            if (frames.get(f).equals(frame)) {
                removeFrame(f);
                return;
            }
        }
    }

    @Override
    public void removeFrame(int frameId) {
        if (!frames.containsKey(frameId)) {
            throw new CustomNPCsException("Unknown frame ID:" + frameId);
        }
        Map<Integer, AnimationFrameConfig> newData = new TreeMap<>();
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
            if (newData.isEmpty()) { newData.put(0, new AnimationFrameConfig(0)); }
            frames.putAll(newData);
        }
    }

    @Override
    public void setName(String nameIn) { name = nameIn == null || nameIn.isEmpty() ? "Default Animation" : nameIn; }

    @Override
    public void setNbt(INbt nbt) { load(nbt.getMCNBT()); }

    @Override
    public void setRepeatLast(int framesIn) { repeatLast = ValueUtil.correctInt(framesIn, 0, frames.size()); }

    @Override
    public void setChance(float chanceIn) { chance = ValueUtil.onlyPositiveFloat(chanceIn, 1.0f); }

    public void startToNpc(EntityCustomNpc npcEntity) {
        if (npcEntity != null && npcEntity.modelData != null && npcEntity.modelData.entity == null) {
            npcEntity.animation.tryRunAnimation(this, type);
            npcEntity.level();
            if (!npcEntity.level().isClientSide()) {
                Packets.sendAll(new PacketNpcCustomAnimation(npcEntity.level().dimension(), getId(), 3));
            }
        }
    }

    @Override
    public void startToNpc(ICustomNpc<?> npc) {
        if (npc == null || !(npc.getMCEntity() instanceof EntityCustomNpc)) { throw new CustomNPCsException("NPC must not be null"); }
        startToNpc((EntityCustomNpc) npc.getMCEntity());
    }

    public CompoundTag save() {
        CompoundTag compound = new CompoundTag();
        ListTag listFC = new ListTag();
        Iterator<AnimationFrameConfig> setts = frames.values().iterator();
        while(setts.hasNext()) {
            try {
                AnimationFrameConfig afc = setts.next();
                listFC.add(afc.writeNBT());
            }
            catch (Exception e) {
                LogWriter.error(e);
                break;
            }
        }
        compound.put("FrameConfigs", listFC);
        compound.putInt("ID", id);
        compound.putInt("Type", type.get());
        compound.putInt("RepeatLast", repeatLast);
        compound.putString("Name", name);
        compound.putFloat("Chance", chance);
        compound.putBoolean("Immutable", immutable);

        ListTag listAP = new ListTag();
        for (int partId : addParts.keySet()) {
            for (AddedPartConfig addedPart : addParts.get(partId)) {
                listAP.add(addedPart.save());
            }
        }
        compound.put("AddedParts", listAP);

        return compound;
    }

    public void resetTicks() {
        totalTicks = 0;
        endingFrameTicks.clear();
        if (this == EMPTY) {
            totalTicks = AnimationFrameConfig.EMPTY.speed + AnimationFrameConfig.EMPTY.delay + 1;
            endingFrameTicks.put(0, totalTicks);
            return;
        }
        for (Integer id : frames.keySet()) {
            AnimationFrameConfig frame = frames.get(id);
            if (frame.speed < 1) { frame.speed = 1; }
            totalTicks += frame.speed;
            if (frame.isNowDamage()) {
                frame.damageDelay = totalTicks;
            }
            totalTicks += frame.delay;
            endingFrameTicks.put(id, totalTicks);
        }
        if (totalTicks == 0) { totalTicks = 1; }
    }

    public boolean hasEmotion() {
        for (AnimationFrameConfig frame : frames.values()) {
            if (frame.emotionId >= 0) { return true; }
        }
        return false;
    }

    public List<AABB> getDamageHitboxes(LivingEntity npc, int frameID) {
        List<AABB> list = new ArrayList<>();
        if (!frames.containsKey(frameID)) { return list; }
        for (AnimationDamageHitbox aDHb : frames.get(frameID).damageHitboxes.values()) {
            list.add(aDHb.getScaledDamageHitbox(npc));
        }
        return list;
    }

    public int getAnimationFrameByTime(long ticks) {
        if (type == AnimationKind.EDITING_PART) { return editFrame; }
        if (ticks >= 0) {
            if (endingFrameTicks.isEmpty() && !frames.isEmpty()) { resetTicks(); }
            for (int id : endingFrameTicks.keySet()) {
                if (ticks <= endingFrameTicks.get(id)) { return id; }
            }
            return frames.size();
        }
        return -1;
    }

    public AddedPartConfig getAddedPart(int id) {
        for (List<AddedPartConfig> list : addParts.values()) {
            for (AddedPartConfig addedPart : list) {
                if (addedPart.id == id) { return addedPart; }
            }
        }
        return null;
    }

    public void removeAddedPart(AddedPartConfig addedPartConfig) {
        if (addedPartConfig == null) { return; }
        int addedPartId = addedPartConfig.id;
        boolean bo = false;
        if (addParts.containsKey(addedPartConfig.parentPart)) {
            bo = addParts.get(addedPartConfig.parentPart).remove(addedPartConfig);
            if (!bo) {
                for (AddedPartConfig addedPart : addParts.get(addedPartConfig.parentPart)) {
                    if (addedPart.id == addedPartId) {
                        bo = addParts.get(addedPartConfig.parentPart).remove(addedPart);
                        break;
                    }
                }
            }
        }
        if (!bo) { removeAddedPart(addedPartId); }
        if (bo) {
            for (AnimationFrameConfig frame : frames.values()) {
                frame.parts.remove(addedPartId);
            }
        }
    }

    public void removeAddedPart(int addedPartId) {
        boolean bo = false;
        for (int partId : addParts.keySet()) {
            for (AddedPartConfig addedPart : addParts.get(partId)) {
                if (addedPart.id == addedPartId) {
                    bo = addParts.get(partId).remove(addedPart);
                    break;
                }
            }
        }
        if (bo) {
            for (AnimationFrameConfig frame : frames.values()) {
                frame.parts.remove(addedPartId);
            }
        }
    }

}
