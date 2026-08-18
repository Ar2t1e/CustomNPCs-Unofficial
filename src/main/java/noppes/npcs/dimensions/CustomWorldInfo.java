package noppes.npcs.dimensions;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.*;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;
import noppes.npcs.api.INbt;
import noppes.npcs.api.handler.data.IWorldInfo;
import noppes.npcs.api.wrapper.NBTWrapper;
import noppes.npcs.controllers.DimensionController;
import noppes.npcs.mixin.world.storage.IWorldInfoMixin;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;
import java.util.Map;

public class CustomWorldInfo extends WorldInfo implements IWorldInfo {

	public int dimensionId = 100;
	protected String dimensionName;

	public CustomWorldInfo(NBTTagCompound nbt) {
		super(nbt);
		dimensionName = "default_" + dimensionId;
	}

	public CustomWorldInfo(WorldSettings settings, String levelName, int dimensionIdIn) {
		super(settings, levelName);
		dimensionId = dimensionIdIn;
		dimensionName = "default_" + dimensionIdIn;
	}

	@Override
	public int getId() { return dimensionId; }

	@Override
	public INbt getNbt() { return new NBTWrapper(cloneNBTCompound(null)); }

	public void load(NBTTagCompound nbt) {
		dimensionId = nbt.hasKey("DimensionId", 3) ? nbt.getInteger("DimensionId") : 100;
		dimensionName = nbt.hasKey("DimensionName", 8) ? nbt.getString("DimensionName") : "default_" + dimensionId;
		if (nbt.hasKey("Version", 10)) {
			NBTTagCompound nbttagcompound = nbt.getCompoundTag("Version");
			setMCVersionName(nbttagcompound.getString("Name"));
			setMCVersionId(nbttagcompound.getInteger("Id"));
			setMCVersionSnapshot(nbttagcompound.getBoolean("Snapshot"));
		}
		setMCSeed(nbt.getLong("RandomSeed"));
		if (nbt.hasKey("generatorName", 8)) {
			String s1 = nbt.getString("generatorName");
			setTerrainType(WorldType.parseWorldType(s1));
            if (getTerrainType().isVersioned()) {
                int i = 0;
                if (nbt.hasKey("generatorVersion", 99)) { i = nbt.getInteger("generatorVersion"); }
				setTerrainType(getTerrainType().getWorldTypeForGeneratorVersion(i));
            }
			if (nbt.hasKey("generatorOptions", 8)) { setMCGeneratorOptions(nbt.getString("generatorOptions")); }
		}
		setMCGameType(GameType.getByID(nbt.getInteger("GameType")));
		setMCMapFeaturesEnabled(!nbt.hasKey("MapFeatures", 99) || nbt.getBoolean("MapFeatures"));
		setMCSpawnX(nbt.getInteger("SpawnX"));
		setMCSpawnY(nbt.getInteger("SpawnY"));
		setMCSpawnZ(nbt.getInteger("SpawnZ"));
		setMCTotalTime(nbt.getLong("Time"));
		setMCWorldTime(nbt.hasKey("DayTime", 99) ? nbt.getLong("DayTime") : getMCTotalTime());
		setMCLastTimePlayed(nbt.getLong("LastPlayed"));
		((IWorldInfoMixin) this).setSizeOnDisk(nbt.getLong("SizeOnDisk"));
		setMCLevelName(nbt.getString("LevelName"));
		setMCVersionSave(nbt.getInteger("version"));
		setMCCleanWeatherTime(nbt.getInteger("clearWeatherTime"));
		setMCRainTime(nbt.getInteger("rainTime"));
		setMCRaining(nbt.getBoolean("raining"));
		setMCThunderTime(nbt.getInteger("thunderTime"));
		setMCThundering(nbt.getBoolean("thundering"));
		setMCHardcore(nbt.getBoolean("hardcore"));
		setMCInitialized(!nbt.hasKey("initialized", 99) || nbt.getBoolean("initialized"));
		setMCAllowCommands(nbt.hasKey("allowCommands", 99) ? nbt.getBoolean("allowCommands") : getMCGameType() == GameType.CREATIVE);
		if (nbt.hasKey("Player", 10)) {
			setMCPlayerTag(nbt.getCompoundTag("Player"));
			((IWorldInfoMixin) this).setDimension(getMCPlayerTag().getInteger("Dimension"));
		}
		if (nbt.hasKey("GameRules", 10)) { getMCGameRules().readFromNBT(nbt.getCompoundTag("GameRules")); }
		if (nbt.hasKey("Difficulty", 99)) { setMCDifficulty(EnumDifficulty.getDifficultyEnum(nbt.getByte("Difficulty"))); }
		if (nbt.hasKey("DifficultyLocked", 1)) { setMCDifficultyLocked(nbt.getBoolean("DifficultyLocked")); }
		if (nbt.hasKey("BorderCenterX", 99)) { setMCBorderCenterX(nbt.getDouble("BorderCenterX")); }
		if (nbt.hasKey("BorderCenterZ", 99)) { setMCBorderCenterZ(nbt.getDouble("BorderCenterZ")); }
		if (nbt.hasKey("BorderSize", 99)) { setMCBorderSize(nbt.getDouble("BorderSize")); }
		if (nbt.hasKey("BorderSizeLerpTime", 99)) { setMCBorderSizeLerpTime(nbt.getLong("BorderSizeLerpTime")); }
		if (nbt.hasKey("BorderSizeLerpTarget", 99)) { setMCBorderSizeLerpTarget(nbt.getDouble("BorderSizeLerpTarget")); }
		if (nbt.hasKey("BorderSafeZone", 99)) { setMCBorderSafeZone(nbt.getDouble("BorderSafeZone")); }
		if (nbt.hasKey("BorderDamagePerBlock", 99)) { setMCBorderDamagePerBlock(nbt.getDouble("BorderDamagePerBlock")); }
		if (nbt.hasKey("BorderWarningBlocks", 99)) { setMCBorderWarningDistance(nbt.getInteger("BorderWarningBlocks")); }
		if (nbt.hasKey("BorderWarningTime", 99)) { setMCBorderWarningTime(nbt.getInteger("BorderWarningTime")); }
		if (nbt.hasKey("DimensionData", 10)) {
			NBTTagCompound compound = nbt.getCompoundTag("DimensionData");
			Map<Integer, NBTTagCompound> dimensionData = getMCDataDimension();
			for (String s : compound.getKeySet()) {
				dimensionData.put(Integer.parseInt(s), compound.getCompoundTag(s));
			}
		}
	}

