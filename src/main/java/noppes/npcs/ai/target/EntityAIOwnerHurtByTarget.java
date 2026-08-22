package noppes.npcs.ai.target;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIOwnerHurtByTarget extends EntityAITarget {
	EntityNPCInterface npc;
	EntityLivingBase theOwnerAttacker;
	private int timer;

	public EntityAIOwnerHurtByTarget(EntityNPCInterface npcIn) {
		super(npcIn, false);
		npc = npcIn;
		setMutexBits(AiMutex.PASSIVE);
	}

	@Override
	public boolean shouldExecute() {
		if (npc.isFollower() && npc.role.defendOwner()) {
			EntityLivingBase entity = npc.getOwner();
			if (entity != null) {
				theOwnerAttacker = entity.getRevengeTarget();
				return entity.getRevengeTimer() != timer && isSuitableTarget(theOwnerAttacker, false);
			}
		}
		return false;
	}

	@Override
	public void startExecuting() {
		taskOwner.setAttackTarget(theOwnerAttacker);
		EntityLivingBase entitylivingbase = npc.getOwner();
		if (entitylivingbase != null) { timer = entitylivingbase.getRevengeTimer(); }
		super.startExecuting();
	}
}
