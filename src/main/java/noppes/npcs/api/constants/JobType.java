package noppes.npcs.api.constants;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.*;

public enum JobType {

	NONE("none", 0, false),
	BARD("bard", 1, true),
	HEALER("healer", 2, true),
	GUARD("guard", 3, true),
	ITEM_GIVER("itemgiver", 4, true),
	FOLLOWER("follower", 5, true),
	SPAWNER("spawner", 6, true),
	CONVERSATION("conversation", 7, true),
	CHUNK_LOADER("chunkloader", 8, false),
	PUPPET("puppet", 9, true),
	BUILDER("builder", 10, false),
	FARMER("farmer", 11, true);

	public static JobType get(int id) {
		for (JobType ej : JobType.values()) {
			if (ej.type == id) {
				return ej;
			}
		}
		return JobType.NONE;
	}
	public static Object[] getNames() {
		List<Component> list = new ArrayList<>();
		for (JobType ej : JobType.values()) {
			if (ej != PUPPET) { list.add(ej.name); }
		}
		return list.toArray(new Component[0]);
	}
	private final int type;
	public final Component name;
	public final boolean hasSettings;

	JobType(String named, int t, boolean hasSet) {
		type = t;
		name = Component.translatable("job." + named);
		hasSettings = hasSet;
	}

	public int get() { return type; }

	public void setToNpc(EntityNPCInterface npc) {
		switch (this) {
			case BARD: npc.job = new JobBard(npc); break;
			case HEALER: npc.job = new JobHealer(npc); break;
			case GUARD: npc.job = new JobGuard(npc); break;
			case ITEM_GIVER: npc.job = new JobItemGiver(npc); break;
			case FOLLOWER: npc.job = new JobFollower(npc); break;
			case SPAWNER: npc.job = new JobSpawner(npc); break;
			case CONVERSATION: npc.job = new JobConversation(npc); break;
			case CHUNK_LOADER: npc.job = new JobChunkLoader(npc); break;
			case BUILDER: npc.job = new JobBuilder(npc); break;
			case FARMER: npc.job = new JobFarmer(npc); break;
			default: npc.job = new JobInterface(npc); break;
		}
		if (this == PUPPET) { npc.puppet = new JobPuppet(npc); }
    }

}
