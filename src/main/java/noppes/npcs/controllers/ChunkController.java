package noppes.npcs.controllers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.entity.EntityNPCInterface;

public class ChunkController implements ForgeChunkManager.LoadingCallback {

	public static ChunkController instance;
	private final HashMap<Entity, ForgeChunkManager.Ticket> tickets = new HashMap<>();

	public ChunkController() {
		ChunkController.instance = this;
	}

	public void clear() { tickets.clear(); }

	public void unload(EntityNPCInterface npc) {
		ForgeChunkManager.Ticket ticket = tickets.get(npc);
		if (ticket != null) {
			tickets.remove(npc);
			ForgeChunkManager.releaseTicket(ticket);
		}
	}

	public ForgeChunkManager.Ticket getTicket(EntityNPCInterface npc) {
		ForgeChunkManager.Ticket ticket = tickets.get(npc);
		if (ticket != null) { return ticket; }
		if (size() >= CustomNpcs.ChuckLoaders) { return null; }
		ticket = ForgeChunkManager.requestTicket(CustomNpcs.instance, npc.world, ForgeChunkManager.Type.ENTITY);
		if (ticket == null) { return null; }
		ticket.bindEntity(npc);
		ticket.setChunkListDepth(6);
		tickets.put(npc, ticket);
		return null;
	}

	public int size() { return tickets.size(); }

	@Override
	public void ticketsLoaded(List<ForgeChunkManager.Ticket> ticketsIn, World world) {
		for (ForgeChunkManager.Ticket ticket : ticketsIn) {
			if (ticket.getEntity() instanceof EntityNPCInterface) {
				EntityNPCInterface npc = (EntityNPCInterface) ticket.getEntity();
				if (npc.job.getEnumType() == JobType.CHUNK_LOADER) {
					tickets.put(npc, ticket);
					// 3x3
					int x = MathHelper.floor(npc.posX / 16.0D);
					int z = MathHelper.floor(npc.posZ / 16.0D);
					for (int u = -1; u < 2; u++) {
						for (int v = -1; v < 2; v++) { ForgeChunkManager.forceChunk(ticket, new ChunkPos(x + u, z + v)); }
					}
				}
			}
		}
	}

	public void unload(int toRemove) {
		Iterator<Entity> ite = tickets.keySet().iterator();
		int i = 0;
		while (ite.hasNext()) {
			if (i >= toRemove) { return; }
			Entity entity = ite.next();
			ForgeChunkManager.releaseTicket(tickets.get(entity));
			ite.remove();
			++i;
		}
	}

	public boolean hasToNpc(EntityNPCInterface npc) { return npc != null && tickets.containsKey(npc); }

}
