package noppes.npcs.ai.attack;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.AnimationKind;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.world.entity.ai.goal.IGoalSelectorMixin;

import java.util.EnumSet;

public abstract class EntityAICustom extends Goal {

	protected final EntityNPCInterface npc;
	protected final int newGoalRate;
	protected LivingEntity target;

	public boolean hasAttack;
	public boolean startRangedAttack;
	public boolean isRanged;
	public boolean canSeeToAttack;
	public boolean inMove;
	public boolean isFriend;

	protected int burstCount;
	protected int tacticalRange;
	protected int rangedTick;
	protected int meleeTick;
	protected int step;

	public double distance;
	public double range;

	public EntityAICustom(RangedAttackMob npcIn) {
		if (!(npcIn instanceof EntityNPCInterface)) {
			throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
		}
		npc = (EntityNPCInterface) npcIn;
		newGoalRate = ((IGoalSelectorMixin) npc.goalSelector).getNewGoalRate();
		distance = -1.0d;
		setFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	public LivingEntity getTarget() { return target; }

	/**
	 * resets this AI's work when "shouldContinueExecuting" returns "false"
	 */
	@Override
	public void stop() {
		canSeeToAttack = false;
		npc.refreshDimensions();
	}

	/**
	 * checks whether this AI can continue to execute -> updateTask
	 */
	@Override
	public boolean canContinueToUse() { return npc != null && npc.isAlive() && setTarget(); }

	private boolean setTarget() {
		target = npc.getTarget();
		if (npc.aiOwnerNPC != null && npc.aiOwnerNPC.isAlive()) {
			LivingEntity ownerTarget = npc.aiOwnerNPC.getTarget();
			if (ownerTarget != null && ownerTarget.equals(target)) {
				npc.setTarget(ownerTarget);
			}
			target = npc.getTarget();
		}
		if (target == null || !target.isAlive()) {
			startRangedAttack = false;
			return false;
		}
		// target is GM Player reset in EntityNPCInterface.onUpdate()
		isFriend = npc.isFriend(target);
		return target != null;
	}

	/**
	 * checks the possibility of running this AI
	 */
	@Override
	public boolean canUse() {
		distance = -1.0d;
		canSeeToAttack = false;
		hasAttack = false;
		setTarget();
		return setTarget();
	}

	protected void tryMoveToTarget() {
		if (!CustomNpcs.ShowCustomAnimation || !npc.animation.isAnimated(AnimationKind.ATTACKING, AnimationKind.INIT, AnimationKind.INTERACT, AnimationKind.DIES)) {
			double baseSpeed = npc.ais.canSprint ? 1.5d : 1.3d;
			if (target.equals(npc.combatHandler.priorityTarget)) { baseSpeed = npc.ais.canSprint ? 1.6d : 1.4d; }
			double dist = npc.distanceTo(target);
			double speed = (0.75d / (double) npc.stats.aggroRange * dist + 0.5d) * baseSpeed;
			if (speed < 1.3d) { speed = 1.3d; }
			else if (speed > baseSpeed) { speed = baseSpeed; }
			npc.getNavigation().moveTo(target, speed);
		}
	}

	protected void tryToCauseDamage() {
		if (isRanged) {
			if (rangedTick > 0 || distance > range || !canSeeToAttack || npc.stats.ranged.getFireType() == 2) {
				if (rangedTick == 0 && !canSeeToAttack) { rangedTick = 5; }
				startRangedAttack = false;
				return;
			}
			startRangedAttack = true;
			return;
		}
		if (meleeTick > 0 || distance > range || !canSeeToAttack) {
			if (meleeTick == 0 && !canSeeToAttack) { meleeTick = 5; }
			return;
		}
		meleeTick = npc.stats.melee.getDelayRNG();
		npc.swing(InteractionHand.MAIN_HAND);
		npc.doHurtTarget(target);
		attacked();
		hasAttack = true;
	}

	public void update() {
		if (!startRangedAttack || target == null || !target.isAlive() || !npc.isAlive()) {
			startRangedAttack = false;
			//step = 0; burstCount = 0;
			return;
		}
		step++;
		if (step >= newGoalRate) { step = 0; }
		if (rangedTick > step) { return; }

		if (burstCount++ <= npc.stats.ranged.getBurst()) { rangedTick = npc.stats.ranged.getBurstDelay(); }
		else {
			burstCount = 0;
			hasAttack = true;
			rangedTick = npc.stats.ranged.getDelayRNG();
		}
		if (burstCount > 1) {
			boolean indirect = false;
			switch (npc.stats.ranged.getFireType()) {
				case 1: {
					indirect = (distance > range / 2.0);
					break;
				}
				case 2: {
					indirect = !npc.getSensing().hasLineOfSight(target);
					break;
				}
			}
			npc.performRangedAttack(target, indirect ? 1.0f : 0.0f);
			attacked();
			if (npc.currentAnimation != 6) { npc.swing(InteractionHand.MAIN_HAND); }
			step = 0;
		}
	}

	public void attacked() { }

	public boolean damaged() { return false; }

	/**
	 * will run every tick until "canContinueToUse" returns "true"
	 */
	@Override
	public void tick() {
		inMove = !npc.getNavigation().isDone();
		tacticalRange = npc.ais.getTacticalRange();
		distance = Math.sqrt(npc.distanceToSqr(target.getX(), target.getBoundingBox().minY, target.getZ()));
		isRanged = npc.inventory.getProjectile() != null && (npc.stats.ranged.getMeleeRange() <= 0 || distance > npc.stats.ranged.getMeleeRange());
		if (isRanged) {
			rangedTick--;
			range = npc.stats.ranged.getRange();
		}
		else {
			meleeTick--;
			range = npc.stats.melee.getRange();
			double minRange = (npc.getBbWidth() + target.getBbWidth()) / 2.0d;
			if (minRange > range) { range = minRange; }
		}
	}

	public boolean canNewAttack() { return true; }

}
