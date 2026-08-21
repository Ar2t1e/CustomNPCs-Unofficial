package noppes.npcs.ai;

import net.minecraft.entity.ai.EntityAIBase;
import noppes.npcs.entity.EntityNPCInterface;

import javax.annotation.Nonnull;

public class EntityAIJob extends EntityAIBase {

	private final @Nonnull EntityNPCInterface npc;

	public EntityAIJob(@Nonnull EntityNPCInterface npcIn) { npc = npcIn; }

	@Override
	public int getMutexBits() {
		if (npc.job == null) { return super.getMutexBits(); }
		return npc.job.getMutexBits();
	}

	@Override
	public void resetTask() {
		if (npc.job != null) { npc.job.stop(); }
	}

	@Override
	public boolean shouldContinueExecuting() { return !npc.isKilled() && npc.job != null && npc.job.aiContinueExecute(); }

	@Override
	public boolean shouldExecute() { return !npc.isKilled() && npc.job != null && npc.job.aiShouldExecute(); }

	@Override
	public void startExecuting() { npc.job.aiStartExecuting(); }

	@Override
	public void updateTask() {
		if (npc.job != null) { npc.job.aiUpdateTask(); }
	}

}
