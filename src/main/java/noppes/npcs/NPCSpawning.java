package noppes.npcs;

import java.util.*;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.controllers.SpawnController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.SpawnData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.world.biome.IBiomeMixin;
import noppes.npcs.shared.common.util.LogWriter;

import javax.annotation.Nonnull;

public class NPCSpawning {

	// Per-world storage to avoid conflicts between dimensions
	private static final Map<World, Set<ChunkPos>> eligibleChunksForSpawning = new WeakHashMap<>();
	private static final Map<World, Boolean> pWGS = new WeakHashMap<>();
	private static final Map<World, Boolean> fCFS = new WeakHashMap<>();

	/**
	 * Called during chunk population from ServerEventsHandler.cnpcPopulateChunk().
	 * Runs synchronously on the server thread — NEVER offload to another thread.
	 */
	public static void performWorldGenSpawning(WorldServer world, int x, int z, Random rand) {
		if (world == null || world.isRemote) { return; }

		// Prevent recursive/re-entrant execution per world
		if (pWGS.getOrDefault(world, false)) { return; }
		pWGS.put(world, true);

		try {
			CustomNpcs.debugData.start(null);

			Biome biome = world.getBiomeForCoordsBody(new BlockPos(x + 8, 0, z + 8));
			SpawnData data = SpawnController.instance.getRandomSpawnData(((IBiomeMixin) biome).getBiomeName());
			if (data == null || data.group <= 0 || rand.nextFloat() > (float) data.itemWeight / 100.0f) {
				return;
			}

			Entity entity = null;
			try {
				entity = EntityList.createEntityFromNBT(data.getCompound(), world);
			} catch (Exception e) {
				LogWriter.error(e);
			}

			if (!(entity instanceof EntityLiving)) {
				return;
			}

			BlockPos spawnPos = world.getTopSolidOrLiquidBlock(new BlockPos(x + rand.nextInt(16), 0, z + rand.nextInt(16)));
			trySummonToPos(3, data, world, spawnPos, (EntityLiving) entity);
		} finally {
			pWGS.put(world, false);
			CustomNpcs.debugData.end(null);
		}
	}

	/**
	 * Called every world tick from ServerTickHandler.onServerWorldTick().
	 * Runs synchronously on the server thread — NEVER offload to another thread.
	 */
	public static void findChunksForSpawning(WorldServer world) {
		if (world == null || world.isRemote) { return; }
		if (SpawnController.instance.data.isEmpty() || world.getWorldInfo().getWorldTotalTime() % 400L != 0L) { return; }

		// Prevent concurrent execution per world
		if (fCFS.getOrDefault(world, false)) { return; }
		fCFS.put(world, true);

		try {
			CustomNpcs.debugData.start(null);

			Set<ChunkPos> eligibleChunks = eligibleChunksForSpawning.computeIfAbsent(world, k -> new HashSet<>());
			eligibleChunks.clear();

			// Copy list to avoid ConcurrentModificationException if players log in/out
			List<EntityPlayer> players = new ArrayList<>(world.playerEntities);
			for (EntityPlayer entityplayer : players) {
				if (entityplayer.isSpectator()) { continue; }

				int chunkX = MathHelper.floor(entityplayer.posX / 16.0);
				int chunkZ = MathHelper.floor(entityplayer.posZ / 16.0);
				byte size = 7;
				for (int x = -size; x <= size; ++x) {
					for (int z = -size; z <= size; ++z) {
						ChunkPos chunkPos = new ChunkPos(x + chunkX, z + chunkZ);
						if (eligibleChunks.contains(chunkPos)) { continue; }
						if (!world.getWorldBorder().contains(chunkPos)) { continue; }

						PlayerChunkMapEntry playerInstance = world.getPlayerChunkMap().getEntry(chunkPos.x, chunkPos.z);
						if (playerInstance != null && playerInstance.isSentToPlayers()) {
							eligibleChunks.add(chunkPos);
						}
					}
				}
			}

			ArrayList<ChunkPos> tmp = new ArrayList<>(eligibleChunks);
			Collections.shuffle(tmp, world.rand);

			for (ChunkPos chunkPos : tmp) {
				BlockPos chunkPosition = getChunk(world, chunkPos.x, chunkPos.z);
				if (chunkPosition == null) { continue; }

				byte range = 6;
				int posX = chunkPosition.getX() + world.rand.nextInt(range) - world.rand.nextInt(range);
				int posZ = chunkPosition.getZ() + world.rand.nextInt(range) - world.rand.nextInt(range);
				BlockPos randomPos = new BlockPos(posX, chunkPosition.getY(), posZ);

				String name = ((IBiomeMixin) world.getBiomeForCoordsBody(randomPos)).getBiomeName();
				SpawnData data = SpawnController.instance.getRandomSpawnData(name);
				if (data == null || data.group <= 0 || world.rand.nextFloat() > (float) data.itemWeight / 100.0f) {
					continue;
				}

				Entity entity = null;
				try {
					entity = EntityList.createEntityFromNBT(data.getCompound(), world);
				} catch (Exception e) {
					LogWriter.error(e);
				}
				if (!(entity instanceof EntityLiving)) { continue; }

				trySummonToPos(1, data, world, randomPos, (EntityLiving) entity);
			}
		} catch (Exception e) {
			LogWriter.error("Error in findChunksForSpawning", e);
		} finally {
			fCFS.put(world, false);
			CustomNpcs.debugData.end(null);
		}
	}

