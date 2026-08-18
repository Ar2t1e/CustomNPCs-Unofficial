package noppes.npcs.controllers;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.EnumHelper;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.gui.IDimensionGetter;
import noppes.npcs.api.wrapper.NBTWrapper;
import noppes.npcs.controllers.data.DimensionData;
import noppes.npcs.dimensions.CustomWorldInfo;
import noppes.npcs.dimensions.CustomWorldProvider;
import noppes.npcs.dimensions.WorldCustom;
import noppes.npcs.mixin.minecraftforge.common.IDimensionManagerMixin;
import noppes.npcs.packets.server.SPacketDimensionTeleport;
import noppes.npcs.packets.server.SPacketDimensionsGet;
import noppes.npcs.util.CustomNPCsScheduler;
import noppes.npcs.util.Util;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.api.INbt;
import noppes.npcs.api.handler.IDimensionHandler;
import noppes.npcs.api.handler.data.IWorldInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DimensionController extends WorldSavedData implements IDimensionHandler {

	protected static String NAME = "cnpcs_dimensions";
	protected static boolean update = false;

	public static DimensionController getInstance() {
		DimensionController INSTANCE;
		MapStorage mapStorage = null;
		try { mapStorage = CustomNpcs.Server != null ? CustomNpcs.Server.getEntityWorld().getMapStorage() : null; } catch (Exception ignored) { }
        if (mapStorage != null) {
			INSTANCE = (DimensionController) mapStorage.getOrLoadData(DimensionController.class, NAME);
			if (INSTANCE == null) {
				INSTANCE = (DimensionController) mapStorage.getOrLoadData(DimensionController.class, "CustomNpcsHandler");
				update = true;
			} // OLD
			if (INSTANCE == null) {
				INSTANCE = new DimensionController(NAME);
				mapStorage.setData(NAME, INSTANCE);
				update = true;
			} // not register
		}
		else { INSTANCE = new DimensionController(NAME); }
		INSTANCE.checkExample();
		return INSTANCE;
	}

	public DimensionController(String mapName) { super(mapName); }

	protected final Map<Integer, DimensionData> data = new LinkedHashMap<>();
	protected final Map<Integer, CustomWorldInfo> dimensionInfo = new TreeMap<>();
	protected final Map<Integer, NBTTagCompound> providerInfo = new TreeMap<>();
	protected final Map<Integer, UUID> toBeDeleted = new TreeMap<>();

	@Override
	public IWorldInfo createNewDimension() {
		CustomWorldInfo cwi = new CustomWorldInfo(new NBTTagCompound());
		createNewDimension(null, cwi);
		return cwi;
	}

	@Override
	public void deleteDimension(int dimensionId) { deleteDimension(null, dimensionId); }

	@Override
	public List<Integer> getAllIDs() {
		List<Integer> list = new ArrayList<>();
		for (DimensionType dt : DimensionType.values()) { list.add(dt.getId()); }
		for (int id : dimensionInfo.keySet()) {
			if (!list.contains(id)) { list.add(id); }
		}
		for (int id : toBeDeleted.keySet()) {
			if (!list.contains(id)) { list.add(id); }
		}
		return list;
	}

	@Override
	public IWorldInfo getMCWorldInfo(int dimensionId) { return dimensionInfo.get(dimensionId); }

	@Override
	public INbt getProviderInfo(int id) {
		return new NBTWrapper(providerInfo.containsKey(id) ? providerInfo.get(id) : new NBTTagCompound());
	}

	@Override
	public INbt getNbt() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new NBTWrapper(nbt);
	}

	@Override
	public void setNbt(INbt nbt) { readFromNBT(nbt.getMCNBT()); }

	@Override
	public boolean isDirty() { return update; }

	/**
	 * File path: .../<world name>/data/cnpcs_dimensions.dat
	 * @param nbt - compound tag (name: "data") from file "cnpcs_dimensions.dat"
	 */
	@Override
	public void readFromNBT(@Nonnull NBTTagCompound nbt) {
		NBTTagList nbtList = nbt.getTagList("dimensions", 10);
		if (nbtList.hasNoTags()) { nbtList = nbt.getTagList("dimensionInfo", 10); } // OLD
		for (int i = 0; i < nbtList.tagCount(); i++) {
			NBTTagCompound compound = nbtList.getCompoundTagAt(i);
			dimensionInfo.put(compound.getInteger("dimensionID"),
					new CustomWorldInfo(compound.getCompoundTag("worldInfo")));
			providerInfo.put(compound.getInteger("dimensionID"),
					compound.getCompoundTag("providerInfo"));
		}
		checkExample();
		update = false;
	}

	@Override
	public @Nonnull NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
		NBTTagList nbtList = new NBTTagList();
		for (Entry<Integer, CustomWorldInfo> entry : dimensionInfo.entrySet()) {
			if (!toBeDeleted.containsKey(entry.getKey())) {
				NBTTagCompound compound = new NBTTagCompound();
				compound.setInteger("dimensionID", entry.getKey());
				compound.setTag("worldInfo", entry.getValue().cloneNBTCompound(entry.getValue().getPlayerNBTTagCompound()));
				compound.setTag("providerInfo", providerInfo.containsKey(entry.getKey()) ? providerInfo.get(entry.getKey()) : new NBTTagCompound());
				nbtList.appendTag(compound);
			}
		}
		nbt.setTag("dimensions", nbtList);
		return nbt;
	}

	// example dimension
	private void checkExample() {
		if (!dimensionInfo.containsKey(100)) {
			CustomWorldInfo cwi = new CustomWorldInfo(new NBTTagCompound());
			cwi.setDimensionName("example");
			cwi.setMCLevelName("Example Dimension");
			createNewDimension(null, cwi);
			providerInfo.put(100, new NBTTagCompound());
		}
	}

	public void createNewDimension(EntityPlayerMP player, CustomWorldInfo worldInfo) {
		int id = findFreeDimensionID();
		worldInfo.dimensionId = id;
		if (worldInfo.getDimensionName().startsWith("default_")) { worldInfo.setDimensionName(null); }
		Function<String, Boolean> getFromName = name -> {
			for (CustomWorldInfo customWorldInfo : dimensionInfo.values()) {
				if (customWorldInfo.getMCLevelName().equals(name)) { return true; }
			}
            return false;
        };
		String name = Util.instance.deleteColor(worldInfo.getMCLevelName());
		while (getFromName.apply(name)) { name += "_"; }
		worldInfo.setMCLevelName(name);
		dimensionInfo.put(id, worldInfo);
		providerInfo.put(id, new NBTTagCompound());
		DimensionType dimensionType = getDimensionType(id, worldInfo.getDimensionName());
		if (!DimensionManager.isDimensionRegistered(id)) { DimensionManager.registerDimension(id, dimensionType); }
		worldInfo.setDimensionName(dimensionType.getName());
		if (player != null) {
			player.sendMessage(Component.translatable("message.dimensions.created", worldInfo.getWorldName(), "" + id).getParent());
		}
		syncWithClients();
	}

	public DimensionType getDimensionType(int id, String name) {
		DimensionType dimensionType = null;
		for (DimensionType dt : DimensionType.values()) {
			if (dt.getId() == id) {
				dimensionType = dt;
				break;
			}
		}
		if (id >= 100 && dimensionType == null) {
			DimensionType dimensionTemp = EnumHelper.addEnum(DimensionType.class, "custom_dimension_" + id,
					new Class<?>[] { int.class, String.class, String.class, Class.class }, id,
					name != null && !name.isEmpty() ? NoppesUtilServer.validPath(Util.instance.deleteColor(name)) : "default_" + id,
					"_cnpc",
					CustomWorldProvider.class);
			dimensionType = dimensionTemp != null ? dimensionTemp.setLoadSpawn(false) : DimensionType.OVERWORLD;
		} // register
		return dimensionType;
	}

	public void deleteDimension(ICommandSender sender, int dimID) {
		if (dimID <= 100 || !dimensionInfo.containsKey(dimID)) {
			if (sender != null) {
				if (toBeDeleted.containsKey(dimID)) {
					sender.sendMessage(Component.translatable("message.dimensions.err.del").getParent());
				} else if (!dimensionInfo.containsKey(dimID)) {
					sender.sendMessage(Component.translatable("message.dimensions.err.notmod").getParent());
				}
			}
			return;
		}
		// remove all players
		MinecraftServer server = sender == null ? CustomNpcs.Server : sender.getServer();
		World world = DimensionManager.getWorld(dimID);
		if (world != null && server != null && !world.playerEntities.isEmpty()) {
			WorldServer overworld = server.getWorld(0);
			BlockPos coords = overworld.getSpawnCoordinate();
			if (coords == null) {
				coords = overworld.getSpawnPoint();
				if (!overworld.isAirBlock(coords)) {
					coords = overworld.getTopSolidOrLiquidBlock(coords);
				} else {
					while (overworld.isAirBlock(coords) && coords.getY() > 0) {
						coords = coords.down();
					}
					if (coords.getY() == 0) {
						coords = overworld.getTopSolidOrLiquidBlock(coords);
					}
				}
			}
			List<EntityPlayerMP> players = new ArrayList<>();
			for (EntityPlayer player : world.playerEntities) {
				if (!(player instanceof EntityPlayerMP)) {
					continue;
				}
				player.sendMessage(Component.translatable("message.dimensions.tp.isdelete").getParent());
				players.add((EntityPlayerMP) player);
			}
			for (EntityPlayerMP player : players) {
				SPacketDimensionTeleport.teleportPlayer(player, 0, coords.getX(), coords.getY(), coords.getZ(),
						player.rotationYaw, player.rotationPitch);
			}
		}
		Entity entitySender = null;
		if (sender != null) { entitySender = sender.getCommandSenderEntity(); }
		// remove
		toBeDeleted.put(dimID, entitySender != null ? entitySender.getUniqueID() : null);
		DimensionManager.unloadWorld(dimID);
		if (DimensionManager.isDimensionRegistered(dimID)) { DimensionManager.unregisterDimension(dimID); }
		IDimensionManagerMixin.getUnloadQueue().remove(dimID);
		List<WorldServer> list = new ArrayList<>();
		for (WorldServer w : CustomNpcs.Server.worlds) {
			if (w.provider.getDimension() != dimID) { list.add(w); }
		}
		if (CustomNpcs.Server.worlds.length != list.size()) { CustomNpcs.Server.worlds = list.toArray(new WorldServer[0]); }
		syncWithClients();
	}

	private int findFreeDimensionID() {
		int id = 100;
		while (dimensionInfo.containsKey(id) || toBeDeleted.containsKey(id)) { id++; }
		return id;
	}

	public boolean isDelete(int id) { return toBeDeleted.containsKey(id); }

	private void loadDimension(int dimensionID, CustomWorldInfo worldInfo) {
		WorldServer overworld = (WorldServer) CustomNpcs.Server.getEntityWorld();
		try {
			DimensionManager.getProviderType(dimensionID);
		} catch (Exception e) {
			LogWriter.error("Cannot Hot-load Dim: " + e);
			return;
		}
		MinecraftServer mcServer = overworld.getMinecraftServer();
		ISaveHandler saveHandler = overworld.getSaveHandler();
		assert mcServer != null;
		EnumDifficulty difficulty = mcServer.getEntityWorld().getDifficulty();
		WorldServer world = (WorldServer) (new WorldCustom(worldInfo, mcServer, saveHandler, dimensionID, overworld,
				mcServer.profiler).init());
		world.addEventListener(new ServerWorldEventHandler(mcServer, world));
		LogWriter.debug("Try Load World: " + dimensionID + "; world = " + world);
		try {
			Class.forName("org.orecruncher.dsurround.server.services.AtmosphereService");
		} catch (ClassNotFoundException e) {
			MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(world));
		}
		if (!mcServer.isSinglePlayer()) {
			world.getWorldInfo().setGameType(mcServer.getGameType());
		}
		mcServer.setDifficultyForAllWorlds(difficulty);
	}

	public void loadDimensions() {
		for (Entry<Integer, CustomWorldInfo> entry : dimensionInfo.entrySet()) {
			if (!DimensionManager.isDimensionRegistered(entry.getKey())) {
				DimensionManager.registerDimension(entry.getKey(), getDimensionType(entry.getKey(), entry.getValue().getDimensionName()));
			}
		}
	}

	private void syncWithClients() {
		if (CustomNpcs.Server != null && !CustomNpcs.Server.getPlayerList().getPlayers().isEmpty()) {
			for (EntityPlayerMP player : CustomNpcs.Server.getPlayerList().getPlayers()) {
				SPacketDimensionsGet.sendDimensionIDs(player);
			}
		}
		update = true;
	}

	/**
	 * Only unregister the dimension if it is actually being deleted.
	 * Do NOT unregister on normal world unload (lazy loading).
	 */
	public void unload(World world, int dimensionID) {
		if (toBeDeleted.containsKey(dimensionID)) {
			if (dimensionInfo.containsKey(dimensionID)) {
				DimensionManager.unregisterDimension(dimensionID);
			}
			UUID uniqueID = toBeDeleted.get(dimensionID);
			toBeDeleted.remove(dimensionID);
			dimensionInfo.remove(dimensionID);
			providerInfo.remove(dimensionID);
			((WorldServer) world).flush();
			EntityPlayerMP player = null;
			if (uniqueID != null) { player = CustomNpcs.Server.getPlayerList().getPlayerByUUID(uniqueID); }
			if (Util.instance.removeFile(new File(DimensionManager.getCurrentSaveRootDirectory(), "DIM" + dimensionID)) && player != null) {
				player.sendMessage(Component.translatable("message.dimensions.del.folder", "" + dimensionID).getParent());
			}

			syncWithClients();
		}
		// If not marked for deletion, let the world unload naturally.
		// It will be re-created on next teleport via ensureDimensionLoaded().
	}

	/**
	 * Public accessor for lazy-loading a custom dimension.
	 */
	public void ensureDimensionLoaded(int dimensionID) {
		if (dimensionInfo.containsKey(dimensionID) && DimensionManager.getWorld(dimensionID) == null) {
			loadDimension(dimensionID, dimensionInfo.get(dimensionID));
		}
	}

	@SuppressWarnings("ConstantConditions")
	public void recreateDimension(ICommandSender sender, int dimensionID) {
		List<Integer> allIDs = getAllIDs();
		if (!allIDs.contains(dimensionID) || toBeDeleted.containsKey(dimensionID)) {
			if (sender != null) {
				if (toBeDeleted.containsKey(dimensionID)) { sender.sendMessage(Component.translatable("message.dimensions.err.del").getParent()); }
				else if (!allIDs.contains(dimensionID)) { sender.sendMessage(Component.translatable("message.dimensions.err.not.found").getParent()); }
			}
			return;
		}
		if (sender != null) {
			sender.sendMessage(Component.translatable("message.dimensions.recreate", "" + dimensionID).getParent());
		}
		// Check if dimension exists in controller
		if (!DimensionManager.isDimensionRegistered(dimensionID)) {
			if (sender != null) {
				sender.sendMessage(Component.translatable("message.dimensions.err.restore.failed", "" + dimensionID).getParent());
			}
			return;
		}
		// Determine evacuation target: Nether for Overworld, Overworld for others
		int targetDim = (dimensionID == 0) ? -1 : 0;
		WorldServer world = CustomNpcs.Server.getWorld(targetDim);
		BlockPos pos = BlockPos.ORIGIN.up(60);
		if (world != null) {
			pos = world.getSpawnCoordinate();
			if (pos == null) {
				pos = world.getSpawnPoint();
				if (!world.isAirBlock(pos)) { pos = world.getTopSolidOrLiquidBlock(pos); }
				else {
					while (world.isAirBlock(pos) && pos.getY() > 0) { pos = pos.down(); }
					pos = pos.up();
					if (pos.getY() == 0) { pos = world.getTopSolidOrLiquidBlock(pos); }
				}
			}
		}
		List<EntityPlayerMP> players = new ArrayList<>();
		for (EntityPlayerMP player : CustomNpcs.Server.getPlayerList().getPlayers()) {
			if (player.world.provider.getDimension() == dimensionID) {
				player.sendMessage(Component.translatable("message.dimensions.tp.isdelete").getParent());
				SPacketDimensionTeleport.teleportPlayer(player, targetDim,
						pos.getX(), pos.getY(), pos.getZ(),
						player.rotationYaw, player.rotationPitch);
				players.add(player);
			}
		}
		// files
		File saveRoot = DimensionManager.getCurrentSaveRootDirectory();
		if (dimensionID == 0) {
			Util.instance.removeFile(new File(saveRoot, "region"));
			Util.instance.removeFile(new File(saveRoot, "data"));
			Util.instance.removeFile(new File(saveRoot, "forcedchunks.dat"));
		} // overworld
		else {
			Util.instance.removeFile(new File(saveRoot, "DIM" + dimensionID));
			DimensionManager.unloadWorld(dimensionID);
		}
		toBeDeleted.remove(dimensionID);
		List<WorldServer> list = new ArrayList<>();
		for (WorldServer w : CustomNpcs.Server.worlds) {
			if (w.provider.getDimension() != dimensionID) { list.add(w); }
		}
		// Reinitialize the dimension so it regenerates from scratch
		IDimensionManagerMixin.getUnloadQueue().remove(dimensionID);
		DimensionManager.initDimension(dimensionID);
		list.add(world = DimensionManager.getWorld(dimensionID));
		CustomNpcs.Server.worlds = list.toArray(new WorldServer[0]);
		if (sender != null) {
			sender.sendMessage(Component.translatable("message.dimensions.restored", dimensionID).getParent());
		}
		syncWithClients();
		if (!players.isEmpty() && world != null) {
			pos = world.getSpawnCoordinate();
			if (pos == null) {
				pos = world.getSpawnPoint();
				if (!world.isAirBlock(pos)) { pos = world.getTopSolidOrLiquidBlock(pos); }
				else {
					while (world.isAirBlock(pos) && pos.getY() > 0) { pos = pos.down(); }
					pos = pos.up();
					if (pos.getY() == 0) { pos = world.getTopSolidOrLiquidBlock(pos); }
				}
			}
			BlockPos finalPos = pos;
			CustomNPCsScheduler.runTack(() -> {
				for (EntityPlayerMP player : players) {
					SPacketDimensionTeleport.teleportPlayer(player, dimensionID,
							finalPos.getX(), finalPos.getY(), finalPos.getZ(),
							player.rotationYaw, player.rotationPitch);
					if (player != sender) {
						player.sendMessage(Component.translatable("message.dimensions.restored", dimensionID).getParent());
					}
				}
			}, 2500);
		}

	}

	public void loadData(NBTTagCompound compound) {
		data.clear();
		if (compound != null) {
			for (int i = 0; i < compound.getTagList("Data", 10).tagCount(); i++) {
				NBTTagCompound nbt = compound.getTagList("Data", 10).getCompoundTagAt(i);
				data.put(nbt.getInteger("id"), new DimensionData(nbt));
			}
			if (Minecraft.getMinecraft().currentScreen instanceof IDimensionGetter) {
				((IDimensionGetter) Minecraft.getMinecraft().currentScreen).resetDimension();
			}
		}
	}

	public boolean hasData(int id) { return data.containsKey(id); }

	public @Nullable DimensionData getData(Integer id) { return data.get(id); }

	public List<DimensionData> getDatas() { return new ArrayList<>(data.values()); }

}