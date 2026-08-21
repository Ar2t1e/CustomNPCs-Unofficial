package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.ForgeChunkManager;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.entity.data.role.IJobChunkLoader;
import noppes.npcs.controllers.ChunkController;
import noppes.npcs.entity.EntityNPCInterface;

public class JobChunkLoader extends JobInterface implements IJobChunkLoader {

	protected List<ChunkPos> chunks = new ArrayList<>();
	protected int ticks = 20;
	protected long playerLastSeen = -1L;

	public JobChunkLoader(EntityNPCInterface npc) {
		super(npc);
		type = JobType.CHUNK_LOADER;
	}

	@Override
	public void load(NBTTagCompound compound) {
		super.load(compound);
		type = JobType.CHUNK_LOADER;
		playerLastSeen = compound.getLong("ChunkPlayerLastSeen");
	}

	@Override
	public NBTTagCompound save(NBTTagCompound compound) {
		super.save(compound);
		compound.setLong("ChunkPlayerLastSeen", playerLastSeen);
		return compound;
	}

	@Override
	public boolean aiContinueExecute() { return false; }

	@Override
	public boolean aiShouldExecute() {
		--ticks;
		if (ticks <= 0 && npc != null) {
			ticks = 20;
			List<EntityPlayer> players = new ArrayList<>();
			try {
				players = npc.world.getEntitiesWithinAABB(EntityPlayer.class, npc.getEntityBoundingBox().grow(48.0, 48.0, 48.0));
			}
			catch (Exception ignored) { }
			if (!players.isEmpty()) { playerLastSeen = System.currentTimeMillis(); }

			if (playerLastSeen > -1L) {
				if (System.currentTimeMillis() > playerLastSeen + 600000L) {
					ChunkController.instance.unload(npc);
					chunks.clear();
					return false;
				}
				ForgeChunkManager.Ticket ticket = ChunkController.instance.getTicket(npc);
				if (ticket != null) {
					List<ChunkPos> list = new ArrayList<>();
					int x = (int) Math.floor(npc.posX / 16.0D);
					int z = (int) Math.floor(npc.posZ / 16.0D);
					// New from Unofficial (BetaZavr) 3x3
					for (int u = -1; u < 2; u++) {
						for (int v = -1; v < 2; v++) {
							list.add(new ChunkPos(x + u, z + v));
						}
					}
					for (ChunkPos chunk : list) {
						if (!chunks.contains(chunk)) { ForgeChunkManager.forceChunk(ticket, chunk); }
					}
					for (ChunkPos chunk : chunks) { ForgeChunkManager.unforceChunk(ticket, chunk); }
					chunks = list;
				}
			}
		}
		return false;
	}

	@Override
	public void reset() {
		if (npc != null && npc.isServerWorld()) {
			ChunkController.instance.unload(npc);
			chunks.clear();
			playerLastSeen = 0L;
		}
	}

	// New from Unofficial (BetaZavr)
	@Override
	public boolean isWorking() { return !chunks.isEmpty() || ChunkController.instance.hasToNpc(npc); }

}
