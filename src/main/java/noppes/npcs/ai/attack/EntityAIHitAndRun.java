package noppes.npcs.ai.attack;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.util.IRayTraceVec;
import noppes.npcs.util.Util;

import java.util.Objects;

public class EntityAIHitAndRun extends EntityAICustom {

	private int[] runPos;
	protected LivingEntity targetIn;
	protected long startTime = 0L;

	public EntityAIHitAndRun(RangedAttackMob npc) { super(npc); }

	@Override
	public void tick() {
		super.tick();
		if (isFriend || npc.tickCount % (newGoalRate * 2) > newGoalRate) { return; }
		canSeeToAttack = npc.canSee(target);
		if (canSeeToAttack && distance <= range) {
			if (inMove) {
				if (runPos == null) { clearTarget(); }
				else {
					Node point = Objects.requireNonNull(npc.getNavigation().getPath()).getEndNode();
					if (point == null || point.x < runPos[0] - 2 && point.x > runPos[0] + 2
							|| point.y < runPos[1] - 2 && point.y > runPos[1] + 2
							|| point.z < runPos[2] - 2 && point.z > runPos[2] + 2) {
						clearTarget();
					}
				}
			}
		}
		else {
			if (!inMove || (runPos != null && startTime + 5000 <= System.currentTimeMillis())) {
				clearTarget();
				tryMoveToTarget();
			}
		}
		tryToCauseDamage();
		if (hasAttack) {
			Vec3 vec = null;
			IRayTraceVec pos;
			runPos = null;
			if (!isRanged || distance < tacticalRange) {
				pos = Util.instance.getPosition(target.getX(), target.getY(), target.getZ(), target.getYRot() + 180.0f, target.getXRot(), tacticalRange);
				Path path = npc.getNavigation().createPath(pos.getX(), pos.getY(), pos.getZ(), 1);
				if (path == null) { vec = DefaultRandomPos.getPos(npc, tacticalRange, 2); }
			}
			if (vec == null) {
				double dist = 0.0d;
				int error = 0;
				int attempts = 0;
				while ((dist < tacticalRange || dist < (isRanged ? range / 2.0d : range) || dist > npc.stats.aggroRange) && error < 3 && attempts < 8) {
					attempts++;
					Vec3 vec2 = DefaultRandomPos.getPos(npc, tacticalRange, 2);
					if (vec2 == null) {
						error++;
						continue;
					}
					error = 0;
					dist = Math.sqrt(npc.distanceToSqr(vec2));
					if (npc.stats.calmdown) {
						double homeDist = Util.instance.distanceTo(npc.getStartXPos(), npc.getStartYPos(), npc.getStartZPos(), vec2.x, vec2.y, vec2.z);
						if (homeDist > CustomNpcs.NpcNavRange) { continue; }
					}
					if ((int) vec2.x == npc.blockPosition().getX() &&
							(int) vec2.y == npc.blockPosition().getY() &&
							(int) vec2.x == npc.blockPosition().getZ()) { dist = 0.0d; }
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
			}
			if (vec != null) {
				npc.getNavigation().moveTo(vec.x, vec.y, vec.z, 2.0d);
				runPos = new int[] { (int) Math.floor(vec.x), (int) Math.floor(vec.y), (int) Math.floor(vec.z) };
				targetIn = target;
				startTime = System.currentTimeMillis();
				npc.setTarget(null);
			}
		}
	}

	private void clearTarget() {
		runPos = null;
		if (targetIn != null) {
			npc.setTarget(targetIn);
			targetIn = null;
		}
		startTime = 0L;
		npc.getNavigation().stop();
	}

	@Override
	public boolean canNewAttack() { return runPos == null; }

}