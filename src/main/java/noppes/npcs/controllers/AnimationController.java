package noppes.npcs.controllers;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.AnimationKind;
import noppes.npcs.api.handler.IAnimationHandler;
import noppes.npcs.client.model.animation.AnimationConfig;
import noppes.npcs.client.model.animation.EmotionConfig;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncRemove;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.Util;

public class AnimationController implements IAnimationHandler {

    protected final TreeMap<Integer, AnimationConfig> animations = new TreeMap<>();
    protected final TreeMap<Integer, EmotionConfig> emotions = new TreeMap<>();
    protected static AnimationController instance;

    protected int baseMaxAnimID = 0;

    public static AnimationController getInstance() {
        if (instance == null) { instance = new AnimationController(); }
        return instance;
    }

    @Override
    public AnimationConfig createNewAnim() {
        AnimationConfig ac = new AnimationConfig();
        ac.id = getUnusedAnimId();
        animations.put(ac.id, ac);
        return ac;
    }

    @Override
    public EmotionConfig createNewEmtn() {
        EmotionConfig ec = new EmotionConfig();
        ec.id = getUnusedEmtnId();
        emotions.put(ec.id, ec);
        return ec;
    }

    @Override
    public AnimationConfig getAnimation(int animationId) {
        if (animations.containsKey(animationId)) { return animations.get(animationId); }
        return null;
    }

    @Override
    public AnimationConfig getAnimation(String animationName) {
        for (AnimationConfig ac : animations.values()) {
            if (ac.getName().equalsIgnoreCase(animationName)) { return ac; }
        }
        return null;
    }

    @Override
    public EmotionConfig getEmotion(int emotionId) {
        if (emotions.containsKey(emotionId)) { return emotions.get(emotionId); }
        return null;
    }

    @Override
    public EmotionConfig getEmotion(String emotionName) {
        for (EmotionConfig ec : emotions.values()) {
            if (ec.getName().equalsIgnoreCase(emotionName)) { return ec; }
        }
        return null;
    }

    @Override
    public AnimationConfig[] getAnimations() { return animations.values().toArray(new AnimationConfig[0]); }

    public List<AnimationConfig> getAnimations(List<Integer> ids) {
        List<AnimationConfig> list = new ArrayList<>();
        if (ids == null || ids.isEmpty()) { return list; }
        for (AnimationConfig ac : animations.values()) {
            for (int id : ids) {
                if (ac.getId() == id) {
                    list.add(ac);
                    break;
                }
            }
        }
        return list;
    }

    public int getUnusedEmtnId() {
        int id = 0;
        for (int i : emotions.keySet()) {
            if (i != id) { break; }
            id = i + 1;
        }
        return id;
    }

    public int getUnusedAnimId() {
        int id = baseMaxAnimID;
        for (int i : animations.keySet()) {
            if (i != id) { break; }
            id = i + 1;
        }
        return id;
    }

    public AnimationConfig loadAnimation(CompoundTag nbtAnimation) {
        if (nbtAnimation == null || !nbtAnimation.contains("ID", 3) || nbtAnimation.getInt("ID") < 0) { return null; }
        AnimationConfig ac;
        if (animations.containsKey(nbtAnimation.getInt("ID"))) {
            ac = animations.get(nbtAnimation.getInt("ID"));
            ac.load(nbtAnimation);
            return ac;
        }
        ac = new AnimationConfig();
        ac.load(nbtAnimation);
        animations.put(ac.id, ac);
        return animations.get(nbtAnimation.getInt("ID"));
    }

    public EmotionConfig loadEmotion(CompoundTag nbtEmotion) {
        if (nbtEmotion == null || !nbtEmotion.contains("ID", 3) || nbtEmotion.getInt("ID") < 0) { return null; }
        if (emotions.containsKey(nbtEmotion.getInt("ID"))) {
            emotions.get(nbtEmotion.getInt("ID")).read(nbtEmotion);
            return emotions.get(nbtEmotion.getInt("ID"));
        }
        EmotionConfig ec = new EmotionConfig();
        ec.read(nbtEmotion);
        emotions.put(nbtEmotion.getInt("ID"), ec);
        return emotions.get(nbtEmotion.getInt("ID"));
    }


