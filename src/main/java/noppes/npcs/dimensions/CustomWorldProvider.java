package noppes.npcs.dimensions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraft.world.chunk.Chunk;
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
import noppes.npcs.controllers.scripts.IScriptHandler;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.mixin.world.IWorldProviderMixin;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class CustomWorldProvider extends WorldProviderSurface implements IWorldProvider {

	protected final DimensionType dimensionType;
	protected final NBTTagCompound nbtData;
	protected final IScriptHandler scriptHandler;

	public CustomWorldProvider(DimensionType dimensionTypeIn) {
		super();
		dimensionType = dimensionTypeIn;
		nbtData = DimensionController.getInstance().getProviderInfo(dimensionType.getId()).getMCNBT();

		doesWaterVaporize = nbtData.hasKey("doesWaterVaporize", 1) && nbtData.getBoolean("doesWaterVaporize");
		hasSkyLight = !nbtData.hasKey("hasSkyLight", 1) || nbtData.getBoolean("hasSkyLight");
		nether = nbtData.hasKey("nether", 1) && nbtData.getBoolean("nether");
		if (Util.instance.getSide() == Side.CLIENT) {
			scriptHandler = ScriptController.Instance.clientScripts;
		}
		else { scriptHandler = ScriptController.Instance.playerScripts; }
	}

	@Override
	public WorldType getMCTerrainType() { return ((IWorldProviderMixin) this).gTerrainType(); }

	@Override
	public WorldInfo getMCWorldInfo() {
		WorldInfo worldInfo = (WorldInfo) DimensionController.getInstance().getMCWorldInfo(dimensionType.getId());
		if (worldInfo != null && world != null) { worldInfo = world.getWorldInfo(); }
		return worldInfo;
	}

	@Override
	public DimensionType getMCDimensionType() { return dimensionType; }

	@Override
	public INbt getData() { return new NBTWrapper(nbtData); }

	@Override
	public String getGeneratorSettings() { return ((IWorldProviderMixin) this).gGeneratorSettings(); }

	@Override
	public BiomeProvider getMCBiomeProvider() { return biomeProvider; }

	@Override
	public boolean isDoesWaterVaporize() { return doesWaterVaporize; }

	@Override
	public float[] getMCLightBrightnessTable() { return lightBrightnessTable; }

	@Override
	public float[] getMCColorsSunriseSunset() { return ((IWorldProviderMixin) this).gColorsSunriseSunset(); }

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
		onEvent("GenerateLightBrightnessTable", lightBrightnessTable);
	}

	@Override
	protected void init() {
		biomeProvider = getMCTerrainType().getBiomeProvider(world);
		onEvent("Init", biomeProvider);
	}

	@Override
	public @Nonnull IChunkGenerator createChunkGenerator() {
		IChunkGenerator value = getMCTerrainType().getChunkGenerator(world, getGeneratorSettings());
		WorldEvent.ProviderEvent event = onEvent("CreateChunkGenerator", value);
		return !event.isCanceled() && event.result instanceof IChunkGenerator ? (IChunkGenerator) event.result : value;
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
		WorldEvent.ProviderEvent event = onEvent("CanCoordinateBeSpawn", value, x, z);
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
		WorldEvent.ProviderEvent event = onEvent("CalculateCelestialAngle", value, worldTime, partialTicks);
		return !event.isCanceled() && event.result instanceof Float ? (float) event.result : value;
	}

	@Override
	public int getMoonPhase(long worldTime) {
		int value = nbtData.hasKey("moonPhase", 5) ? nbtData.getInteger("moonPhase") : (int)(worldTime / 24000L % 8L + 8L) % 8;
		WorldEvent.ProviderEvent event = onEvent("GetMoonPhase", value, worldTime);
		return !event.isCanceled() && event.result instanceof Integer ? (int) event.result : value;
	}

	@Override
	public boolean isSurfaceWorld() { return false; }

	@Override
	@SideOnly(Side.CLIENT)
	public @Nullable float[] calcSunriseSunsetColors(float celestialAngle, float partialTicks) {
		NBTTagList list = nbtData.getTagList("sunriseSunsetColors", 5);
		float[] value = null;
		if (nbtData.hasKey("sunriseSunsetColors", 9) && list.tagCount() >= 4) {
			value = new float[4];
			for (int i = 0; i <= 4; i++) { value[i] = list.getFloatAt(i); }
		}
		else {
			float f = 0.4F;
			float f1 = MathHelper.cos(celestialAngle * ((float)Math.PI * 2F)) - 0.0F;
			float f2 = -0.0F;
			if (f1 >= -f && f1 <= f) {
				float f3 = (f1 - f2) / f * 0.5F + 0.5F;
				float f4 = 1.0F - (1.0F - MathHelper.sin(f3 * (float)Math.PI)) * 0.99F;
				f4 = f4 * f4;
				value = getMCColorsSunriseSunset();
				value[0] = f3 * 0.3F + 0.7F;
				value[1] = f3 * f3 * 0.7F + 0.2F;
				value[2] = f3 * f3 * 0.0F + 0.2F;
				value[3] = f4;
			}
		}
		WorldEvent.ProviderEvent event = onEvent("CalcSunriseSunsetColors", value, celestialAngle, partialTicks);
		return !event.isCanceled() && event.result instanceof float[] ? (float[]) event.result : value;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public @Nonnull Vec3d getFogColor(float celestialAngle, float partialTicks) {
		NBTTagList list = nbtData.getTagList("fogColor", 6);
		Vec3d value;
		if (nbtData.hasKey("fogColor", 9) && list.tagCount() >= 3) { value = new Vec3d(list.getDoubleAt(0), list.getDoubleAt(1), list.getDoubleAt(2)); }
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
		WorldEvent.ProviderEvent event = onEvent("CetFogColor", value, celestialAngle, partialTicks);
		return !event.isCanceled() && event.result instanceof Vec3d ? (Vec3d) event.result : value;
	}

	@Override
	public boolean canRespawnHere() { return !nbtData.hasKey("canRespawnHere", 1) || nbtData.getBoolean("canRespawnHere"); }

	@Override
	@SideOnly(Side.CLIENT)
	public float getCloudHeight() { return nbtData.hasKey("cloudHeight", 5) ? nbtData.getFloat("cloudHeight") : getMCTerrainType().getCloudHeight(); }

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isSkyColored() { return !nbtData.hasKey("isSkyColored", 1) || nbtData.getBoolean("isSkyColored"); }

	@Override
	public @Nullable BlockPos getSpawnCoordinate() { return nbtData.hasKey("spawnCoordinate", 4) ? BlockPos.fromLong(nbtData.getLong("spawnCoordinate")) : null; }

	@Override
	public int getAverageGroundLevel() { return nbtData.hasKey("averageGroundLevel", 3) ? nbtData.getInteger("averageGroundLevel") : getMCTerrainType().getMinimumSpawnHeight(world); }

	@Override
	@SideOnly(Side.CLIENT)
	public double getVoidFogYFactor() {
		double value = nbtData.hasKey("voidFogYFactor", 6) ? nbtData.getDouble("voidFogYFactor") : getMCTerrainType().voidFadeMagnitude();
		WorldEvent.ProviderEvent event = onEvent("GetVoidFogYFactor", value);
		return !event.isCanceled() && event.result instanceof Double ? (double) event.result : value;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean doesXZShowFog(int x, int z) {
		WorldEvent.ProviderEvent event = onEvent("DoesXZShowFog", !nbtData.hasKey("doesXZShowFog", 1) && nbtData.getBoolean("doesXZShowFog"), x, z);
		return !event.isCanceled() && event.result instanceof Boolean && (boolean) event.result;
	}

	@Override
	public void setDimension(int dim) { }

	@Override
	public int getDimension() { return dimensionType.getId(); }

	@Override
	public @Nullable String getSaveFolder() { return getDimension() == 0 ? null : "DIM" + getDimension(); }

	@Override
	public double getMovementFactor() { return isNether() ? 8.0 : 1.0; }

	@Override
	public boolean shouldClientCheckLighting() {
		WorldEvent.ProviderEvent event = onEvent("ShouldClientCheckLighting", !nbtData.hasKey("shouldClientCheckLighting", 1) || nbtData.getBoolean("shouldClientCheckLighting"));
		return event.isCanceled() || !(event.result instanceof Boolean) || (boolean) event.result;
	}

	@Override
	public @Nonnull BlockPos getRandomizedSpawnPoint() {
		BlockPos value;
		if (nbtData.hasKey("spawnPoint", 4)) { value = BlockPos.fromLong(nbtData.getLong("spawnPoint")); }
		else {
			value = !nbtData.hasKey("spawnPoint", 4) ? BlockPos.fromLong(nbtData.getLong("spawnPoint")) : world.getSpawnPoint();
			boolean isAdventure = world.getWorldInfo().getGameType() == GameType.ADVENTURE;
			int spawnFuzz = world instanceof WorldServer ? getMCTerrainType().getSpawnFuzz((WorldServer)world, Objects.requireNonNull(world.getMinecraftServer())) : 1;
			int border = MathHelper.floor(world.getWorldBorder().getClosestDistance(value.getX(), value.getZ()));
			if (border < spawnFuzz) spawnFuzz = border;

			if (!isNether() && !isAdventure && spawnFuzz != 0)
			{
				if (spawnFuzz < 2) spawnFuzz = 2;
				int spawnFuzzHalf = spawnFuzz / 2;
				value = world.getTopSolidOrLiquidBlock(value.add(spawnFuzzHalf - world.rand.nextInt(spawnFuzz), 0, spawnFuzzHalf - world.rand.nextInt(spawnFuzz)));
			}
		}
		WorldEvent.ProviderEvent event = onEvent("GetRandomizedSpawnPoint", value);
		return !event.isCanceled() && event.result instanceof BlockPos ? (BlockPos) event.result : value;
	}

	@Override
	public boolean shouldMapSpin(@Nonnull String entity, double x, double z, double rotation) {
		boolean value = nbtData.hasKey("shouldMapSpin", 1) ? nbtData.getBoolean("shouldMapSpin") : getDimension() < 0;
		WorldEvent.ProviderEvent event = onEvent("ShouldMapSpin", value, x, z, rotation);
		return !event.isCanceled() && event.result instanceof Boolean ? (boolean) event.result : value;
	}

	@Override
	public int getRespawnDimension(@Nonnull EntityPlayerMP player) {
		int value = nbtData.hasKey("respawnDimension", 3) ? nbtData.getInteger("respawnDimension") : player.getSpawnDimension();
		WorldEvent.ProviderEvent event = onEvent("GetRespawnDimension", value, player);
		return !event.isCanceled() && event.result instanceof Integer ? (int) event.result : value;
	}

	@Override
	public @Nonnull WorldProvider.WorldSleepResult canSleepAt(@Nonnull EntityPlayer player, @Nonnull BlockPos pos) {
		WorldProvider.WorldSleepResult value = nbtData.hasKey("canSleepAt", 1) && nbtData.getBoolean("canSleepAt") ? WorldProvider.WorldSleepResult.ALLOW :
				(canRespawnHere() && world.getBiome(pos) != net.minecraft.init.Biomes.HELL) ? WorldProvider.WorldSleepResult.ALLOW : WorldProvider.WorldSleepResult.BED_EXPLODES;
		WorldEvent.ProviderEvent event = onEvent("CanSleepAt", value, player, pos);
		return !event.isCanceled() && event.result instanceof WorldProvider.WorldSleepResult ? (WorldProvider.WorldSleepResult) event.result : value;
	}


	/*======================================= Start Moved From World =========================================*/
	@Override
	public @Nonnull Biome getBiomeForCoords(@Nonnull BlockPos pos) {
		Biome value = world.getBiomeForCoordsBody(pos);
		WorldEvent.ProviderEvent event = onEvent("GetBiomeForCoords", value, pos);
		return !event.isCanceled() && event.result instanceof Biome ? (Biome) event.result : value;
	}

	@Override
	public boolean isDaytime() { return nbtData.hasKey("isDaytime", 1) ? nbtData.getBoolean("isDaytime") : world.getSkylightSubtracted() < 4; }

	@Override
	public float getSunBrightnessFactor(float partialTicks) { return nbtData.hasKey("sunBrightnessFactor", 5) ? nbtData.getFloat("sunBrightnessFactor") : world.getSunBrightnessFactor(partialTicks); }

	@Override
	public float getCurrentMoonPhaseFactor() { return nbtData.hasKey("currentMoonPhaseFactor", 5) ? nbtData.getFloat("currentMoonPhaseFactor") : world.getCurrentMoonPhaseFactorBody(); }

	@Override
	@SideOnly(Side.CLIENT)
	public @Nonnull Vec3d getSkyColor(@Nonnull Entity cameraEntity, float partialTicks) {
		NBTTagList list = nbtData.getTagList("skyColor", 6);
		Vec3d value;
		if (nbtData.hasKey("skyColor", 9) && list.tagCount() >= 3) { value = new Vec3d(list.getDoubleAt(0), list.getDoubleAt(1), list.getDoubleAt(2)); }
		else { value = world.getSkyColorBody(cameraEntity, partialTicks); }
		WorldEvent.ProviderEvent event = onEvent("GetSkyColor", value, cameraEntity, partialTicks);
		return !event.isCanceled() && event.result instanceof Vec3d ? (Vec3d) event.result : value;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public @Nonnull Vec3d getCloudColor(float partialTicks) {
		NBTTagList list = nbtData.getTagList("cloudColor", 6);
		Vec3d value;
		if (nbtData.hasKey("cloudColor", 9) && list.tagCount() >= 3) { value = new Vec3d(list.getDoubleAt(0), list.getDoubleAt(1), list.getDoubleAt(2)); }
		else { value = world.getCloudColorBody(partialTicks); }
		WorldEvent.ProviderEvent event = onEvent("GetCloudColor", value, partialTicks);
		return !event.isCanceled() && event.result instanceof Vec3d ? (Vec3d) event.result : value;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getSunBrightness(float partialTicks) {
		float value = nbtData.hasKey("sunBrightness", 5) ? nbtData.getFloat("sunBrightness") : world.getSunBrightnessBody(partialTicks);
		WorldEvent.ProviderEvent event = onEvent("GetSunBrightness", value, partialTicks);
		return !event.isCanceled() && event.result instanceof Float ? (float) event.result : value;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getStarBrightness(float partialTicks) {
		float value = nbtData.hasKey("starBrightness", 5) ? nbtData.getFloat("starBrightness") : world.getStarBrightnessBody(partialTicks);
		WorldEvent.ProviderEvent event = onEvent("GetStarBrightness", value, partialTicks);
		return !event.isCanceled() && event.result instanceof Float ? (float) event.result : value;
	}

	@Override
	public void calculateInitialWeather() {
		if (!onEvent("CalculateInitialWeather", null).isCanceled()) { world.calculateInitialWeatherBody(); }
	}

	@Override
	public void updateWeather() {
		if (!onEvent("UpdateWeather", null).isCanceled()) { world.updateWeatherBody(); }
	}

	@Override
	public boolean canBlockFreeze(@Nonnull BlockPos pos, boolean byWater) {
		boolean value = nbtData.hasKey("canBlockFreeze", 1) ? nbtData.getBoolean("canBlockFreeze") : world.canBlockFreezeBody(pos, byWater);
		WorldEvent.ProviderEvent event = onEvent("CanBlockFreeze", value, pos, byWater);
		return !event.isCanceled() && event.result instanceof Boolean ? (boolean) event.result : value;
	}

	@Override
	public boolean canSnowAt(@Nonnull BlockPos pos, boolean checkLight) {
		boolean value = nbtData.hasKey("canBlockFreeze", 1) ? nbtData.getBoolean("canBlockFreeze") : world.canSnowAtBody(pos, checkLight);
		WorldEvent.ProviderEvent event = onEvent("CanBlockFreeze", value, pos, checkLight);
		return !event.isCanceled() && event.result instanceof Boolean ? (boolean) event.result : value;
	}

	@Override
	public @Nonnull BlockPos getSpawnPoint() {
		BlockPos value = nbtData.hasKey("spawnPoint", 4) ? BlockPos.fromLong(nbtData.getLong("spawnPoint")) :
				new BlockPos(world.getWorldInfo().getSpawnX(), world.getWorldInfo().getSpawnY(), world.getWorldInfo().getSpawnZ());
		WorldEvent.ProviderEvent event = onEvent("CanBlockFreeze", value);
		return !event.isCanceled() && event.result instanceof BlockPos ? (BlockPos) event.result : value;
	}

	@Override
	public boolean canMineBlock(@Nonnull EntityPlayer player, @Nonnull BlockPos pos) {
		boolean value = nbtData.hasKey("canMineBlock", 1) ? nbtData.getBoolean("canMineBlock") : world.canMineBlockBody(player, pos);
		WorldEvent.ProviderEvent event = onEvent("CanMineBlock", value, player, pos);
		return !event.isCanceled() && event.result instanceof Boolean ? (boolean) event.result : value;
	}

	@Override
	public boolean isBlockHighHumidity(@Nonnull BlockPos pos) {
		boolean value = nbtData.hasKey("isBlockHighHumidity", 1) ? nbtData.getBoolean("isBlockHighHumidity") : world.getBiome(pos).isHighHumidity();
		WorldEvent.ProviderEvent event = onEvent("IsBlockHighHumidity", value);
		return !event.isCanceled() && event.result instanceof Boolean ? (boolean) event.result : value;
	}

	@Override
	public int getHeight() { return nbtData.hasKey("height", 3) ? nbtData.getInteger("height") : 256; }

	@Override
	public int getActualHeight() { return nbtData.hasKey("actualHeight", 3) ? nbtData.getInteger("actualHeight") : nether ? 128 : 256; }

	@Override
	public double getHorizon() { return nbtData.hasKey("horizon", 6) ? nbtData.getDouble("horizon") : world.getWorldInfo().getTerrainType().getHorizon(world); }

	@Override
	public boolean canDoLightning(@Nonnull Chunk chunk) { return !nbtData.hasKey("canDoLightning", 6) || nbtData.getBoolean("canDoLightning"); }

	@Override
	public boolean canDoRainSnowIce(@Nonnull Chunk chunk) { return !nbtData.hasKey("canDoRainSnowIce", 6) || nbtData.getBoolean("canDoRainSnowIce"); }

	@Override
	public void onPlayerAdded(@Nonnull EntityPlayerMP player) { onEvent("OnPlayerAdded", null); }

	@Override
	public void onPlayerRemoved(@Nonnull EntityPlayerMP player) { onEvent("OnPlayerRemoved", null); }

	@Override
	public @Nonnull DimensionType getDimensionType() { return dimensionType; }

	@Override
	public void onWorldSave() { onEvent("OnWorldSave", null); }

	@Override
	public void onWorldUpdateEntities() { onEvent("OnWorldUpdateEntities", null); }

	@Override
	public boolean canDropChunk(int x, int z) {
		boolean value = !nbtData.hasKey("canDropChunk", 1) || nbtData.getBoolean("canDropChunk");
		WorldEvent.ProviderEvent event = onEvent("CanDropChunk", value, x, z);
		return !event.isCanceled() && event.result instanceof Boolean ? (boolean) event.result : value;
	}

	protected WorldEvent.ProviderEvent onEvent(String methodName, Object result, Object ... parameters) {
		WorldEvent.ProviderEvent event = new WorldEvent.ProviderEvent(this, result, parameters);
		EventHooks.onEvent(scriptHandler, "provider" + methodName, event);
		return event;
	}

}
