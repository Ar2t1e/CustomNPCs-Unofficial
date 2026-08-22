package noppes.npcs.ai.movement;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.Objects;

public class EntityAISprintToTarget extends EntityAIBase {

	private final EntityNPCInterface npc;

    public EntityAISprintToTarget(EntityNPCInterface npcIn) {
		npc = npcIn;
		setMutexBits(AiMutex.PASSIVE);
	}

	@Override
	public void startExecuting() { npc.setSprinting(true); }

	@Override
	public void resetTask() { npc.setSprinting(false); }

	@Override
	public boolean shouldContinueExecuting() {
		return npc.isEntityAlive() && npc.ais.canSprint && npc.onGround && npc.hurtTime <= 0;
	}

	@Override
	public boolean shouldExecute() {
		CustomNpcs.debugData.start(npc);
		EntityLivingBase runTarget = npc.getAttackTarget();
		if (runTarget != null && runTarget.isEntityAlive() && !npc.getNavigator().noPath()) {
			boolean isSprint;
			switch (npc.ais.onAttack) {
				case 0: {
					isSprint = !npc.isInRange(Objects.requireNonNull(npc.getAttackTarget()), (double) npc.stats.aggroRange / 3.0d);
					break;
				} // Attack
				case 2: {
					isSprint = npc.isInRange(Objects.requireNonNull(npc.getAttackTarget()), npc.stats.aggroRange);
					break;
				} // Avoid
				default: isSprint = true; // Panic
			}
			CustomNpcs.debugData.end(npc);
			return isSprint;
		}
		CustomNpcs.debugData.end(npc);
		return false;
	}

}
