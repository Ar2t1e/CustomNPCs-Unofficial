package noppes.npcs.ai;

import net.minecraft.entity.ai.EntityAIBase;
import noppes.npcs.entity.EntityNPCInterface;

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
		if (removed) { return npc.currentAnimation != 2; }
		if (npc.stats.ranged.getHasAimAnimation() && npc.isAttacking()) { return npc.currentAnimation != 6; }
		hasPath = !npc.getNavigator().noPath();
		isAttacking = npc.isAttacking();
		isAtStartPoint = npc.ais.shouldReturnHome() && npc.isVeryNearAssignedPlace();
		if (temp != 0) {
			if (!hasNavigation()) { return npc.currentAnimation != temp; }
			temp = 0;
		}
		if (hasNavigation() && notWalkingAnimation(npc.currentAnimation)) { return npc.currentAnimation != 0; }
		return npc.currentAnimation != npc.ais.animationType;
	}

	@Override
	public void updateTask() {
		if (npc.stats.ranged.getHasAimAnimation() && npc.isAttacking()) { setAnimation(6); }
		else {
			int type = npc.ais.animationType;
			if (removed) { type = 2; }
			else if (notWalkingAnimation(npc.ais.animationType) && hasNavigation()) { type = 0; }
			else if (temp != 0) {
				if (hasNavigation()) { temp = 0; }
				else { type = temp;}
			}
			// if (this.npc.stats.ranged.getHasAimAnimation() && this.npc.isAttacking()) { type = 6; } // <- AI target
			setAnimation(type);
		}
	}

	public static int getWalkingAnimationGuiIndex(int animation) {
		switch (animation) {
			case 3: return 5;
			case 4: return 1;
			case 5: return 3;
			case 6: return 2;
			case 7: return 4;
			default: return 0;
		}
	}

	public static boolean notWalkingAnimation(int animation) { return getWalkingAnimationGuiIndex(animation) == 0; }

	private void setAnimation(int animation) {
		npc.setCurrentAnimation(animation);
		npc.updateHitbox();
		npc.setPosition(npc.posX, npc.posY, npc.posZ);
	}

	private boolean hasNavigation() { return isAttacking || npc.ais.shouldReturnHome() && !isAtStartPoint && !npc.isFollower() || hasPath; }

}