	@Override
	public @Nonnull NBTTagCompound cloneNBTCompound(NBTTagCompound playerTag) {
		NBTTagCompound compound = super.cloneNBTCompound(playerTag);
		compound.setInteger("DimensionId", dimensionId);
		compound.setString("DimensionName", dimensionName);
		return compound;
	}

	@Override
	public GameRules getMCGameRules() { return getGameRulesInstance(); }

	@Override
	public void setNbt(INbt inbt) { load(inbt.getMCNBT()); }

	@Override
	public long getMCSeed() { return getSeed(); }

	@Override
	public void setMCSeed(long seed) { ((IWorldInfoMixin) this).setRandomSeed(seed); }

	@Override
	public WorldType getMCTerrainType() { return getTerrainType(); }

	@Override
	public void setMCTerrainType(WorldType type) { setTerrainType(type); }

	@Override
	public String getMCGeneratorOptions() { return getGeneratorOptions(); }

	@Override
	public void setMCGeneratorOptions(String options) { ((IWorldInfoMixin) this).setGeneratorOptions(options); }

	@Override
	public String getMCLevelName() { return ((IWorldInfoMixin) this).getLevelName(); }

	@Override
	public void setMCLevelName(String name) {
		if (name != null && !name.isEmpty()) { ((IWorldInfoMixin) this).setLevelName(name); }
	}

	@Override
	public int getMCSpawnX() { return getSpawnX(); }

	@Override
	public void setMCSpawnX(int x) { ((IWorldInfoMixin) this).setCommonSpawnX(x); }

	@Override
	public int getMCSpawnY() { return getSpawnY(); }

	@Override
	public void setMCSpawnY(int y) { ((IWorldInfoMixin) this).setCommonSpawnY(y); }

	@Override
	public int getMCSpawnZ() { return getSpawnZ(); }

	@Override
	public void setMCSpawnZ(int z) { ((IWorldInfoMixin) this).setCommonSpawnZ(z); }

