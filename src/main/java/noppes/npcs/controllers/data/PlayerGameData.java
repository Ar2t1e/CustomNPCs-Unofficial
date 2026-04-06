package noppes.npcs.controllers.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import noppes.npcs.api.handler.data.IPlayerData;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.Util;
import noppes.npcs.util.ValueUtil;

import java.util.*;

public class PlayerGameData implements IPlayerData {

    protected static final String dataName = "GameData";

    public static class FollowerSet {

        public UUID id;
        public ResourceLocation dimId;
        public EntityNPCInterface npc;

        public FollowerSet(EntityNPCInterface npcIn) {
            npc = npcIn;
            id = npcIn.getUUID();
            dimId = npcIn.level().dimension().location();
        }

        public FollowerSet(CompoundTag nbt) {
            id = UUID.fromString(nbt.getString("UUID"));
            dimId = new ResourceLocation(nbt.getString("DimID"));
        }

    }

    private final List<FollowerSet> followers = new ArrayList<>();
    public final List<MarkupData> marketData = new ArrayList<>(); // ID market, slot
    protected long money = 0L;
    protected long donat = 0L;

    public boolean updateClient; // ServerTickHandler.cnpcPlayerTick() 122
    public boolean op = false; // ServerTickHandler.cnpcPlayerTick() 62
    public double[] logPos; // back login pos [x, y, z]
    public ResourceKey<Level> logPosDimID = Level.OVERWORLD; // back login dimensionId
    public double blockReachDistance = 6.0;
    public double renderDistance = 128.0;
    public ResourceKey<Level> dimID = Level.OVERWORLD; // used to set spawn on dimension

    @Override
    public void load(CompoundTag compound) {
        if (compound != null && compound.contains(dataName, 10)) {
            CompoundTag gameNbt = compound.getCompound(dataName);
            money = gameNbt.getLong("Money");
            donat = gameNbt.getLong("Donat");
            op = gameNbt.getBoolean("IsOP");
            if (compound.contains("BlockReachDistance", 6)) { blockReachDistance = compound.getDouble("BlockReachDistance"); }
            if (compound.contains("RenderDistance", 6)) { renderDistance = compound.getDouble("RenderDistance"); }

            if (gameNbt.contains("MarketData", 9)) {
                marketData.clear();
                for (int i = 0; i < gameNbt.getList("MarketData", 10).size(); i++) {
                    CompoundTag nbt = gameNbt.getList("MarketData", 10).getCompound(i);
                    marketData.add(new MarkupData(nbt.getInt("id"), nbt.getInt("level"), nbt.getInt("xp")));
                }
            }
            logPos = null;
            logPosDimID = Level.OVERWORLD;
            if (gameNbt.contains("LoginPos", 9) && gameNbt.getList("LoginPos", 6).size() > 2 && gameNbt.contains("LoginDimID", 8)) {
                ListTag list = gameNbt.getList("LoginPos", 6);
                logPos = new double[] { list.getDouble(0), list.getDouble(1), list.getDouble(2) };
                logPosDimID = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(gameNbt.getString("LoginDimID")));
            }
            if (gameNbt.contains("Followers", 9)) {
                followers.clear();
                ListTag fls = gameNbt.getList("Followers", 10);
                for (int i = 0; i < fls.size(); i++) { followers.add(new FollowerSet(fls.getCompound(i))); }
            }
        }
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        CompoundTag gameNbt = new CompoundTag();
        gameNbt.putLong("Money", money);
        gameNbt.putLong("Donat", donat);
        gameNbt.putDouble("BlockReachDistance", blockReachDistance);
        gameNbt.putDouble("RenderDistance", renderDistance);
        gameNbt.putBoolean("IsOP", op);
        ListTag markup = new ListTag();
        for (MarkupData data : marketData) { markup.add(data.getPlayerNBT()); }
        gameNbt.put("MarketData", markup);
        if (logPos != null) {
            ListTag pos = new ListTag();
            for (double d : logPos) { pos.add(DoubleTag.valueOf(d)); }
            gameNbt.put("LoginPos", pos);
            gameNbt.putString("LoginDimID", logPosDimID.toString());
        }
        ListTag fls = new ListTag();
        for (FollowerSet fs : followers) {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("UUID", fs.id.toString());
            nbt.putString("DimID", fs.dimId.toString());
            fls.add(nbt);
        }
        gameNbt.put("Followers", fls);
        compound.put(dataName, gameNbt);
        return compound;
    }

    public void clear() {
        money = 0L;
        donat = 0L;
        marketData.clear();
    }

    public long getMoney() { return money; }

    public void addMoney(long moneyIn) {
        money = ValueUtil.correctLong(money + moneyIn, 0, Integer.MAX_VALUE);
        updateClient = true;
    }

    public void setMoney(long moneyIn) {
        money = ValueUtil.correctLong(moneyIn, 0, Long.MAX_VALUE);
        updateClient = true;
    }

    public String getTextMoney() { return Util.instance.getTextReducedNumber(money, true, true, false); }

    public long getDonat() { return donat; }

    public void addDonat(long moneyIn) {
        donat = ValueUtil.correctLong(donat + moneyIn, 0, Long.MAX_VALUE);
        updateClient = true;
    }

    public void setDonat(long moneyIn) {
        donat = ValueUtil.correctLong(moneyIn, 0, Integer.MAX_VALUE);
        updateClient = true;
    }

    public String getTextDonat() { return Util.instance.getTextReducedNumber(donat, true, true, false); }

    public void addMarkupXP(int marketID, int xp) {
        if (xp == 0) { return; }
        MarkupData md = getMarkupData(marketID);
        md.addXP(xp);
        Marcet marcet = MarcetController.getInstance().getMarcet(marketID);
        if (marcet != null) {
            MarkupData d = marcet.markup.get(md.level);
            if (md.level < marcet.markup.size() - 1 && d != null && d.xp <= md.xp) {
                md.level++;
                md.xp = 0;
            }
        }
        updateClient = true;
    }

    public int getMarcetLevel(int marketID) { return getMarkupData(marketID).level; }

    public MarkupData getMarkupData(int marketID) {
        MarkupData md = null;
        for (MarkupData m : marketData) {
            if (m.id == marketID) {
                md = m;
                break;
            }
        }
        if (md == null) {
            md = new MarkupData(marketID, 0, 0);
            marketData.add(md);
        }
        return md;
    }

    public FollowerSet addFollower(EntityNPCInterface npc) {
        FollowerSet fs = new FollowerSet(npc);
        followers.add(fs);
        return fs;
    }

    public FollowerSet getFollower(EntityNPCInterface npc) {
        for (FollowerSet fs : followers) {
            if (npc.equals(fs.npc) || fs.id.equals(npc.getUUID())) { return fs; }
        }
        return null;
    }

    public List<FollowerSet> getFollowers() { return followers; }

    public List<EntityNPCInterface> getMercenaries() {
        List<EntityNPCInterface> npcs = new ArrayList<>();
        for (FollowerSet fs : followers) {
            if (fs.npc != null && !fs.npc.isRemoved()) { npcs.add(fs.npc); }
        }
        return npcs;
    }

    public void removeFollower(EntityNPCInterface npc) {
        for (FollowerSet fs : followers) {
            if (fs.id.equals(npc.getUUID())) {
                followers.remove(fs);
                return;
            }
        }
    }

    public void removeFollower(FollowerSet fs) { followers.remove(fs); }

}
