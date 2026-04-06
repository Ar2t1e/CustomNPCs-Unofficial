package noppes.npcs.controllers.data;

import java.util.*;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import noppes.npcs.api.IPos;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.data.IMiniMapData;
import noppes.npcs.api.wrapper.BlockPosWrapper;
import noppes.npcs.util.Util;

public class MiniMapData implements IMiniMapData {

    private int questId = -1;
    private int taskId = -1;
    private boolean update = false;

    public int id = 0;
    public int color;
    public List<ResourceKey<Level>> dimIDs = new ArrayList<>();
    public String name = "default map point";
    public String type = "Normal";
    public String icon = "icon.png";
    public IPos pos = BlockPosWrapper.ZERO;
    public boolean isEnable = true;
    public Map<String, String> gsonData = new TreeMap<>();

    public MiniMapData() {
        color = (int) ((double) 0xFF000000 + Math.random() * (double) 0xFFFFFF);
        dimIDs.add(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("minecraft", "overworld")));
    }

    public void setQuestId(int id) { questId = id; }

    public void setTaskId(int id) { taskId = id; }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MiniMapData mmd) {
            boolean equalDimIDs = dimIDs.size() == mmd.dimIDs.size();
            if (equalDimIDs) {
                int eq = 0;
                for (ResourceKey<Level> idp : dimIDs) {
                    for (ResourceKey<Level> idd : mmd.dimIDs) {
                        if (idp.location().equals(idd.location())) { eq++; break; }
                    }
                }
                equalDimIDs = dimIDs.size() == eq;
            }
            return equalDimIDs && id == mmd.id && color == mmd.color && name.equals(mmd.name) && type.equals(mmd.type)
                    && icon.equals(mmd.icon) && pos.getMCBlockPos().equals(mmd.pos.getMCBlockPos())
                    && isEnable == mmd.isEnable;
        }
        return false;
    }

    @Override
    public int getColor() { return color; }

    @Override
    public List<String> getDimensions() {
        List<String> list = new ArrayList<>();
        for (ResourceKey<Level> key : dimIDs) { list.add(key.location().toString()); }
        return list;
    }

    @Override
    public String getIcon() { return type; }

    @Override
    public int getId() { return id; }

    @Override
    public String getName() { return name; }

    @Override
    public IPos getPos() { return pos; }

    @Override
    public List<String> getSpecificKeys() { return new ArrayList<>(gsonData.keySet()); }

    @Override
    public String getSpecificValue(String key) { return gsonData.get(key); }

    @Override
    public String getType() { return type; }

    @Override
    public boolean isEnable() { return isEnable; }

    public boolean isQuestTask(int questIdIn, int taskIdIn) { return questId == questIdIn && (taskIdIn < 0 || taskId == taskIdIn); }

    public boolean isUpdate() {
        if (update) {
            update = false;
            return true;
        }
        return false;
    }

    public void load(CompoundTag compound) {
        isEnable = compound.getBoolean("IsEnable");
        questId = compound.getInt("QuestID");
        taskId = compound.getInt("TaskID");
        color = compound.getInt("Color");
        id = compound.getInt("ID");
        type = compound.getString("Type");
        name = Util.instance.deleteColor(compound.getString("Name"));
        icon = compound.getString("Icon");
        pos = Objects.requireNonNull(NpcAPI.Instance()).getIPos(BlockPos.of(compound.getLong("Pos")));
        gsonData.clear();
        ListTag gsonList = compound.getList("GsonData", 10);
        for (int i = 0; i < gsonList.size(); i++){
            CompoundTag gsonNBT = compound.getList("GsonData", 10).getCompound(i);
            gsonData.put(gsonNBT.getString("K"), gsonNBT.getString("V"));
        }
        ListTag dimsList = compound.getList("DimensionID", 8);
        for (int i = 0; i < dimsList.size(); i++){
            dimIDs.add(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimsList.getString(i))));
        }
        update = false;
    }

    public CompoundTag save() {
        CompoundTag compound = new CompoundTag();
        compound.putBoolean("IsEnable", isEnable);
        compound.putInt("QuestID", questId);
        compound.putInt("TaskID", taskId);
        compound.putInt("Color", color);
        compound.putInt("ID", id);
        compound.putString("Type", type);
        compound.putString("Name", Util.instance.deleteColor(name));
        compound.putString("Icon", icon);
        compound.putLong("Pos", pos.getMCBlockPos().asLong());
        ListTag gsonList = new ListTag();
        for (String key : gsonData.keySet()) {
            CompoundTag gsonNBT = new CompoundTag();
            gsonNBT.putString("K", key);
            gsonNBT.putString("V", gsonData.get(key));
            gsonList.add(gsonNBT);
        }
        compound.put("GsonData", gsonList);
        ListTag dimsList = new ListTag();
        for (ResourceKey<Level> key : dimIDs) { dimsList.add(StringTag.valueOf(key.location().toString())); }
        compound.put("DimensionID", dimsList);
        return compound;
    }

    @Override
    public void setColor(int newColor) {
        if (color != newColor) {
            color = newColor;
            update = true;
        }
    }

    @Override
    public void setDimensions(String ... dimensions) {
        if (dimensions == null || dimensions.length == 0) { return; }
        dimIDs.clear();
        for (String dim : dimensions) { dimIDs.add(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dim))); }
    }

    @Override
    public void setIcon(String newIcon) {
        if (newIcon == null) { newIcon = ""; }
        if (!icon.equals(newIcon)) {
            icon = newIcon;
            update = true;
        }
    }

    @Override
    public void setName(String newName) {
        newName = Util.instance.deleteColor(newName);
        if (newName == null) { newName = ""; }
        if (!name.equals(newName)) {
            name = newName;
            update = true;
        }
    }

    @Override
    public void setPos(int x, int y, int z) {
        BlockPos newPos = new BlockPos(x, y, z);
        if (!pos.getMCBlockPos().equals(newPos)) {
            pos = Objects.requireNonNull(NpcAPI.Instance()).getIPos(newPos);
            update = true;
        }
    }

    @Override
    public void setPos(IPos newPos) {
        if (newPos == null) { newPos = Objects.requireNonNull(NpcAPI.Instance()).getIPos(BlockPos.ZERO); }
        if (!pos.getMCBlockPos().equals(newPos.getMCBlockPos())) {
            pos = Objects.requireNonNull(NpcAPI.Instance()).getIPos(newPos.getMCBlockPos());
            update = true;
        }
    }

    public void setQuest(MiniMapData parent) {
        questId = parent.questId;
        taskId = parent.taskId;
    }

    @Override
    public void setType(String newType) {
        if (newType == null) { newType = ""; }
        if (!type.equals(newType)) {
            type = newType;
            update = true;
        }
    }

    @Override
    public String toString() {
        StringBuilder gs = new StringBuilder("empty");
        if (!gsonData.isEmpty()) {
            gs = new StringBuilder();
            for (String k : gsonData.keySet()) {
                if (!gs.isEmpty()) { gs.append(", "); }
                gs.append("(").append(k).append("=").append(gsonData.get(k)).append(")");
            }
            gs = new StringBuilder("[" + gs + "]");
        }
        StringBuilder ds = new StringBuilder();
        for (ResourceKey<Level> id : dimIDs) {
            if (!ds.isEmpty()) { ds.append(", "); }
            ds.append(id.location());
        }
        ds = new StringBuilder("[" + ds + "]");
        String qd = "";
        if (questId != -1) { qd = ", QuestID: " + questId + ", TaskID: " + taskId; }
        return "Point Data: {ID: " + id + ", Name: " + name + ", Type: " + type + ", Icon: " + icon
                + ", Color: " + color + ", Pos: " + pos.getMCBlockPos() + ", DimensionIDs: " + ds
                + ", IsEnable: " + isEnable + ", GsonData: " + gs + qd + "}";
    }

}
