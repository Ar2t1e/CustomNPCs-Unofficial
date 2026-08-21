package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.entity.data.role.IJobConversation;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.entity.EntityNPCInterface;

public class JobConversation extends JobInterface implements IJobConversation {


	public Availability availability = new Availability();
	private final ArrayList<String> names = new ArrayList<>();
	private final HashMap<String, EntityNPCInterface> npcs = new HashMap<>();
	public TreeMap<Integer, ConversationLine> lines = new TreeMap<>();
	public int quest = -1;
	public int generalDelay = 400;
	public int ticks = 100;
	public int range = 20;
	private JobConversation.ConversationLine nextLine;
	private boolean hasStarted = false;
	private int startedTicks = 20;
	public boolean mode = false;

	public JobConversation(EntityNPCInterface npc) {
		super(npc);
		type = JobType.CONVERSATION;
	}

	@Override
	public void load(NBTTagCompound compound) {
		super.load(compound);
		type = JobType.CONVERSATION;
		availability.load(compound.getCompoundTag("ConversationAvailability"));
		quest = compound.getInteger("ConversationQuest");
		generalDelay = compound.getInteger("ConversationDelay");
		range = compound.getInteger("ConversationRange");
		if (compound.hasKey("ConversationMode", 3)) {
			mode = compound.getInteger("ConversationMode") != 0;
		} // OLD
		else { mode = compound.getBoolean("ConversationMode"); }

		NBTTagList tagList = compound.getTagList("ConversationLines", 10);
		names.clear();
		lines.clear();
		for (int i = 0; i < tagList.tagCount(); ++i) {
			NBTTagCompound nbt = tagList.getCompoundTagAt(i);
			ConversationLine line = new ConversationLine();
			line.readEntityFromNBT(nbt);
			if (!line.npc.isEmpty() && !names.contains(line.npc.toLowerCase())) { names.add(line.npc.toLowerCase()); }
			lines.put(nbt.getInteger("Slot"), line);
		}
		ticks = generalDelay;
	}

	@Override
	public NBTTagCompound save(NBTTagCompound compound) {
		super.save(compound);
		compound.setTag("ConversationAvailability", availability.save(new NBTTagCompound()));
		compound.setInteger("ConversationQuest", quest);
		compound.setInteger("ConversationDelay", generalDelay);
		compound.setInteger("ConversationRange", range);
		compound.setBoolean("ConversationMode", mode);
		NBTTagList nbttaglist = new NBTTagList();
		for (int slot : lines.keySet()) {
			ConversationLine line = lines.get(slot);
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setInteger("Slot", slot);
			line.writeEntityToNBT(nbttagcompound);
			nbttaglist.appendTag(nbttagcompound);
		}
		compound.setTag("ConversationLines", nbttaglist);
		if (hasQuest()) { compound.setString("ConversationQuestTitle", getQuest().title); }
		return compound;
	}

	@Override
	public void aiUpdateTask() {
		--ticks;
		if (ticks <= 0 && nextLine != null) {
			say(nextLine);
			boolean seenNext = false;
			JobConversation.ConversationLine compare = nextLine;
			nextLine = null;
			for (ConversationLine line : lines.values()) {
				if (!line.isEmpty()) {
					if (seenNext) {
						nextLine = line;
						break;
					}
					if (line == compare) { seenNext = true; }
				}
			}
			if (nextLine != null) { ticks = nextLine.delay; }
			else if (hasQuest() && npc != null) {
				List<EntityPlayer> inRange = new ArrayList<>();
				try {
					inRange = npc.world.getEntitiesWithinAABB(EntityPlayer.class,
							npc.getEntityBoundingBox().grow(range, range, range));
				}
				catch (Exception ignored) { }
				for (EntityPlayer player : inRange) {
					if (availability.isAvailable(player)) { PlayerQuestController.addActiveQuest(getQuest(), player, false); }
				}
			}
		}
	}

