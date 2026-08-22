package noppes.npcs.ai.attack;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.Vec3d;
import noppes.npcs.api.util.IRayTraceRotate;
import noppes.npcs.api.util.IRayTraceVec;
import noppes.npcs.util.Util;

public class EntityAISurround extends EntityAICustom {

	public EntityAISurround(IRangedAttackMob npc) { super(npc); }

	@Override
	public void updateTask() {
		super.updateTask();
		if (isFriend || npc.ticksExisted % (tickRate * 2) != 0) { return; }
		canSeeToAttack = npc.canSee(target);
		double tr = tacticalRange;
		if (tr > range) { tr = range; }
		if (!canSeeToAttack || distance > range) { tryMoveToTarget(); }
		else if (distance <= tr * 0.9d || distance >= tr * 1.1d) {
			IRayTraceRotate angles = Util.instance.getAngles3D(target.posX, target.posY, target.posZ, npc.posX, npc.posY, npc.posZ);
			IRayTraceVec pos = Util.instance.getPosition(target.posX, target.posY, target.posZ, angles.getYaw(), angles.getPitch(), tr);
			Path path = npc.getNavigator().getPathToXYZ(pos.getX(), pos.getY(), pos.getZ());
			if (path != null) { npc.getNavigator().setPath(path, 1.3d); }
			else {
				Vec3d targetVec3 = new Vec3d(npc.posX - pos.getX(), npc.posY - pos.getY(), npc.posZ - pos.getZ());
				Vec3d vec = RandomPositionGenerator.findRandomTargetBlockAwayFrom(npc, 2, 2, targetVec3);
				if (vec != null) { npc.getNavigator().tryMoveToXYZ(vec.x, vec.y, vec.z, 1.3d); }
			}
		}
		tryToCauseDamage();
	}

}
