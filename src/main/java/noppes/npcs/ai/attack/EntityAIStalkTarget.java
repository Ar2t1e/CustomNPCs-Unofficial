package noppes.npcs.ai.attack;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;

public class EntityAIStalkTarget extends EntityAICustom {

	private EntityLivingBase oldTarget;

	public EntityAIStalkTarget(IRangedAttackMob npcIn) {
		super(npcIn);
		npc.setPose(1, false);
	}

	@Override
	public boolean shouldExecute() {
		if (super.shouldExecute()) {
			return true;
		}
		if (npc.hasPose(1)) {
			oldTarget = null;
			npc.setPose(1, false);
		}
		return false;
	}

	@Override
	public void updateTask() {
		super.updateTask();
		if (isFriend || npc.ticksExisted % (tickRate * 2) != 0) { return; }
		canSeeToAttack = npc.canSee(target);
		if (!npc.hasPose(1) && distance < tacticalRange) { npc.setPose(1, true); }
		if (canSeeToAttack && distance <= range) {
			if (inMove) { npc.getNavigator().clearPath(); }
		}
		else { npc.getNavigator().tryMoveToEntityLiving(target, npc.hasPose(1) ? 0.725d : 1.3d); }
		tryToCauseDamage();
		if (!npc.hasPose(1) && hasAttack || target.canEntityBeSeen(npc)) { npc.setPose(1, true); }
		if (!target.equals(oldTarget)) {
			oldTarget = target;
			npc.setPose(1, npc.hasPose(1));
		}
	}

}