    public void loadAnimations() {
        CustomNpcs.debugData.start(null);
        LogWriter.info("Start load animations");
        boolean needSave = false;
        File animDir;
        animations.clear();
        loadDefaultAnimations();
        File emtnDir;
        emotions.clear();
        // check old data
        if (CustomNpcs.Dir != null) {
            File oldFile = new File(CustomNpcs.Dir, "animations.dat");
            if (oldFile.exists()) {
                try { loadOldAnimations(NbtIo.readCompressed(oldFile)); } catch (Exception e) { LogWriter.error(e); }
                Util.instance.removeFile(oldFile);
            }
            animDir = new File (CustomNpcs.Dir,  "animations");
            if (animDir.exists()) {
                try {
                    loadAnimations(animDir);
                    Util.instance.removeFile(animDir);
                    save();
                }
                catch (Exception e) { LogWriter.error(e); }
            }
            emtnDir = new File (CustomNpcs.Dir,  "emotions");
            if (emtnDir.exists()) {
                try {
                    loadEmotions(emtnDir);
                    Util.instance.removeFile(emtnDir);
                    save();
                }
                catch (Exception e) { LogWriter.error(e); }
            }
        }
        // normal load
        animDir = new File (CustomNpcs.getLevelSaveDirectory(),  "animations");
        if (animDir.exists()) {
            try { loadAnimations(animDir); } catch (Exception e) { LogWriter.error(e); }
        }
        else { needSave = true; }
        emtnDir = new File (CustomNpcs.getLevelSaveDirectory(),  "emotions");
        if (emtnDir.exists()) { try { loadEmotions(emtnDir); } catch (Exception e) { LogWriter.error(e); } }
        else { needSave = true; }
        if (needSave) { save(); }
        LogWriter.info("End load animations");
        CustomNpcs.debugData.end(null);
    }

    private void loadOldAnimations(CompoundTag compound) {
        ListTag listA = compound.getList("Animations", 10);
        if (compound.contains("Animations", 9)) { listA = compound.getList("Animations", 10); }
        else if (compound.contains("Data", 9)) { listA = compound.getList("Data", 10); }
        for (int i = 0; i < listA.size(); ++i) {
            AnimationConfig anim = loadAnimation(listA.getCompound(i));
            if (anim.id < 43) { anim.immutable = true; }
        }
        emotions.clear();
        ListTag listE = compound.getList("Emotions", 10);
        for (int i = 0; i < listE.size(); ++i) {
            EmotionConfig emtn = loadEmotion(listE.getCompound(i));
            emtn.immutable = true;
        }
    }

    private void loadAnimations(File file) {
        List<CompoundTag> afterAnimations = new ArrayList<>();
        for (File f : Objects.requireNonNull(file.listFiles())) {
            try {
                CompoundTag nbt = NbtIo.readCompressed(f);
                int id = -1;
                try { id = Integer.parseInt(f.getName().toLowerCase().replace(".dat", "")); } catch (Exception e) { LogWriter.error(e); }
                if (id == -1 || animations.containsKey(id) || id < baseMaxAnimID) { afterAnimations.add(nbt); }
                else {
                    nbt.putInt("ID", id);
                    loadAnimation(nbt);
                }
            } catch (Exception e) { LogWriter.error(e); }
        }
        for (CompoundTag nbt : afterAnimations) {
            int id = nbt.getInt("ID");
            if (id == -1 || animations.containsKey(id) || id < baseMaxAnimID) { nbt.putInt("ID", getUnusedAnimId()); }
            loadAnimation(nbt);
        }
    }

    private void loadEmotions(File file) {
        for (File f : Objects.requireNonNull(file.listFiles())) {
            try {
                try {
                    CompoundTag nbt = NbtIo.readCompressed(f);
                    int id = -1;
                    try { id = Integer.parseInt(f.getName().toLowerCase().replace(".dat", "")); } catch (Exception e) { LogWriter.error(e); }
                    if (id != -1 && animations.containsKey(id)) { nbt.putInt("ID", getUnusedAnimId()); }
                    else { nbt.putInt("ID", id); }
                    loadEmotion(nbt);
                } catch (Exception e) { LogWriter.error(e); }
            } catch (Exception e) { LogWriter.error(e); }
        }
    }

