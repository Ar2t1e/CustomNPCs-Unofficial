package noppes.npcs.ai.attack;

import net.minecraft.entity.IRangedAttackMob;
import noppes.npcs.util.Util;

public class EntityAIOnslaught extends EntityAICustom {

	public EntityAIOnslaught(IRangedAttackMob npc) {super(npc); }

	@Override
	public void updateTask() {
		super.updateTask();
		if (isFriend || npc.ticksExisted % (tickRate * 2) != 0) { return; }
		canSeeToAttack = npc.canSee(target);
		if (canSeeToAttack && distance <= range) {
			if (inMove) { npc.getNavigator().clearPath(); }
		}
		else { tryMoveToTarget(); }
		tryToCauseDamage();
	}

}