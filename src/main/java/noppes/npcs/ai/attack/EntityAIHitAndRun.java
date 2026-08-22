package noppes.npcs.ai.attack;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.Vec3d;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.util.IRayTraceVec;
import noppes.npcs.util.Util;

import java.util.Objects;

public class EntityAIHitAndRun extends EntityAICustom {

	private int[] runPos;
	protected EntityLivingBase targetIn;
	protected long startTime = 0L;

	public EntityAIHitAndRun(IRangedAttackMob npc) { super(npc); }

	@Override
	public void updateTask() {
		super.updateTask();
		if (isFriend || npc.ticksExisted % (tickRate * 2) != 0) { return; }
		canSeeToAttack = npc.canSee(target);
		if (canSeeToAttack && distance <= range) {
			if (inMove) {
				if (runPos == null) { clearTarget(); }
				else {
					PathPoint point = Objects.requireNonNull(npc.getNavigator().getPath()).getFinalPathPoint();
					if (point == null || point.x < runPos[0] - 2 && point.x > runPos[0] + 2
							|| point.y < runPos[1] - 2 && point.y > runPos[1] + 2
							|| point.z < runPos[2] - 2 && point.z > runPos[2] + 2) {
						clearTarget();
					}
				}
			}
		} else {
			if (!inMove) {
				clearTarget();
				tryMoveToTarget();
			}
		}
		tryToCauseDamage();
		if (hasAttack) {
			Vec3d vec = null;
			IRayTraceVec pos;
			runPos = null;
			if (!isRanged || distance < tacticalRange) {
				pos = Util.instance.getPosition(target.posX, target.posY, target.posZ, target.rotationYaw + 180.0f, target.rotationPitch, tacticalRange);
				Path path = npc.getNavigator().getPathToXYZ(pos.getX(), pos.getY(), pos.getZ());
				if (path == null) { vec = RandomPositionGenerator.findRandomTarget(npc, tacticalRange, 2); }
			}
			if (vec == null) {
				double dist = 0.0d;
				int error = 0, attempts = 0;
				while ((dist < tacticalRange || dist < (isRanged ? range / 2.0d : range) || dist > npc.stats.aggroRange) && error < 3 && attempts < 8) {
					attempts++;
					Vec3d vec2 = RandomPositionGenerator.findRandomTarget(npc, tacticalRange, 2);
					if (vec2 == null) {
						error++;
						continue;
					}
					error = 0;
					dist = npc.getDistance(vec2.x, vec2.y, vec2.z);
					if (npc.stats.calmdown) {
						double homeDist = Util.instance.distanceTo(npc.getStartXPos(), npc.getStartYPos(), npc.getStartZPos(), vec2.x, vec2.y, vec2.z);
						if (homeDist > CustomNpcs.NpcNavRange) { continue; }
					}
					if ((int) vec2.x == npc.getPosition().getX() && (int) vec2.y == npc.getPosition().getY() && (int) vec2.x == npc.getPosition().getZ()) { dist = 0.0d; }
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
			}
			if (vec != null) {
				npc.getNavigator().tryMoveToXYZ(vec.x, vec.y, vec.z, 1.3d);
				runPos = new int[] { (int) Math.floor(vec.x), (int) Math.floor(vec.y), (int) Math.floor(vec.z) };
				targetIn = target;
				startTime = System.currentTimeMillis();
				npc.setAttackTarget(null);
			}
		}
	}

	private void clearTarget() {
		runPos = null;
		npc.setAttackTarget(targetIn);
		targetIn = null;
		startTime = 0L;
		npc.getNavigator().clearPath();
	}

	@Override
	public boolean canNewAttack() { return runPos == null; }

}