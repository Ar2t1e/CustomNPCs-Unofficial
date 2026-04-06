package noppes.npcs.ai.attack;

import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.util.Util;

public class EntityAIDodge extends EntityAICustom {

	protected long delay = 1000L;
	protected long lastJump = System.currentTimeMillis() + delay;
	protected final float leapSpeed = 1.3F;

	public EntityAIDodge(RangedAttackMob npc) { super(npc); }

	@Override
	public void tick() {
		super.tick();
		if (isFriend || npc.tickCount % (newGoalRate * 2) > newGoalRate) { return; }
		canSeeToAttack = npc.canSee(target);
		if (!(canSeeToAttack && distance <= range) && !inMove) { tryMoveToTarget(); }
		tryToCauseDamage();
	}

	@Override
	public void attacked() { tryJump(); }

	@Override
	public boolean damaged() {
		if (tryJump()) { return npc.getRandom().nextFloat() < 0.05f; }
		return false;
	}

	protected boolean tryJump() {
		if (lastJump < System.currentTimeMillis()) {
			double dist = 0.0d;
			int error = 0;
			int attempts = 0;
			Vec3 vec = null;
			while ((dist < tacticalRange || dist < (isRanged ? range / 2.0d : range)
					|| dist > npc.stats.aggroRange) && error < 3 && attempts < 8) {
				attempts++;
				Vec3 vec2 = DefaultRandomPos.getPos(npc, tacticalRange, 2);
				if (vec2 == null) {
					error++;
					continue;
				}
				error = 0;
				dist = Math.sqrt(npc.distanceToSqr(vec2));
				if ((int) vec2.x == npc.blockPosition().getX() && (int) vec2.y == npc.blockPosition().getY()
						&& (int) vec2.x == npc.blockPosition().getZ()) { dist = 0.0d; }
				else {
					Path path = npc.getNavigation().createPath(vec2.x, vec2.y, vec2.z, 1);
					if (path == null) { dist = 0.0d; }
					else {
						Node node = path.getEndNode();
						if (node == null || (npc.blockPosition().getX() == node.x &&
								npc.blockPosition().getY() == node.y &&
								npc.blockPosition().getZ() == node.z)) { dist = 0.0d; }
						else { vec = new Vec3(node.x, node.y, node.z); }
					}
				}
			}
			if (vec != null) {
				Util.instance.jumpTowards(leapSpeed, npc, vec);
				lastJump = System.currentTimeMillis() + delay;
				return true;
			}
		}
		return false;
	}

}