package noppes.npcs.ai.attack;

import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.api.util.IRayTraceRotate;
import noppes.npcs.api.util.IRayTraceVec;
import noppes.npcs.util.Util;

public class EntityAISurround extends EntityAICustom {

	public EntityAISurround(RangedAttackMob npc) { super(npc); }

	@Override
	public void tick() {
		super.tick();
		if (isFriend || npc.tickCount % (newGoalRate * 2) > newGoalRate) { return; }
		canSeeToAttack = npc.canSee(target);
		double tr = tacticalRange;
		if (tr > range) { tr = range; }
		if (!canSeeToAttack || distance > range) { tryMoveToTarget(); }
		else if (distance <= tr * 0.9d || distance >= tr * 1.1d) {
			IRayTraceRotate angles = Util.instance.getAngles3D(target.getX(), target.getY(), target.getZ(), npc.getX(), npc.getY(), npc.getZ());
			IRayTraceVec pos = Util.instance.getPosition(target.getX(), target.getY(), target.getZ(), angles.getYaw(), angles.getPitch(), tr);
			Path path = npc.getNavigation().createPath(pos.getX(), pos.getY(), pos.getZ(), 1);
			if (path != null) { npc.getNavigation().moveTo(path, 1.3d); }
			else {
				Vec3 targetVec3 = new Vec3(npc.getX() - pos.getX(), npc.getY() - pos.getY(), npc.getZ() - pos.getZ());
				Vec3 vec = DefaultRandomPos.getPosAway(npc, 2, 2, targetVec3);
				if (vec != null) { npc.getNavigation().moveTo(vec.x, vec.y, vec.z, 1.3d); }
			}
		}
		tryToCauseDamage();
	}

}
