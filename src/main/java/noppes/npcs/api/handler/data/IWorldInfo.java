package noppes.npcs.api.handler.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;
import noppes.npcs.api.INbt;
import noppes.npcs.api.interfaces.ParamName;

import java.util.Map;

@SuppressWarnings("unused")
public interface IWorldInfo {

	int getId();

	INbt getNbt();

	GameRules getMCGameRules();

	void setNbt(@ParamName("nbt") INbt nbt);

	long getMCSeed();

	void setMCSeed(long seed);

	WorldType getMCTerrainType();

	void setMCTerrainType(WorldType type);

	String getMCGeneratorOptions();

	void setMCGeneratorOptions(String options);

	String getMCLevelName();

	void setMCLevelName(String name);

	int getMCSpawnX();

	void setMCSpawnX(int x);

	int getMCSpawnY();

	void setMCSpawnY(int y);

	int getMCSpawnZ();

	void setMCSpawnZ(int z);

	GameType getMCGameType();

	void setMCGameType(GameType type);

	void setMCGameType(int type);

	boolean isMCMapFeaturesEnabled();

	void setMCMapFeaturesEnabled(boolean enabled);

	boolean isMCHardcore();

	void setMCHardcore(boolean hardcore);

	boolean isMCAllowCommands();

	void setMCAllowCommands(boolean allow);

	boolean isMCInitialized();

	void setMCInitialized(boolean init);

	EnumDifficulty getMCDifficulty();

	void setMCDifficulty(EnumDifficulty diff);

	boolean isMCDifficultyLocked();

	void setMCDifficultyLocked(boolean locked);

	boolean isMCRaining();

	void setMCRaining(boolean raining);

	int getMCRainTime();

	void setMCRainTime(int time);

	boolean getMCThundering();

	void setMCThundering(boolean thundering);

	int getMCThunderTime();

	void setMCThunderTime(int time);

	long getMCTotalTime();

	void setMCTotalTime(long time);

	long getMCWorldTime();

	void setMCWorldTime(long time);

	long getMCLastTimePlayed();

	void setMCLastTimePlayed(long time);

	long getMCSizeOnDisk();

	int getMCCleanWeatherTime();

	void setMCCleanWeatherTime(int time);

	double getMCBorderCenterX();

	void setMCBorderCenterX(double posX);

	double getMCBorderCenterZ();

	void setMCBorderCenterZ(double posZ);

	double getMCBorderSize();

	void setMCBorderSize(double size);

	long getMCBorderSizeLerpTime();

	void setMCBorderSizeLerpTime(long time);

	double getMCBorderSizeLerpTarget();

	void setMCBorderSizeLerpTarget(double sizeLerpTarget);

	double getMCBorderSafeZone();

	void setMCBorderSafeZone(double safeZone);

	double getMCBorderDamagePerBlock();

	void setMCBorderDamagePerBlock(double damage);

	int getMCBorderWarningDistance();

	void setMCBorderWarningDistance(int distance);

	String getMCVersionName();

	void setMCVersionName(String versionName);

	int getMCVersionId();

	void setMCVersionId(int versionId);

	boolean isMCVersionSnapshot();

	void setMCVersionSnapshot(boolean versionSnapshot);

	int getMCVersionSave();

	void setMCVersionSave(int saveVersion);

	NBTTagCompound getMCPlayerTag();

	INbt getPlayerTag();

	void setMCPlayerTag(NBTTagCompound playerTag);

	void setMCPlayerTag(INbt playerTag);

	Map<Integer, NBTTagCompound> getMCDataDimension();

	INbt[] getDataDimension();

	void setMCDataDimension(Map<Integer, NBTTagCompound> dimensionData);

	void setDataDimension(INbt[] dimensionData);

	int getMCDimension();

	int getMCBorderWarningTime();

	void setMCBorderWarningTime(int time);

	void update();
}
