package noppes.npcs.dimensions;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

import javax.annotation.Nonnull;

public class WorldCustom extends WorldServer {

	private final WorldServer delegate;
	private final IBorderListener borderListener;

	public WorldCustom(CustomWorldInfo worldInfo, MinecraftServer server, ISaveHandler saveHandlerIn,
					   int dimensionId, WorldServer delegateIn, Profiler profilerIn) {
		super(server, saveHandlerIn, worldInfo, dimensionId, profilerIn);
		delegate = delegateIn;
		WorldBorder borderIn = getWorldBorder();
		borderListener = new IBorderListener() {
			@Override
			public void onCenterChanged(@Nonnull WorldBorder border, double x, double z) {
				borderIn.setCenter(x, z);
			}

			@Override
			public void onDamageAmountChanged(@Nonnull WorldBorder border, double newAmount) {
				borderIn.setDamageAmount(newAmount);
			}

			@Override
			public void onDamageBufferChanged(@Nonnull WorldBorder border, double newSize) {
				borderIn.setDamageBuffer(newSize);
			}

			@Override
			public void onSizeChanged(@Nonnull WorldBorder border, double newSize) {
				borderIn.setTransition(newSize);
			}

			@Override
			public void onTransitionStarted(@Nonnull WorldBorder border, double oldSize, double newSize, long time) {
				borderIn.setTransition(oldSize, newSize, time);
			}

			@Override
			public void onWarningDistanceChanged(@Nonnull WorldBorder border, int newDistance) {
				borderIn.setWarningDistance(newDistance);
			}

			@Override
			public void onWarningTimeChanged(@Nonnull WorldBorder border, int newTime) {
				borderIn.setWarningTime(newTime);
			}

		};
		borderIn.addListener(borderListener);
	}

	@Override
	public void flush() {
		super.flush();
		delegate.getWorldBorder().removeListener(borderListener); // Unlink ourselves, to prevent world leak.
	}

	/**
	 * Update the WorldInfo for this custom world.
	 * Used when editing dimension settings.
	 */
	public void updateWorldInfo(WorldInfo info) {
		worldInfo = info;
		init();
	}

	/*
	@Override
	public @Nonnull World init() {
		mapStorage = delegate.getMapStorage();
		worldScoreboard = delegate.getScoreboard();
		lootTable = delegate.getLootTableManager();

		String s = VillageCollection.fileNameForProvider(provider);
		VillageCollection villagecollection = (VillageCollection) perWorldStorage
				.getOrLoadData(VillageCollection.class, s);
		if (villagecollection == null) {
			villageCollection = new VillageCollection(this);
			perWorldStorage.setData(s, villageCollection);
		} else {
			villageCollection = villagecollection;
			villageCollection.setWorldsForAll(this);
		}
		return super.init();
	}

	@Override
	protected void saveLevel() {
		perWorldStorage.saveAllData();
	}
	/**/

}
