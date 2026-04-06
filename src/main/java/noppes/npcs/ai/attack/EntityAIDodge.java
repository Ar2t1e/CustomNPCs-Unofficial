package noppes.npcs.ai.attack;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.Vec3d;
import noppes.npcs.util.Util;

public class EntityAIDodge extends EntityAICustom {

	protected long delay = 1000L;
	protected long lastJump = System.currentTimeMillis() + delay;
	protected final float leapSpeed = 1.3F;

	public EntityAIDodge(IRangedAttackMob npc) { super(npc); }

	@Override
	public void updateTask() {
		super.updateTask();
		if (isFriend || npc.ticksExisted % (tickRate * 2) > tickRate) { return; }
		canSeeToAttack = npc.canSee(target);
		if (!(canSeeToAttack && distance <= range) && !inMove) { tryMoveToTarget(); }
		tryToCauseDamage();
	}

	@Override
	public void attacked() { tryJump(); }

	@Override
	public boolean damaged() {
		if (tryJump()) { return npc.getRNG().nextFloat() < 0.05f; }
		return false;
	}

	protected boolean tryJump() {
		if (lastJump < System.currentTimeMillis()) {
			double dist = 0.0d;
			int error = 0;
			int attempts = 0;
			Vec3d vec = null;
			while ((dist < tacticalRange || dist < (isRanged ? range / 2.0d : range)
					|| dist > npc.stats.aggroRange) && error < 3 && attempts < 8) {
				attempts++;
				Vec3d vec2 = RandomPositionGenerator.findRandomTarget(npc, tacticalRange, 2);
				if (vec2 == null) {
					error++;
					continue;
				}
				error = 0;
				dist = npc.getDistance(vec2.x, vec2.y, vec2.z);
				if ((int) vec2.x == npc.getPosition().getX() && (int) vec2.y == npc.getPosition().getY()
						&& (int) vec2.x == npc.getPosition().getZ()) { dist = 0.0d; }
				else {
					Path path = npc.getNavigator().getPathToXYZ(vec2.x, vec2.y, vec2.z);
					if (path == null) { dist = 0.0d; }
					else {
						PathPoint point = path.getFinalPathPoint();
						if (point == null || (npc.getPosition().getX() == point.x &&
								npc.getPosition().getY() == point.y &&
								npc.getPosition().getZ() == point.z)) { dist = 0.0d; }
						else { vec = new Vec3d(point.x, point.y, point.z); }
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