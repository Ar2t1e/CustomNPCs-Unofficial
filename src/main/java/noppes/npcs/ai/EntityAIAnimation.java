package noppes.npcs.ai;

import net.minecraft.block.BlockStairs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.AnimationKind;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.client.model.animation.AnimationConfig;
import noppes.npcs.client.model.animation.AnimationFrameConfig;
import noppes.npcs.constants.EnumAnimationStages;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.CustomNPCsScheduler;

public class EntityAIAnimation extends EntityAIBase {

	private boolean hasPath = false;
	private boolean isAtStartPoint = false;
	private boolean isAttacking = false;
	private boolean removed = false;
	private final EntityNPCInterface npc;
	public int temp = 0;

	public EntityAIAnimation(EntityNPCInterface npcIn) { npc = npcIn; }

	@Override
	public boolean shouldExecute() {
		removed = !npc.isEntityAlive();
		if (removed) { return npc.currentAnimation != AnimationType.SLEEP.get(); }
		if (npc.stats.ranged.getHasAimAnimation() && npc.isAttacking()) { return npc.currentAnimation != AnimationType.AIM.get(); }
		hasPath = !npc.getNavigator().noPath();
		isAttacking = npc.isAttacking();
		isAtStartPoint = npc.ais.shouldReturnHome() && npc.isVeryNearAssignedPlace();
		if (temp != AnimationType.NORMAL.get()) {
			if (!hasNavigation()) { return npc.currentAnimation != temp; }
			temp = AnimationType.NORMAL.get();
		}
		if (hasNavigation() && notWalkingAnimation(npc.currentAnimation)) { return npc.currentAnimation != AnimationType.NORMAL.get(); }
		return npc.currentAnimation != npc.ais.animationType;
	}

	@Override
	public void updateTask() {
		if (npc.stats.ranged.getHasAimAnimation() && npc.isAttacking()) { setAnimation(AnimationType.AIM.get()); }
		else {
			int type = npc.ais.animationType;
			if (removed) { type = AnimationType.SLEEP.get(); }
			else if (notWalkingAnimation(npc.ais.animationType) && hasNavigation()) { type = AnimationType.NORMAL.get(); }
			else if (temp != AnimationType.NORMAL.get()) {
				if (hasNavigation()) { temp = AnimationType.NORMAL.get(); }
				else { type = temp;}
			}
			setAnimation(type);
		}
	}

	public static int getWalkingAnimationGuiIndex(int animation) {
		switch (animation) {
			case 3: return AnimationType.DANCE.get();
			case 4: return AnimationType.SIT.get();
			case 5: return AnimationType.HUG.get();
			case 6: return AnimationType.SLEEP.get();
			case 7: return AnimationType.SNEAK.get();
			default: return AnimationType.NORMAL.get();
		}
	}

	public static boolean notWalkingAnimation(int animation) { return getWalkingAnimationGuiIndex(animation) == AnimationType.NORMAL.get(); }

	private void setAnimation(int animation) {
		npc.setCurrentAnimation(animation);
		npc.updateHitbox();
		npc.setPosition(npc.posX, npc.posY, npc.posZ);
	}

	private boolean hasNavigation() { return isAttacking || npc.ais.shouldReturnHome() && !isAtStartPoint && !npc.isFollower() || hasPath; }

	// New from Unofficial (BetaZavr)
	public boolean playAttackEntityCustomAnimation(Entity target) {
		AnimationConfig anim = npc.animation.tryRunAnimation(AnimationKind.ATTACKING);
		if (anim != null) {
			for (int i = 0; i < anim.frames.size(); i++) {
				AnimationFrameConfig frame = anim.frames.get(i);
				if (frame.isNowDamage() && frame.damageDelay != 0) {
					CustomNPCsScheduler.runTack(() -> npc.tryAttackEntityAsMob(target, frame.id), frame.damageDelay * 50L);
					return false;
				}
			}
		}
		return npc.tryAttackEntityAsMob(target, 0);
	}

	public void playHitCustomAnimation() { npc.animation.tryRunAnimation(AnimationKind.HIT); }

	public void playBlockedCustomAnimation() { npc.animation.tryRunAnimation(AnimationKind.BLOCKED); }

	public void playDeathCustomAnimation() {
		AnimationConfig anim = npc.animation.tryRunAnimation(AnimationKind.DIES);
		if (anim != null) {
			npc.motionX = 0.0d;
			npc.motionY = 0.0d;
			npc.motionZ = 0.0d;
		}
	}

	public void playShootCustomAnimation() {
		npc.animation.tryRunAnimation(AnimationKind.SHOOT);
		if (npc.animation.isAnimated(AnimationKind.AIM)) { npc.animation.stopAnimation(); }
	}

	public void playInteractCustomAnimation() {
		AnimationConfig anim = npc.animation.tryRunAnimation(AnimationKind.INTERACT);
		if (anim != null ) {
			npc.lookAi.fastRotation = true;
			CustomNPCsScheduler.runTack(() -> npc.lookAi.fastRotation = false , anim.totalTicks * 50L);
		}
	}

	public void playInitCustomAnimation() { npc.animation.tryRunAnimation(AnimationKind.INIT); }

	public void livingUpdate() {
		if (CustomNpcs.ShowCustomAnimation && !npc.isKilled()) {
			CustomNPCsScheduler.runTack(() -> {
				AnimationConfig anim = null;
				// Jump
				if (!npc.animation.getJump() && !npc.isKilled() &&
						npc.getHealth() > 0.0f && npc.world != null &&
						!(npc.isInWater() || npc.isInLava()) && npc.ais.getNavigationType() == 0 &&
						!npc.onGround && npc.motionY > 0.0d) {
					BlockPos posUnderfoot = npc.getPosition().down();
					BlockPos posAhead = npc.getPosition().add(npc.motionX, 0, npc.motionZ).down();
					boolean canJumpHere = !(npc.world.getBlockState(posUnderfoot).getBlock() instanceof BlockStairs);
					boolean canLandThere = !(npc.world.getBlockState(posAhead).getBlock() instanceof BlockStairs);
					if (canJumpHere && canLandThere) {
						npc.animation.setJump(true);
						anim = npc.animation.tryRunAnimation(AnimationKind.JUMP);
					}
				}
				else if (npc.animation.getJump() && npc.onGround && npc.animation.getAnimationStage() != EnumAnimationStages.Started) {
					npc.animation.setJump(false);
					if (npc.animation.isAnimated(AnimationKind.JUMP)) { npc.animation.stopAnimation(); }
				}
				// Swing
				if (anim == null && !npc.animation.getSwing() && npc.swingProgress > 0.0f) {
					npc.animation.setSwing(true);
					if (!npc.animation.isAnimated(AnimationKind.ATTACKING, AnimationKind.AIM, AnimationKind.SHOOT)) {
						anim = npc.animation.tryRunAnimation(AnimationKind.SWING);
						if (anim != null) {
							npc.swingProgress = 0.0f;
							npc.swingProgressInt = 0;
							npc.prevSwingProgress = 0.0f;
							npc.isSwingInProgress = false;
						}
					}
				}
				else if (npc.animation.getSwing() && npc.swingProgress == 0.0f) {
					npc.animation.setSwing(false);
				}
				// walking or standing
				npc.animation.resetWalkAndStandAnimations();
			});
		}
	}

}
