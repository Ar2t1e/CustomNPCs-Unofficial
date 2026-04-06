package noppes.npcs.controllers.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import noppes.npcs.api.entity.data.IMiniMapData;
import noppes.npcs.api.entity.data.IPlayerMiniMap;
import noppes.npcs.api.handler.data.IPlayerData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerMiniMapData implements IPlayerData, IPlayerMiniMap {

    protected static final String dataName = "MiniMapData";

    public final List<MiniMapData> points = new ArrayList<>();
    public Map<String, Object> addData = new HashMap<>();
    public String modName = "non";

    protected boolean update;

    @Override
    public IMiniMapData addPoint(String dimensionId) {
        if (modName.equals("non")) { return new MiniMapData(); }
        MiniMapData mmd = new MiniMapData();
        mmd.dimIDs.clear();
        mmd.dimIDs.add(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimensionId)));
        mmd.id = points.size();
        if (modName.equals("voxelmap")) { mmd.icon = ""; }
        points.add(mmd);
        update = true;
        return mmd;
    }

    @Override
    public IMiniMapData[] getAllPoints() { return points.toArray(new IMiniMapData[0]); }

    @Override
    public String getModName() { return modName; }

    private CompoundTag getNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("ModName", modName);
        ListTag pList = new ListTag();
        for (MiniMapData mmd : points) { pList.add(mmd.save()); }
        nbt.put("Data", pList);
        return nbt;
    }

    @Override
    public IMiniMapData getPoint(int id) {
        if (id < 0 || id >= points.size()) { return null; }
        MiniMapData mmd = points.get(id);
        if (mmd != null && mmd.id == id) { return mmd; }
        for (MiniMapData mmdc : points) {
            if (mmdc.id == id) { return mmdc; }
        }
        return null;
    }

    @Override
    public IMiniMapData getPoint(String name) {
        for (MiniMapData mmd : points) {
            if (mmd.name.equals(name)) { return mmd; }
        }
        return null;
    }

    @Override
    public IMiniMapData[] getPoints(String dimensionId) {
        List<MiniMapData> list = new ArrayList<>();
        for (MiniMapData mmd : points) {
            for (ResourceKey<Level> id : mmd.dimIDs) {
                if (id.location().toString().equals(dimensionId)) {
                    list.add(mmd);
                    break;
                }
            }
        }
        return list.toArray(new IMiniMapData[0]);
    }

    public MiniMapData getQuestTask(int questId, int taskId, String questName, String dimensionId) {
        questName = Util.instance.deleteColor(questName);
        for (MiniMapData mmd : points) {
            if (mmd.isQuestTask(questId, taskId)) { return mmd; }
            if (mmd.name.equals(questName)) {
                for (ResourceKey<Level> id : mmd.dimIDs) {
                    if (id.location().toString().equals(dimensionId)) { return mmd; }
                }
            }
        }
        return null;
    }

    @Override
    public String[] getSpecificKeys() {
        return addData.keySet().toArray(new String[0]);
    }

    @Override
    public Object getSpecificValue(String key) {
        return addData.get(key);
    }

    public void load(CompoundTag compound) {
        if (!compound.contains(dataName)) { return; }
        CompoundTag nbt = compound.getCompound(dataName);
        modName = nbt.getString("ModName");
        points.clear();
        ListTag list = nbt.getList("Data", 10);
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                CompoundTag nbtList = list.getCompound(i);
                if (nbtList.contains("Points", 9) || nbtList.contains("DimensionID", 3)) { // OLD
                    for (int j = 0; j < nbtList.getList("Points", 10).size(); j++) {
                        MiniMapData mmd = new MiniMapData();
                        mmd.load(nbtList.getList("Points", 10).getCompound(j));
                        points.add(mmd);
                    }
                } else {
                    MiniMapData mmd = new MiniMapData();
                    mmd.load(nbtList);
                    points.add(mmd);
                }
            }
        }
    }

    public void removeQuestPoints(int questId) {
        boolean remove = false;
        List<MiniMapData> tempList = new ArrayList<>(points);
        for (MiniMapData mmd : tempList) {
            if (mmd.isQuestTask(questId, -1)) {
                points.remove(mmd);
                remove = true;
            }
        }
        if (remove) { update = true; }
    }

    @Override
    public boolean removePoint(int id) {
        if (id < 0 || id >= points.size()) { return false; }
        MiniMapData mmd = points.get(id);
        boolean remove = false;
        if (mmd != null && mmd.id == id) { remove = points.remove(mmd); }
        if (!remove) {
            for (MiniMapData mmds : points) {
                if (mmds.id == id) { remove = points.remove(mmds); }
                if (remove) { break; }
            }
        }
        if (remove) { update = true; }
        return true;
    }

    @Override
    public boolean removePoint(String name) {
        name = Util.instance.deleteColor(name);
        for (MiniMapData mmd : points) {
            if (mmd.name.equals(name) && points.remove(mmd)) {
                update = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removePoints(String dimensionId) {
        boolean remove = false;
        List<MiniMapData> tempList = new ArrayList<>(points);
        for (MiniMapData mmd : tempList) {
            for (ResourceKey<Level> id : mmd.dimIDs) {
                if (id.location().toString().equals(dimensionId)) {
                    remove = true;
                    break;
                }
            }
        }
        if (remove) { update = true; }
        return remove;
    }

    public CompoundTag save(CompoundTag compound) {
        compound.put(dataName, getNBT());
        return compound;
    }

    public void update(ServerPlayer player) {
        boolean needSend = update;
        if (!needSend) {
            for (MiniMapData mmd : points) {
                if (mmd.isUpdate()) {
                    needSend = true;
                    break;
                }
            }
        }
        if (needSend) {
            update = false;
            Packets.send(player, new PacketSyncUpdate(0,6, save(new CompoundTag())));
        }
    }

    public MiniMapData get(MiniMapData mmd) {
        for (MiniMapData mmp : points) {
            boolean equalDimIDs = mmp.dimIDs.size() == mmd.dimIDs.size();
            if (equalDimIDs) {
                int eq = 0;
                for (ResourceKey<Level> idp : mmp.dimIDs) {
                    for (ResourceKey<Level> idd : mmd.dimIDs) {
                        if (idp.location().equals(idd.location())) { eq++; break; }
                    }
                }
                equalDimIDs = mmp.dimIDs.size() == eq;
            }
            if (equalDimIDs && mmp.name.equals(mmd.name) && mmp.type.equals(mmd.type) && mmp.pos.getMCBlockPos().equals(mmd.pos.getMCBlockPos())) {
                return mmp;
            }
        }
        return null;
    }

    public void clear() {
        points.clear();
        addData.clear();
    }

}