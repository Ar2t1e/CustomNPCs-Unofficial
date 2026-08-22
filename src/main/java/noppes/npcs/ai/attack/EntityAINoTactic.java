package noppes.npcs.ai.attack;

import net.minecraft.entity.IRangedAttackMob;

public class EntityAINoTactic extends EntityAICustom {

	public EntityAINoTactic(IRangedAttackMob npc) { super(npc); }

	@Override
	public void updateTask() {
		super.updateTask();
		if (isFriend || npc.ticksExisted % (tickRate * 2) != 0) { return; }
		if (isRanged) {
			canSeeToAttack = npc.canSee(target);
			if (canSeeToAttack && distance <= range) {
				if (inMove) {npc.getNavigator().clearPath(); }
			}
			else { tryMoveToTarget(); }
		} else {
			canSeeToAttack = npc.canSee(target);
			tryMoveToTarget();
		}
		this.tryToCauseDamage();
	}

}