    private void loadDefaultAnimations() {
        baseMaxAnimID = -1;
        InputStream inputStream = Util.instance.getModInputStream("a_def.dat");
        if (inputStream == null) { return; }
        CompoundTag compound = new CompoundTag();
        try { compound = NbtIo.readCompressed(inputStream); } catch (Exception e) { LogWriter.error(e); }
        ListTag listA = compound.getList("Animations", 10);
        if (!listA.isEmpty()) {
            for (int i = 0; i < listA.size(); ++i) {
                CompoundTag nbt = listA.getCompound(i);
                int id = nbt.getInt("ID");
                if (animations.containsKey(id)) {
                    if (!animations.get(id).immutable || !animations.get(id).name.equals(nbt.getString("Name"))) {
                        boolean found = false;
                        for (AnimationConfig anim : animations.values()) {
                            if (anim.name.equals(nbt.getString("Name"))) {
                                id = anim.id;
                                found = true;
                                break;
                            }
                        }
                        if (!found) { id = getUnusedAnimId(); }
                        nbt.putInt("ID", id);
                    }
                }
                AnimationConfig iAnim = loadAnimation(nbt);
                if (baseMaxAnimID < id) { baseMaxAnimID = id + 1; }
                if (iAnim != null) { iAnim.immutable = true; }
            }
        }
        ListTag listE = compound.getList("Emotions", 10);
        if (!listE.isEmpty()) {
            for (int i = 0; i < listE.size(); ++i) {
                CompoundTag nbt = listE.getCompound(i);
                int id = nbt.getInt("ID");
                if (emotions.containsKey(id)) {
                    if (!emotions.get(id).immutable || !emotions.get(id).name.equals(nbt.getString("Name"))) {
                        boolean found = false;
                        for (EmotionConfig emtn : emotions.values()) {
                            if (emtn.name.equals(nbt.getString("Name"))) {
                                id = emtn.id;
                                found = true;
                                break;
                            }
                        }
                        if (!found) { id = getUnusedEmtnId(); }
                        nbt.putInt("ID", id);
                    }
                }
                loadEmotion(nbt);
            }
        }
    }

    @Override
    public boolean removeAnimation(int animationId) {
        if (animations.containsKey(animationId)) {
            animations.remove(animationId);
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAnimation(String animationName) {
        for (int id : animations.keySet()) {
            if (animations.get(id).getName().equalsIgnoreCase(animationName)) {
                animations.remove(id);
                save();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeEmotion(int emotionId) {
        if (emotions.containsKey(emotionId)) {
            emotions.remove(emotionId);
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeEmotion(String emotionName) {
        for (int id : emotions.keySet()) {
            if (emotions.get(id).getName().equalsIgnoreCase(emotionName)) {
                emotions.remove(id);
                save();
                return true;
            }
        }
        return false;
    }


    public void save() {
        CustomNpcs.debugData.start(null);
        File animDir = CustomNpcs.getLevelSaveDirectory("animations");
        if (animDir != null) {
            if (animDir.exists() || animDir.mkdirs()) {
                for (int id : animations.keySet()) {
                    if (animations.get(id).immutable) { continue; }
                    try { NbtIo.writeCompressed(animations.get(id).save(), new File(animDir, id + ".dat")); } catch (Exception e) { LogWriter.error(e); }
                }
            }
            File emtnDir = CustomNpcs.getLevelSaveDirectory("emotions");
            if (emtnDir == null) {
                CustomNpcs.debugData.end(null);
                return;
            }
            if (emtnDir.exists() || emtnDir.mkdirs()) {
                for (int id : emotions.keySet()) {
                    try { NbtIo.writeCompressed(emotions.get(id).save(), new File(emtnDir, id + ".dat")); } catch (Exception e) { LogWriter.error(e); }
                }
            }
        }
        CustomNpcs.debugData.end(null);
    }

    public void sendTo(ServerPlayer player) {
        if (CustomNpcs.Server != null && CustomNpcs.Server.isSingleplayer()) { return; }
        Packets.send(player, new PacketSyncRemove(-1, 9));
        Packets.send(player, new PacketSyncRemove(-1, 10));
        for (AnimationConfig ac : animations.values()) { Packets.send(player, new PacketSyncUpdate(0, 9, ac.save())); }
        for (EmotionConfig ec : emotions.values()) { Packets.send(player, new PacketSyncUpdate(0, 10, ec.save())); }
    }

    @Override
    public EmotionConfig[] getEmotions() { return emotions.values().toArray(new EmotionConfig[0]); }

    public void clearAnimations() { animations.clear(); }

    public void clearEmotions() { emotions.clear(); }

    public AnimationConfig copy(int id, AnimationKind type) {
        if (!animations.containsKey(id)) { return null; }
        AnimationConfig ac = animations.get(id).copy();
        ac.id = getUnusedAnimId();
        ac.immutable = false;
        if (type != null) { ac.type = type; }
        animations.put(ac.id, ac);
        return ac;
    }

    public boolean hasAnimation(int id) { return animations.containsKey(id); }

    public boolean hasEmotion(int id) { return emotions.containsKey(id); }

    public void sendAnimationToAll(int id) {
        if (CustomNpcs.Server != null) {
            CompoundTag data = animations.containsKey(id) ? animations.get(id).save() : null;
            for (ServerPlayer player : CustomNpcs.Server. getPlayerList().getPlayers()) {
                if (id < 0) { sendTo(player); }
                else if (data == null) { Packets.send(player, new PacketSyncRemove(id, 9)); }
                else { Packets.send(player, new PacketSyncUpdate(0, 9, data)); }
            }
        }
    }

}
