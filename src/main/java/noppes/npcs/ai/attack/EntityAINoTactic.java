package noppes.npcs.ai.attack;

import net.minecraft.world.entity.monster.RangedAttackMob;

public class EntityAINoTactic extends EntityAICustom {

	public EntityAINoTactic(RangedAttackMob npc) { super(npc); }

	@Override
	public void tick() {
		super.tick();
		if (!isFriend) {
			if (isRanged) {
				canSeeToAttack = npc.canSee(target);
				if (canSeeToAttack && distance <= range) {
					if (inMove) { npc.getNavigation().stop(); }
				}
				else { tryMoveToTarget(); }
			} else {
				canSeeToAttack = npc.canSee(target);
				tryMoveToTarget();
			}
			tryToCauseDamage();
		}
	}

}
