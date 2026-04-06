package noppes.npcs.ai.attack;


import net.minecraft.world.entity.monster.RangedAttackMob;
import noppes.npcs.shared.common.util.LogWriter;

public class EntityAIOnslaught extends EntityAICustom {

	public EntityAIOnslaught(RangedAttackMob npc) { super(npc); }

	@Override
	public void tick() {
		super.tick();
		if (!isFriend) {
			canSeeToAttack = npc.canSee(target);
			if (canSeeToAttack && distance <= range) {
				if (inMove) { npc.getNavigation().stop(); }
			}
			else { tryMoveToTarget(); }
			tryToCauseDamage();
		}
	}

}