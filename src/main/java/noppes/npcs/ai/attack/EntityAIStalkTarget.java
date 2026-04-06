package noppes.npcs.ai.attack;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class EntityAIStalkTarget extends EntityAICustom {

	private LivingEntity oldTarget;

	public EntityAIStalkTarget(RangedAttackMob npcIn) {
		super(npcIn);
		npc.setPose(Pose.STANDING);
	}

	@Override
	public boolean canUse() {
		if (super.canUse()) { return true; }
		if (npc.hasPose(Pose.CROUCHING)) {
			oldTarget = null;
			npc.setPose(Pose.STANDING);
		}
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		if (isFriend || npc.tickCount % (newGoalRate * 2) > newGoalRate) { return; }
		canSeeToAttack = npc.canSee(target);
		if (!npc.hasPose(Pose.CROUCHING) && distance < tacticalRange) { npc.setPose(Pose.CROUCHING); }
		if (canSeeToAttack && distance <= range) {
			if (inMove) { npc.getNavigation().stop(); }
		}
		else { npc.getNavigation().moveTo(target, npc.isCrouching() ? 0.725d : 1.3d); }
		tryToCauseDamage();
		if (!npc.hasPose(Pose.CROUCHING) && hasAttack || npc.getSensing().hasLineOfSight(target)) { npc.setPose(Pose.CROUCHING); }
		if (!target.equals(oldTarget)) {
			oldTarget = target;
			if (npc.hasPose(Pose.CROUCHING)) { npc.setPose(Pose.STANDING); } else { npc.setPose(Pose.CROUCHING); }
		}
	}

}
