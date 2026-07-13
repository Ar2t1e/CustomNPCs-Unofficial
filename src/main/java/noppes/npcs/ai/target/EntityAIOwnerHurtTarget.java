package noppes.npcs.ai.target;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIOwnerHurtTarget extends EntityAITarget {

	EntityNPCInterface npc;
	EntityLivingBase theTarget;
	private int timestamp;

	public EntityAIOwnerHurtTarget(EntityNPCInterface npcIn) {
		super(npcIn, false);
		npc = npcIn;
		setMutexBits(AiMutex.PASSIVE);
	}

	@Override
	public boolean shouldExecute() {
		if (npc.isFollower() && npc.role.defendOwner()) {
			EntityLivingBase entity = npc.getOwner();
			if (entity != null) {
				theTarget = entity.getLastAttackedEntity();
				return entity.getLastAttackedEntityTime() != timestamp && isSuitableTarget(theTarget, false);
			}
		}
		return false;
	}

	@Override
	public void startExecuting() {
		npc.setAttackTarget(theTarget);
		EntityLivingBase entitylivingbase = npc.getOwner();
		if (entitylivingbase != null) { timestamp = entitylivingbase.getLastAttackedEntityTime(); }
		super.startExecuting();
	}

}