	@Override
	public GameType getMCGameType() { return getGameType(); }

	@Override
	public void setMCGameType(GameType type) { setGameType(type); }

	@Override
	public void setMCGameType(int type) { setGameType(WorldSettings.getGameTypeById(type)); }

	@Override
	public boolean isMCMapFeaturesEnabled() { return isMapFeaturesEnabled(); }

	@Override
	public void setMCMapFeaturesEnabled(boolean enabled) { setMapFeaturesEnabled(enabled); }

	@Override
	public boolean isMCHardcore() { return isHardcoreModeEnabled(); }

	@Override
	public void setMCHardcore(boolean hardcore) { setHardcore(hardcore); }

	@Override
	public boolean isMCAllowCommands() { return areCommandsAllowed(); }

	@Override
	public void setMCAllowCommands(boolean allow) { setAllowCommands(allow); }

	@Override
	public boolean isMCInitialized() { return isInitialized(); }

	@Override
	public void setMCInitialized(boolean init) { setServerInitialized(init); }

	@Override
	public EnumDifficulty getMCDifficulty() { return getDifficulty(); }

	@Override
	public void setMCDifficulty(EnumDifficulty diff) { setDifficulty(diff); }

	@Override
	public boolean isMCDifficultyLocked() { return isDifficultyLocked(); }

	@Override
	public void setMCDifficultyLocked(boolean locked) { setDifficultyLocked(locked); }

	@Override
	public boolean isMCRaining() { return isRaining(); }

	@Override
	public void setMCRaining(boolean raining) { setRaining(raining); }

	@Override
	public int getMCRainTime() { return getRainTime(); }

	@Override
	public void setMCRainTime(int time) { setRainTime(time); }

	@Override
	public boolean getMCThundering() { return isThundering(); }

	@Override
	public void setMCThundering(boolean thundering) { setThundering(thundering); }

	@Override
	public int getMCThunderTime() { return getThunderTime(); }

	@Override
	public void setMCThunderTime(int time) { setThunderTime(time); }

	@Override
	public long getMCTotalTime() { return getWorldTotalTime(); }

	@Override
	public void setMCTotalTime(long time) { setWorldTotalTime(time); }

	@Override
	public long getMCWorldTime() { return getWorldTime(); }

	@Override
	public void setMCWorldTime(long time) { setWorldTime(time); }

	@Override
	public long getMCLastTimePlayed() { return ((IWorldInfoMixin) this).getLastTimePlayed(); }

	@Override
	public void setMCLastTimePlayed(long time) { ((IWorldInfoMixin) this).setLastTimePlayed(time); }

	@Override
	public long getMCSizeOnDisk() { return ((IWorldInfoMixin) this).getSizeOnDisk(); }

	@Override
	public int getMCCleanWeatherTime() { return getCleanWeatherTime(); }

	@Override
	public void setMCCleanWeatherTime(int time) { setCleanWeatherTime(time); }

	@Override
	public double getMCBorderCenterX() { return getBorderCenterX(); }

	@Override
	public void setMCBorderCenterX(double posX) { getBorderCenterX(posX); }

	@Override
	public double getMCBorderCenterZ() { return getBorderCenterZ(); }

	@Override
	public void setMCBorderCenterZ(double posZ) { getBorderCenterX(posZ); }

	@Override
	public double getMCBorderSize() { return getBorderSize(); }

	@Override
	public void setMCBorderSize(double size) { setBorderSize(size); }

	@Override
	public long getMCBorderSizeLerpTime() { return getBorderLerpTime(); }

	@Override
	public void setMCBorderSizeLerpTime(long time) { setBorderLerpTime(time); }

	@Override
	public double getMCBorderSizeLerpTarget() { return getBorderLerpTarget(); }

	@Override
	public void setMCBorderSizeLerpTarget(double sizeLerpTarget) { setBorderLerpTarget(sizeLerpTarget); }

	@Override
	public double getMCBorderSafeZone() { return getBorderSafeZone(); }

	@Override
	public void setMCBorderSafeZone(double safeZone) { setBorderSafeZone(safeZone); }

	@Override
	public double getMCBorderDamagePerBlock() { return getBorderDamagePerBlock(); }

