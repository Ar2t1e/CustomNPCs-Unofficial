package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.api.handler.data.IPlayerData;
import noppes.npcs.controllers.MarcetController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.Util;
import noppes.npcs.util.ValueUtil;

public class PlayerGameData implements IPlayerData {

	protected static final String dataName = "GameData";

	public static class FollowerSet {

		public UUID id;
		public int dimId;
		public EntityNPCInterface npc;

		public FollowerSet(EntityNPCInterface npcIn) {
			npc = npcIn;
			id = npcIn.getUniqueID();
			dimId = npcIn.world.provider.getDimension();
		}

		public FollowerSet(NBTTagCompound nbt) {
			id = UUID.fromString(nbt.getString("UUID"));
			dimId = nbt.getInteger("DimID");
		}

	}

	private final List<FollowerSet> followers = new ArrayList<>();
	public final List<MarkupData> marketData = new ArrayList<>(); // ID market, slot
	protected long money = 0L;
	protected long donat = 0L;
	public boolean questLogIsFast;

	public boolean updateClient; // ServerTickHandler.cnpcPlayerTick() 122
	public boolean op = false; // ServerTickHandler.cnpcPlayerTick() 62
	public double[] logPos; // back login [x, y, z]
	public int logPosDimID = 0; // back login dimensionId
	public double blockReachDistance = 6.0;
	public double renderDistance = 128.0;
	public int dimID = 0; // used to set spawn on dimension

	@Override
	public void load(NBTTagCompound compound) {
		if (compound != null && compound.hasKey(dataName, 10)) {
			NBTTagCompound gameNbt = compound.getCompoundTag(dataName);
			money = gameNbt.getLong("Money");
			donat = gameNbt.getLong("Donat");
			op = gameNbt.getBoolean("IsOP");
			questLogIsFast = gameNbt.getBoolean("QuestLogIsFast");
			if (compound.hasKey("BlockReachDistance", 6)) { blockReachDistance = compound.getDouble("BlockReachDistance"); }
			if (compound.hasKey("RenderDistance", 6)) { renderDistance = compound.getDouble("RenderDistance"); }
			if (gameNbt.hasKey("MarketData", 9)) {
				marketData.clear();
				for (int i = 0; i < gameNbt.getTagList("MarketData", 10).tagCount(); i++) {
					NBTTagCompound nbt = gameNbt.getTagList("MarketData", 10).getCompoundTagAt(i);
					marketData.add(new MarkupData(nbt.getInteger("id"), nbt.getInteger("level"), nbt.getInteger("xp")));
				}
			}
			logPos = null;
			logPosDimID = 0;
			if (gameNbt.hasKey("LoginPos", 9) && gameNbt.getTagList("LoginPos", 6).tagCount() > 3 && gameNbt.hasKey("LoginDimID", 3)) {
				NBTTagList list = gameNbt.getTagList("LoginPos", 6);
				logPos = new double[] { list.getDoubleAt(0), list.getDoubleAt(1), list.getDoubleAt(2), list.getDoubleAt(3) };
				logPosDimID = gameNbt.getInteger("LoginDimID");
			}
			if (gameNbt.hasKey("Followers", 9)) {
				followers.clear();
				NBTTagList fls = gameNbt.getTagList("Followers", 10);
				for (int i = 0; i < fls.tagCount(); i++) { followers.add(new FollowerSet(fls.getCompoundTagAt(i))); }
			}
		}
	}

	@Override
	public NBTTagCompound save(NBTTagCompound compound) {
		NBTTagCompound gameNbt = new NBTTagCompound();
		gameNbt.setLong("Money", money);
		gameNbt.setLong("Donat", donat);
		gameNbt.setDouble("BlockReachDistance", blockReachDistance);
		gameNbt.setDouble("RenderDistance", renderDistance);
		gameNbt.setBoolean("IsOP", op);
		gameNbt.setBoolean("QuestLogIsFast", questLogIsFast);
		NBTTagList markup = new NBTTagList();
		for (MarkupData data : marketData) { markup.appendTag(data.getPlayerNBT()); }
		gameNbt.setTag("MarketData", markup);
		if (logPos != null) {
			NBTTagList pos = new NBTTagList();
			for (double d : logPos) { pos.appendTag(new NBTTagDouble(d)); }
			gameNbt.setTag("LoginPos", pos);
			gameNbt.setInteger("LoginDimID", logPosDimID);
		}
		NBTTagList fls = new NBTTagList();
		for (FollowerSet fs : followers) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("UUID", fs.id.toString());
			nbt.setInteger("DimID", fs.dimId);
			fls.appendTag(nbt);
		}
		gameNbt.setTag("Followers", fls);
		compound.setTag(dataName, gameNbt);
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
		money = ValueUtil.correctLong(moneyIn, 0, Integer.MAX_VALUE);
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
			if (npc.equals(fs.npc) || fs.id.equals(npc.getUniqueID())) { return fs; }
		}
		return null;
	}

	public List<FollowerSet> getFollowers() { return followers; }

	public List<EntityNPCInterface> getMercenaries() {
		List<EntityNPCInterface> npcs = new ArrayList<>();
		for (FollowerSet fs : followers) {
			if (fs.npc != null && !fs.npc.isDead) { npcs.add(fs.npc); }
		}
		return npcs;
	}

	public void removeFollower(EntityNPCInterface npc) {
		for (FollowerSet fs : followers) {
			if (fs.id.equals(npc.getUniqueID())) {
				followers.remove(fs);
				return;
			}
		}
	}

	public void removeFollower(FollowerSet fs) { followers.remove(fs); }

}