	private static void trySummonToPos(int maxTries, @Nonnull SpawnData data, @Nonnull WorldServer world,
									   @Nonnull BlockPos startPos, @Nonnull EntityLiving entity) {
		// Check global world entity limits before attempting spawn
		int[] sizes = getEntitySizes(world);
		if (entity instanceof EntityNPCInterface) {
			if (sizes[0] > 70) { return; }
		} else if (entity instanceof EntityAnimal) {
			if (sizes[1] > 10) { return; }
		} else if (entity instanceof EntityMob) {
			if (sizes[2] > 70) { return; }
		} else {
			if (sizes[3] > 50) { return; }
		}

		for (int summonTry = 0; summonTry < maxTries; ++summonTry) {
			BlockPos pos = getSpawnLocation(data, entity, world, startPos);
			if (pos == null) { continue; }

			boolean spawned = false;
			for (int i = 0; i < data.group; i++) {
				Entity e;
				try {
					e = EntityList.createEntityFromNBT(data.getCompound(), world);
				} catch (Exception ignored) {
					continue;
				}
				if (!(e instanceof EntityLiving)) { continue; }
				if (checkEntitySize(world, e, pos, data)) {
					spawnData((EntityLiving) e, world, pos);
					spawned = true;
				}
			}
			if (spawned) { break; }
		}
	}