	@Override
	public void setMCBorderDamagePerBlock(double damage) { setBorderDamagePerBlock(ValueUtil.correctDouble(damage, 1.0d, Double.MAX_VALUE)); }

	@Override
	public int getMCBorderWarningDistance() { return getBorderWarningDistance(); }

	@Override
	public void setMCBorderWarningDistance(int distance) { setBorderWarningDistance(ValueUtil.correctInt(distance, 1, Integer.MAX_VALUE)); }

	@Override
	public int getMCBorderWarningTime() { return getBorderWarningTime(); }

	@Override
	public void setMCBorderWarningTime(int time) { setBorderWarningTime(ValueUtil.correctInt(time, 1, Integer.MAX_VALUE)); }

	@Override
	public String getMCVersionName() { return ((IWorldInfoMixin) this).getVersionName(); }

	@Override
	public void setMCVersionName(String versionName) {
		if (versionName != null && versionName.isEmpty()) { ((IWorldInfoMixin) this).setVersionName(versionName); }
	}

	@Override
	public int getMCVersionId() { return ((IWorldInfoMixin) this).getVersionId(); }

	@Override
	public void setMCVersionId(int versionId) { ((IWorldInfoMixin) this).setVersionId(versionId); }

	@Override
	public boolean isMCVersionSnapshot() { return ((IWorldInfoMixin) this).isVersionSnapshot(); }

	@Override
	public void setMCVersionSnapshot(boolean versionSnapshot) { ((IWorldInfoMixin) this).setVersionSnapshot(versionSnapshot); }

	@Override
	public int getMCVersionSave() { return getSaveVersion(); }

	@Override
	public void setMCVersionSave(int saveVersion) { setSaveVersion(ValueUtil.correctInt(saveVersion, 0, Integer.MAX_VALUE)); }

	@Override
	public NBTTagCompound getMCPlayerTag() { return getPlayerNBTTagCompound(); }

	@Override
	public INbt getPlayerTag() { return new NBTWrapper(getPlayerNBTTagCompound()); }

	@Override
	public void setMCPlayerTag(NBTTagCompound playerTag) {
		if (playerTag != null) { ((IWorldInfoMixin) this).setPlayerTag(playerTag); }
	}

	@Override
	public void setMCPlayerTag(INbt playerTag) {
		if (playerTag != null) { ((IWorldInfoMixin) this).setPlayerTag(playerTag.getMCNBT()); }
	}

	@Override
	public Map<Integer, NBTTagCompound> getMCDataDimension() { return ((IWorldInfoMixin) this).getDimensionData(); }

	@Override
	public INbt[] getDataDimension() {
		Map<Integer, NBTTagCompound> map = getMCDataDimension();
		int max = map.keySet().stream()
				.mapToInt(Integer::intValue)
				.max()
				.orElse(0);
		INbt[] datas = new INbt[max];
		for (int i = 0; i < max; i++) {
			if (map.containsKey(i)) { datas[i] = new NBTWrapper(map.get(i)); }
		}
		return datas;
	}

	@Override
	public void setMCDataDimension(Map<Integer, NBTTagCompound> map) {
		if (map != null) {
			Map<Integer, NBTTagCompound> dimensionData = ((IWorldInfoMixin) this).getDimensionData();
			dimensionData.clear();
			dimensionData.putAll(map);
		}
	}

	@Override
	public void setDataDimension(INbt[] dimensionData) {
		Map<Integer, NBTTagCompound> dimensionDataIn = ((IWorldInfoMixin) this).getDimensionData();
		dimensionDataIn.clear();
		if (dimensionData != null) {
			for (int i = 0; i < dimensionData.length; i++) {
				if (dimensionData[i] != null) { dimensionDataIn.put(i, dimensionData[i].getMCNBT()); }
			}
		}
	}

	@Override
	public int getMCDimension() { return ((IWorldInfoMixin) this).getDimension(); }

	@Override
	public void update() {
		WorldServer world = DimensionManager.getWorld(dimensionId);
		if (world instanceof WorldCustom) { ((WorldCustom) world).updateWorldInfo(this); }
		DimensionController.getInstance().markDirty();
	}

	public String getDimensionName() { return dimensionName; }

	public void setDimensionName(String name) {
		if (name == null || name.isEmpty()) { name = "default_" + dimensionId; }
		dimensionName = name;
	}

}