	@Override
	public boolean aiShouldExecute() {
		if (!lines.isEmpty() && npc != null && !npc.isKilled() && !npc.isAttacking() && shouldRun()) {
			if (!hasStarted && mode) {
				if (startedTicks-- > 0) { return false; }
				startedTicks = 10;
				try {
					if (npc.world.getEntitiesWithinAABB(EntityPlayer.class, npc.getEntityBoundingBox().grow(range, range, range)).isEmpty()) { return false; }
				}
				catch (Exception ignored) { }
			}
			for (ConversationLine line : lines.values()) {
				if (line != null && !line.isEmpty()) {
					nextLine = line;
					break;
				}
			}
			return nextLine != null;
		}
		return false;
	}

	@Override
	public boolean aiContinueExecute() {
		for (EntityNPCInterface npc : new ArrayList<>(npcs.values())) {
			if (npc.isKilled() || npc.isAttacking()) { return false; }
		}
		return nextLine != null;
	}

	@Override
	public void stop() {
		nextLine = null;
		ticks = generalDelay;
		hasStarted = false;
	}

	@Override
	public void aiStartExecuting() {
		startedTicks = 20;
		hasStarted = true;
	}

	@Override
	public void killed() { reset(); }

	@Override
	public void reset() {
		hasStarted = false;
		stop();
		ticks = 60;
	}

	private boolean shouldRun() {
		--ticks;
		if (ticks <= 0 && npc != null) {
			npcs.clear();
			List<EntityNPCInterface> list = new ArrayList<>();
			try {
				list = npc.world.getEntitiesWithinAABB(EntityNPCInterface.class,
						npc.getEntityBoundingBox().grow(10.0, 10.0, 10.0));
			}
			catch (Exception ignored) { }
			for (EntityNPCInterface npc : list) {
				String name = npc.getName().toLowerCase();
				if (!npc.isKilled() && !npc.isAttacking() && names.contains(name)) { npcs.put(name, npc); }
			}
			boolean bo = names.size() == npcs.size();
			if (!bo) { ticks = 20; }
			return bo;
		}
		return false;
	}

	public boolean hasQuest() { return getQuest() != null; }

	public Quest getQuest() { return npc == null || !npc.isServerWorld() ? null : QuestController.instance.quests.get(quest); }

	private void say(ConversationLine line) {
		if (npc != null) {
			List<EntityPlayer> inRange = new ArrayList<>();
			try {
				inRange = npc.world.getEntitiesWithinAABB(EntityPlayer.class,
						npc.getEntityBoundingBox().grow(range, range, range));
			}
			catch (Exception ignored) { }
			EntityNPCInterface npcIn = npcs.get(line.npc.toLowerCase());
			if (npcIn != null) {
				for (EntityPlayer player : inRange) {
					if (availability.isAvailable(player)) { npcIn.say(player, line); }
				}
			}
		}
	}

	public ConversationLine getLine(int slot) {
		if (lines.containsKey(slot)) { return lines.get(slot); }
		JobConversation.ConversationLine line = new ConversationLine();
		lines.put(slot, line);
		return line;
	}

	public static class ConversationLine extends Line {
		public int delay;
		public String npc;

		public ConversationLine() {
			npc = "";
			delay = 40;
		}

		public boolean isEmpty() { return npc.isEmpty() || text.isEmpty(); }

		public void readEntityFromNBT(NBTTagCompound compound) {
			text = compound.getString("Line");
			npc = compound.getString("Npc");
			sound = compound.getString("Sound");
			delay = compound.getInteger("Delay");
		}

		public void writeEntityToNBT(NBTTagCompound compound) {
			compound.setString("Line", text);
			compound.setString("Npc", npc);
			compound.setString("Sound", sound);
			compound.setInteger("Delay", delay);
		}
	}

	// New from Unofficial (BetaZavr)
	@Override
	public boolean isWorking() { return hasStarted; }

}