	private static BlockPos getSpawnLocation(SpawnData data, EntityLiving entity, WorldServer world, BlockPos startPos) {
		if (data == null || world == null || startPos == null) { return null; }

		int radius = 5;
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				int checkY = y + startPos.getY();
				if (checkY < 3 || checkY > 250) { continue; }

				for (int z = -radius; z <= radius; z++) {
					BlockPos checkPos = startPos.add(x, y, z);

					// Fast check: is the block's chunk actually loaded
					if (!world.isBlockLoaded(checkPos)) { continue; }

					// Light level check
					int light = world.getLight(checkPos);
					if ((data.type == 1 && light > 8) || (data.type == 2 && light <= 8)) { continue; }

					entity.posX = checkPos.getX() + 0.5d;
					entity.posZ = checkPos.getZ() + 0.5d;
					entity.posY = checkPos.getY();

					boolean isSpawnPos = false;
					BlockPos posDown = checkPos.down();
					BlockPos posUp = checkPos.up((int) Math.floor(entity.getEyeHeight()));
					IBlockState state = world.getBlockState(checkPos);
					IBlockState stateDown = world.getBlockState(posDown);
					IBlockState stateUp = world.getBlockState(posUp);

					// In water
					if (data.liquid) {
						// Fixed operator precedence: || must be grouped with the liquid check
						isSpawnPos = state.getMaterial().isLiquid() &&
								stateDown.getMaterial().isLiquid() &&
								(stateUp.getMaterial().isLiquid() || !stateUp.isNormalCube());
					}
					// In air (flying NPCs)
					else if (entity instanceof EntityNPCInterface && ((EntityNPCInterface) entity).ais.getNavigationType() == 1) {
						isSpawnPos = (!state.isNormalCube() || state.getBlock().isAir(state, world, checkPos)) &&
								(!stateDown.isNormalCube() || stateDown.getBlock().isAir(stateDown, world, posDown)) &&
								(!stateUp.isNormalCube() || stateUp.getBlock().isAir(stateUp, world, posUp));
					}
					// On ground
					else {
						if (stateDown.getBlock() != Blocks.BEDROCK && stateDown.getBlock() != Blocks.BARRIER) {
							isSpawnPos = !state.isNormalCube() && !state.getMaterial().isLiquid() &&
									stateDown.getBlock().canCreatureSpawn(stateDown, world, posDown, EntityLiving.SpawnPlacementType.ON_GROUND) &&
									(!stateUp.isNormalCube() || stateUp.getBlock().isAir(stateUp, world, posUp));
						}
					}

					if (!isSpawnPos) { continue; }

					// Validate player proximity and visibility
					boolean validPos = true;
					List<EntityPlayer> players = new ArrayList<>(world.playerEntities);
					for (EntityPlayer player : players) {
						if (player.isSpectator()) {
							validPos = false;
							break;
						}

						double dist = player.getDistance(entity);
						if (dist > PlayerData.get(player).game.renderDistance + 16.0d) {
							validPos = false;
							break;
						}
						// Too close to player
						if (dist < 12.0d) {
							validPos = false;
							break;
						}
						// Visibility check
						if (data.canSeeSummon != player.canEntityBeSeen(entity)) {
							validPos = false;
							break;
						}
					}

					if (!validPos) { continue; }

					// Check entity density around the spawn point
					List<EntityLiving> list = world.getEntitiesWithinAABB(EntityLiving.class,
							new AxisAlignedBB(-160.0d, -160.0d, -160.0d, 160.0d, 160.0d, 160.0d).offset(checkPos),
							(e) -> e.getDistance(checkPos.getX() + 0.5d, checkPos.getY() + 0.5d, checkPos.getZ() + 0.5d) < 160.0d);

					int count = list.size();
					if (entity instanceof EntityNPCInterface) {
						count = 0;
						for (Entity e : list) {
							if (e instanceof EntityNPCInterface && ((EntityNPCInterface) e).stats.spawnCycle == 4) {
								count++;
							}
						}
					}

					if (count >= data.maxNearPlayer) {
						continue;
					}

					// Forge spawn event check
					@SuppressWarnings("deprecation")
					Event.Result canSpawn = ForgeEventFactory.canEntitySpawn(entity, world,
							(float) entity.posX, (float) entity.posY, (float) entity.posZ);
					if (canSpawn == Event.Result.DENY || (canSpawn == Event.Result.DEFAULT && !entity.getCanSpawnHere())) {
						continue;
					}

					return checkPos;
				}
			}
		}
		return null;
	}

	private static boolean checkEntitySize(WorldServer world, Entity entity, BlockPos pos, @Nonnull SpawnData data) {
		List<? extends Entity> list = world.getEntitiesWithinAABB(entity.getClass(),
				new AxisAlignedBB(-data.range, -data.range, -data.range, data.range, data.range, data.range).offset(pos),
				(e) -> e.getDistance(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d) < data.range);

		int count = list.size();
		if (entity instanceof EntityNPCInterface) {
			count = 0;
			for (Entity e : list) {
				if (e instanceof EntityNPCInterface && ((EntityNPCInterface) e).stats.spawnCycle == 4) {
					count++;
				}
			}
		}
		// Fixed: ensure the new group won't exceed the chunk limit
		return count + data.group <= CustomNpcs.NpcNaturalSpawningChunkLimit;
	}

	private static int[] getEntitySizes(World world) {
		int[] sizes = new int[4]; // npc, animals, mobs, others
		for (Entity e : world.loadedEntityList) {
			if (!e.isEntityAlive()) { continue; }

			// Exclude non-living entities that should not count toward spawn limits
			if (e instanceof EntityPlayer || e instanceof EntityItem || e instanceof EntityXPOrb || e instanceof EntityArrow) {
				continue;
			}

			if (e instanceof EntityNPCInterface) {
				if (((EntityNPCInterface) e).stats.spawnCycle == 4) { sizes[0]++; }
			} else if (e instanceof EntityAnimal) {
				sizes[1]++;
			} else if (e instanceof EntityMob) {
				sizes[2]++;
			} else if (e instanceof EntityLiving) {
				sizes[3]++;
			}
		}
		return sizes;
	}

	@SuppressWarnings("ConstantConditions")
	protected static BlockPos getChunk(World world, int x, int z) {
		Chunk chunk = world.getChunkFromChunkCoords(x, z);
		if (chunk == null) { return null; }

		int posX = x * 16 + world.rand.nextInt(16);
		int posZ = z * 16 + world.rand.nextInt(16);
		int height = chunk.getHeight(new BlockPos(posX, 0, posZ));
		int y = MathHelper.roundUp(height + 1, 16);
		int posY = world.rand.nextInt((y > 0) ? y : (chunk.getTopFilledSegment() + 16 - 1));

		return new BlockPos(posX, posY, posZ);
	}

	private static void spawnData(EntityLiving entity, World world, BlockPos pos) {
		if (entity instanceof EntityNPCInterface) {
			EntityNPCInterface npc = (EntityNPCInterface) entity;
			npc.stats.spawnCycle = 4;
			npc.stats.respawnTime = 0;
			npc.ais.returnToStart = false;
			npc.ais.setStartPos(pos);
		}
		entity.setLocationAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
				world.rand.nextFloat() * 360.0f, 0.0f);
		world.spawnEntity(entity);
	}

}