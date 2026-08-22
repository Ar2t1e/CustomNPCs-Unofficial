package noppes.npcs.ai;

import net.minecraft.entity.ai.EntityAIBase;
import noppes.npcs.entity.EntityNPCInterface;

import javax.annotation.Nonnull;

public class EntityAIRole extends EntityAIBase {

	private final @Nonnull EntityNPCInterface npc;

	public EntityAIRole(@Nonnull EntityNPCInterface npcIn) { npc = npcIn; }

	@Override
	public boolean shouldContinueExecuting() { return !npc.isKilled() && npc.role != null && npc.role.aiContinueExecute(); }

	@Override
	public boolean shouldExecute() { return !npc.isKilled() && npc.role != null && npc.role.aiShouldExecute(); }

	@Override
	public void startExecuting() { npc.role.aiStartExecuting(); }

	@Override
	public void updateTask() { if (npc.role != null) { npc.role.aiUpdateTask(); } }

}
