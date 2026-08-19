package noppes.npcs.dimensions;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.EventHooks;
import noppes.npcs.api.INbt;
import noppes.npcs.api.event.WorldEvent;
import noppes.npcs.api.handler.data.IWorldProvider;
import noppes.npcs.api.wrapper.NBTWrapper;
import noppes.npcs.controllers.DimensionController;
import noppes.npcs.controllers.IScriptHandler;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.mixin.world.IWorldMixin;
import noppes.npcs.mixin.world.IWorldProviderMixin;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CustomWorldProvider extends WorldProviderSurface implements IWorldProvider {

	protected final DimensionType dimensionType;
	protected final CustomWorldInfo worldInfo;
	protected final NBTTagCompound nbtData;
	protected final IScriptHandler scriptHandler;

	public CustomWorldProvider(DimensionType dimensionTypeIn) {
		super();
		dimensionType = dimensionTypeIn;
		DimensionController dData = DimensionController.getInstance();
		worldInfo = (CustomWorldInfo) dData.getMCWorldInfo(dimensionType.getId());
		nbtData = dData.getProviderInfo(dimensionType.getId()).getMCNBT();

		doesWaterVaporize = nbtData.hasKey("doesWaterVaporize", 1) && nbtData.getBoolean("doesWaterVaporize");
		hasSkyLight = !nbtData.hasKey("hasSkyLight", 1) || nbtData.getBoolean("hasSkyLight");
		nether = nbtData.hasKey("nether", 1) && nbtData.getBoolean("nether");
		if (Util.instance.getSide() == Side.CLIENT) {
			scriptHandler = ScriptController.Instance.clientScripts;
		}
		else { scriptHandler = ScriptController.Instance.playerScripts; }
	}

	@Override
	public WorldType getMCTerrainType() { return ((IWorldProviderMixin) this).getTerrainType(); }

	@Override
	public WorldInfo getMCWorldInfo() { return worldInfo; }

	@Override
	public DimensionType getMCDimensionType() { return dimensionType; }

	@Override
	public INbt getData() { return new NBTWrapper(nbtData); }

	@Override
	public String getGeneratorSettings() { return ((IWorldProviderMixin) this).getGeneratorSettings(); }

	@Override
	public BiomeProvider getMCBiomeProvider() { return biomeProvider; }

	@Override
	public boolean isDoesWaterVaporize() { return doesWaterVaporize; }

	@Override
	public float[] getMCLightBrightnessTable() { return lightBrightnessTable; }

	@Override
	public float[] getMCColorsSunriseSunset() { return ((IWorldProviderMixin) this).getColorsSunriseSunset(); }

	@Override
	protected void generateLightBrightnessTable() {
		NBTTagList list = nbtData.getTagList("lightBrightnessTable", 5);
		if (nbtData.hasKey("lightBrightnessTable", 9) && list.tagCount() >= 15) {
			for (int i = 0; i <= 15; i++) { lightBrightnessTable[i] = list.getFloatAt(i); }
		}
		else {
			for (int i = 0; i <= 15; ++i) {
				float f1 = 1.0F - (float) i / 15.0F;
				lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F);
			}
		}
		onEvent("GenerateLightBrightnessTable", getEvent(lightBrightnessTable));
	}

	@Override
	protected void init() {
		biomeProvider = getMCTerrainType().getBiomeProvider(world);
		onEvent("Init", getEvent(null));
	}

	@Override
	public @Nonnull IChunkGenerator createChunkGenerator() {
		IChunkGenerator generator = getMCTerrainType().getChunkGenerator(world, getGeneratorSettings());
		WorldEvent.ProviderEvent event = getEvent(generator);
		onEvent("CreateChunkGenerator", event);
		return !event.isCanceled() && event.result instanceof IChunkGenerator ? (IChunkGenerator) event.result : generator;
	}

	@Override
	public boolean canCoordinateBeSpawn(int x, int z) {
		BlockPos blockpos = new BlockPos(x, 0, z);
		boolean value;
		if (!nbtData.hasKey("canCoordinateBeSpawn", 1)) {
			if (world.getBiome(blockpos).ignorePlayerSpawnSuitability()) { value = true; }
			else { value = world.getGroundAboveSeaLevel(blockpos).getBlock() == Blocks.GRASS; }
		}
		else { value = nbtData.getBoolean("canCoordinateBeSpawn"); }
		WorldEvent.ProviderEvent event = getEvent(value, x, z);
		onEvent("CanCoordinateBeSpawn", event);
		return !event.isCanceled() && event.result instanceof Boolean ? (boolean) event.result : value;
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTicks) {
		int i = (int)(worldTime % 24000L);
		float value;
		if (!nbtData.hasKey("celestialAngle", 5)) {
			value = ((float)i + partialTicks) / 24000.0F - 0.25F;
			if (value < 0.0F) { ++value; }
			if (value > 1.0F) { --value; }
			float f1 = 1.0F - (float)((Math.cos((double) value * Math.PI) + 1.0D) / 2.0D);
			value = value + (f1 - value) / 3.0F;
		}
		else { value = nbtData.getFloat("celestialAngle"); }
		WorldEvent.ProviderEvent event = getEvent(value, worldTime, partialTicks);
		onEvent("CalculateCelestialAngle", event);
		return !event.isCanceled() && event.result instanceof Float ? (float) event.result : value;
	}

	@Override
	public int getMoonPhase(long worldTime) {
		int value;
		if (!nbtData.hasKey("moonPhase", 5)) { value = (int)(worldTime / 24000L % 8L + 8L) % 8; }
		else { value = nbtData.getInteger("moonPhase"); }
		WorldEvent.ProviderEvent event = getEvent(value, worldTime);
		onEvent("GetMoonPhase", event);
		return !event.isCanceled() && event.result instanceof Integer ? (int) event.result : value;
	}

	@Override
	public boolean isSurfaceWorld() { return false; }

	@Override
	@SideOnly(Side.CLIENT)
	public @Nullable float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
		NBTTagList list = nbtData.getTagList("sunriseSunsetColors", 5);
		float[] colorsSunriseSunset = null;
		if (nbtData.hasKey("sunriseSunsetColors", 9) && list.tagCount() >= 4) {
			colorsSunriseSunset = new float[4];
			for (int i = 0; i <= 4; i++) { colorsSunriseSunset[i] = list.getFloatAt(i); }
		}
		else {
			float f = 0.4F;
			float f1 = MathHelper.cos(celestialAngle * ((float)Math.PI * 2F)) - 0.0F;
			float f2 = -0.0F;
			if (f1 >= -f && f1 <= f) {
				float f3 = (f1 - f2) / f * 0.5F + 0.5F;
				float f4 = 1.0F - (1.0F - MathHelper.sin(f3 * (float)Math.PI)) * 0.99F;
				f4 = f4 * f4;
				colorsSunriseSunset = getMCColorsSunriseSunset();
				colorsSunriseSunset[0] = f3 * 0.3F + 0.7F;
				colorsSunriseSunset[1] = f3 * f3 * 0.7F + 0.2F;
				colorsSunriseSunset[2] = f3 * f3 * 0.0F + 0.2F;
				colorsSunriseSunset[3] = f4;
			}
		}
		WorldEvent.ProviderEvent event = getEvent(colorsSunriseSunset, celestialAngle, partialTicks);;
		onEvent("CalcSunriseSunsetColors", event);
		return !event.isCanceled() && event.result instanceof float[] ? (float[]) event.result : colorsSunriseSunset;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public @Nonnull Vec3d getFogColor(float celestialAngle, float partialTicks) {
		NBTTagList list = nbtData.getTagList("fogColor", 6);
		Vec3d value;
		if (nbtData.hasKey("fogColor", 9) && list.tagCount() >= 3) {
			value = new Vec3d(list.getDoubleAt(0), list.getDoubleAt(1), list.getDoubleAt(2));
		}
		else {
			float f = MathHelper.cos(celestialAngle * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
			f = MathHelper.clamp(f, 0.0F, 1.0F);
			float f1 = 0.7529412F;
			float f2 = 0.84705883F;
			float f3 = 1.0F;
			f1 = f1 * (f * 0.94F + 0.06F);
			f2 = f2 * (f * 0.94F + 0.06F);
			f3 = f3 * (f * 0.91F + 0.09F);
			value = new Vec3d(f1, f2, f3);
		}
		WorldEvent.ProviderEvent event = getEvent(value, celestialAngle, partialTicks);
		onEvent("CetFogColor", event);
		return !event.isCanceled() && event.result instanceof Vec3d ? (Vec3d) event.result : value;
	}

	@Override
	public boolean canRespawnHere() {
		WorldEvent.ProviderEvent event = getEvent(!nbtData.hasKey("canRespawnHere", 1) || nbtData.getBoolean("canRespawnHere"));
		onEvent("CanRespawnHere", event);
		return event.isCanceled() || !(event.result instanceof Boolean) || (boolean) event.result;
	}

	@SideOnly(Side.CLIENT)
	public float getCloudHeight() {
		float value = nbtData.hasKey("cloudHeight", 5) ? nbtData.getFloat("cloudHeight") : getMCTerrainType().getCloudHeight();
		WorldEvent.ProviderEvent event = getEvent(value);
		onEvent("GetCloudHeight", event);
		return !event.isCanceled() && event.result instanceof Float ? (float) event.result : value;
	}

	@SideOnly(Side.CLIENT)
	public boolean isSkyColored() {
		WorldEvent.ProviderEvent event = getEvent(!nbtData.hasKey("isSkyColored", 1) || nbtData.getBoolean("isSkyColored"));
		onEvent("IsSkyColored", event);
		return event.isCanceled() || !(event.result instanceof Boolean) || (boolean) event.result;
	}

	public @Nullable BlockPos getSpawnCoordinate() {

		return null;
	}

	public int getAverageGroundLevel()
	{
		return getMCTerrainType().getMinimumSpawnHeight(world);
	}

	@SideOnly(Side.CLIENT)
	public double getVoidFogYFactor()
	{
		return getMCTerrainType().voidFadeMagnitude();
	}

	@SideOnly(Side.CLIENT)
	public boolean doesXZShowFog(int x, int z)
	{
		return false;
	}

	public BiomeProvider getBiomeProvider()
	{
		return biomeProvider;
	}

	public boolean doesWaterVaporize()
	{
		return doesWaterVaporize;
	}

	public boolean hasSkyLight() {
		return hasSkyLight;
	}

	public float[] getLightBrightnessTable() {
		return lightBrightnessTable;
	}

	public WorldBorder createWorldBorder() {
		return new WorldBorder();
	}

	/*======================================= Forge Start =========================================*/
	private net.minecraftforge.client.IRenderHandler skyRenderer = null;
	private net.minecraftforge.client.IRenderHandler cloudRenderer = null;
	private net.minecraftforge.client.IRenderHandler weatherRenderer = null;
	private int dimensionId;

	public void setDimension(int dim)
	{
		dimensionId = dim;
	}
	public int getDimension()
	{
		return dimensionId;
	}

	@Nullable
	public String getSaveFolder()
	{
		return (dimensionId == 0 ? null : "DIM" + dimensionId);
	}

	public double getMovementFactor() {
		return isNether() ? 8.0 : 1.0;
	}

	public boolean shouldClientCheckLighting() { return true; }

	@Nullable
	@SideOnly(Side.CLIENT)
	public net.minecraftforge.client.IRenderHandler getSkyRenderer()
	{
		return skyRenderer;
	}

	@SideOnly(Side.CLIENT)
	public void setSkyRenderer(net.minecraftforge.client.IRenderHandler skyRenderer)
	{
		skyRenderer = skyRenderer;
	}

	@Nullable
	@SideOnly(Side.CLIENT)
	public net.minecraftforge.client.IRenderHandler getCloudRenderer()
	{
		return cloudRenderer;
	}

	@SideOnly(Side.CLIENT)
	public void setCloudRenderer(net.minecraftforge.client.IRenderHandler renderer)
	{
		cloudRenderer = renderer;
	}

	@Nullable
	@SideOnly(Side.CLIENT)
	public net.minecraftforge.client.IRenderHandler getWeatherRenderer()
	{
		return weatherRenderer;
	}

	@SideOnly(Side.CLIENT)
	public void setWeatherRenderer(net.minecraftforge.client.IRenderHandler renderer)
	{
		weatherRenderer = renderer;
	}

	public void getLightmapColors(float partialTicks, float sunBrightness, float skyLight, float blockLight, float[] colors) {}

	public BlockPos getRandomizedSpawnPoint()
	{
		BlockPos ret = world.getSpawnPoint();

		boolean isAdventure = world.getWorldInfo().getGameType() == GameType.ADVENTURE;
		int spawnFuzz = world instanceof WorldServer ? getMCTerrainType().getSpawnFuzz((WorldServer)world, world.getMinecraftServer()) : 1;
		int border = MathHelper.floor(world.getWorldBorder().getClosestDistance(ret.getX(), ret.getZ()));
		if (border < spawnFuzz) spawnFuzz = border;

		if (!isNether() && !isAdventure && spawnFuzz != 0)
		{
			if (spawnFuzz < 2) spawnFuzz = 2;
			int spawnFuzzHalf = spawnFuzz / 2;
			ret = world.getTopSolidOrLiquidBlock(ret.add(spawnFuzzHalf - world.rand.nextInt(spawnFuzz), 0, spawnFuzzHalf - world.rand.nextInt(spawnFuzz)));
		}

		return ret;
	}

	public boolean shouldMapSpin(String entity, double x, double z, double rotation)
	{
		return dimensionId < 0;
	}

	public int getRespawnDimension(net.minecraft.entity.player.EntityPlayerMP player)
	{
		return player.getSpawnDimension();
	}

	@Nullable
	public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities() {
		return null;
	}

	@Nullable
	@SideOnly(Side.CLIENT)
	public net.minecraft.client.audio.MusicTicker.MusicType getMusicType()
	{
		return null;
	}

	public WorldProvider.WorldSleepResult canSleepAt(net.minecraft.entity.player.EntityPlayer player, BlockPos pos)
	{
		return (canRespawnHere() && world.getBiome(pos) != net.minecraft.init.Biomes.HELL) ? WorldProvider.WorldSleepResult.ALLOW : WorldProvider.WorldSleepResult.BED_EXPLODES;
	}

	public static enum WorldSleepResult
	{
		ALLOW,
		DENY,
		BED_EXPLODES;
	}

	/*======================================= Start Moved From World =========================================*/

	public Biome getBiomeForCoords(BlockPos pos)
	{
		return world.getBiomeForCoordsBody(pos);
	}

	public boolean isDaytime()
	{
		return world.getSkylightSubtracted() < 4;
	}

	/**
	 * The current sun brightness factor for this dimension.
	 * 0.0f means no light at all, and 1.0f means maximum sunlight.
	 * This will be used for the "calculateSkylightSubtracted"
	 * which is for Sky light value calculation.
	 *
	 * @return The current brightness factor
	 * */
	public float getSunBrightnessFactor(float par1)
	{
		return world.getSunBrightnessFactor(par1);
	}

	/**
	 * Calculates the current moon phase factor.
	 * This factor is effective for slimes.
	 * (This method do not affect the moon rendering)
	 * */
	public float getCurrentMoonPhaseFactor()
	{
		return world.getCurrentMoonPhaseFactorBody();
	}

	@SideOnly(Side.CLIENT)
	public Vec3d getSkyColor(net.minecraft.entity.Entity cameraEntity, float partialTicks)
	{
		return world.getSkyColorBody(cameraEntity, partialTicks);
	}

	@SideOnly(Side.CLIENT)
	public Vec3d getCloudColor(float partialTicks)
	{
		return world.getCloudColorBody(partialTicks);
	}

	/**
	 * Gets the Sun Brightness for rendering sky.
	 * */
	@SideOnly(Side.CLIENT)
	public float getSunBrightness(float par1)
	{
		return world.getSunBrightnessBody(par1);
	}

	/**
	 * Gets the Star Brightness for rendering sky.
	 * */
	@SideOnly(Side.CLIENT)
	public float getStarBrightness(float par1)
	{
		return world.getStarBrightnessBody(par1);
	}

	public void setAllowedSpawnTypes(boolean allowHostile, boolean allowPeaceful)
	{
		((IWorldMixin) world).setSpawnHostileMobs(allowHostile);
		((IWorldMixin) world).setSpawnPeacefulMobs(allowPeaceful);
	}

	public void calculateInitialWeather()
	{
		world.calculateInitialWeatherBody();
	}

	public void updateWeather()
	{
		world.updateWeatherBody();
	}

	public boolean canBlockFreeze(BlockPos pos, boolean byWater)
	{
		return world.canBlockFreezeBody(pos, byWater);
	}

	public boolean canSnowAt(BlockPos pos, boolean checkLight)
	{
		return world.canSnowAtBody(pos, checkLight);
	}
	public void setWorldTime(long time)
	{
		world.getWorldInfo().setWorldTime(time);
	}

	public long getSeed()
	{
		return world.getWorldInfo().getSeed();
	}

	public long getWorldTime()
	{
		return world.getWorldInfo().getWorldTime();
	}

	public BlockPos getSpawnPoint()
	{
		WorldInfo info = world.getWorldInfo();
		return new BlockPos(info.getSpawnX(), info.getSpawnY(), info.getSpawnZ());
	}

	public void setSpawnPoint(BlockPos pos)
	{
		world.getWorldInfo().setSpawn(pos);
	}

	public boolean canMineBlock(net.minecraft.entity.player.EntityPlayer player, BlockPos pos)
	{
		return world.canMineBlockBody(player, pos);
	}

	public boolean isBlockHighHumidity(BlockPos pos)
	{
		return world.getBiome(pos).isHighHumidity();
	}

	public int getHeight()
	{
		return 256;
	}

	public int getActualHeight() {
		return nether ? 128 : 256;
	}

	public double getHorizon()
	{
		return world.getWorldInfo().getTerrainType().getHorizon(world);
	}

	public void resetRainAndThunder() {
		world.getWorldInfo().setRainTime(0);
		world.getWorldInfo().setRaining(false);
		world.getWorldInfo().setThunderTime(0);
		world.getWorldInfo().setThundering(false);
	}

	@Override
	public boolean canDoLightning(net.minecraft.world.chunk.Chunk chunk) {
		return true;
	}

	@Override
	public boolean canDoRainSnowIce(net.minecraft.world.chunk.Chunk chunk) {
		return true;
	}

	@Override
	public void onPlayerAdded(EntityPlayerMP player) { }

	@Override
	public void onPlayerRemoved(EntityPlayerMP player) { }

	@Override
	public @Nonnull DimensionType getDimensionType() { return dimensionType; }

	@Override
	public void onWorldSave() { }

	@Override
	public void onWorldUpdateEntities() { }

	public boolean canDropChunk(int x, int z) {
		return true;
	}

	protected WorldEvent.ProviderEvent getEvent(Object result, Object ... parameters) { return new WorldEvent.ProviderEvent(this, result, parameters); }

	private void onEvent(String methodName, WorldEvent.ProviderEvent event) {
		EventHooks.onEvent(scriptHandler, "provider" + methodName, event);
	}

}
