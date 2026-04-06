package noppes.npcs.ai.attack;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.Util;

public class EntityAICommanderTarget extends EntityAICustom {

	public int baseAnimation;

	private final List<EntityNPCInterface> npcs = new ArrayList<>();
	private boolean done = false;
	private int time = 0;
	private double minDist;

	public EntityAICommanderTarget(IRangedAttackMob npcIn) {
		super(npcIn);
		baseAnimation = npc.currentAnimation;
		npc.aiOwnerNPC = null;
	}

	private void attack() {
		done = true;
		time = 0;
		if (npc.currentAnimation != baseAnimation) { npc.setCurrentAnimation(baseAnimation); }
		for (EntityNPCInterface n : npcs) {
			n.aiOwnerNPC = null;
			n.setAttackTarget(target);
			if (n.aiAttackTarget instanceof EntityAICommanderTarget) { ((EntityAICommanderTarget) n.aiAttackTarget).done = true; }
		}
		npcs.clear();
	}

	private void reset() {
		done = false;
		time = 0;
		if (npc.currentAnimation != baseAnimation) { npc.setCurrentAnimation(baseAnimation); }
		for (EntityNPCInterface n : npcs) {
			n.aiOwnerNPC = null;
			if (n.ais.returnToStart) { n.getNavigator().tryMoveToXYZ(n.getStartXPos(), n.getStartYPos(), n.getStartZPos(), 1.3d); }
		}
		npcs.clear();
	}

	@Override
	public boolean shouldExecute() {
		if (super.shouldExecute()) { return true; }
		reset();
		return false;
	}

	@Override
	public void updateTask() {
		super.updateTask();
		if (isFriend || npc.ticksExisted % (tickRate * 2) != 0) { return; }
		canSeeToAttack = npc.canSee(target);
		if (done) {
			if (canSeeToAttack && distance <= range) {
				if (inMove) { npc.getNavigator().clearPath(); }
			}
			else { tryMoveToTarget(); }
			tryToCauseDamage();
		} else {
			// target is close
			if (canSeeToAttack && distance <= range && distance <= tacticalRange) {
				attack();
				return;
			}
			// collect npc
			if (npcs.isEmpty()) {
				for (EntityNPCInterface n : npc.world.getEntitiesWithinAABB(EntityNPCInterface.class,
						new AxisAlignedBB(-tacticalRange, -tacticalRange, -tacticalRange, tacticalRange, tacticalRange, tacticalRange).offset(npc.posX, npc.posY, npc.posZ),
						entity -> npc.getDistance(entity) < tacticalRange)) {
					if (npc.equals(n)) { continue; }
					if (npc.getFaction().id == n.getFaction().id && n.getAttackTarget() == null
							&& (n.ais.onAttack == 0 || n.ais.onAttack == 2) && n.aiOwnerNPC == null) {
						Path path = n.getNavigator().getPathToEntityLiving(npc);
						if (path != null) {
							npcs.add(n);
							n.getNavigator().setPath(path, 1.0d);
							n.aiOwnerNPC = npc;
						}
					}
				}
				if (npcs.isEmpty()) { // no friends
					attack();
					return;
				}
				npc.setCurrentAnimation(4);
				minDist = npcs.size() < 5 ? 3.0d : 0.4d * npcs.size() + 1.0d;
				time = tacticalRange < 5 ? 18 : (int) (4.90909f * (float) tacticalRange - 6.54545f); // min
																													// 3
																													// sec,
																													// range==16
																													// -
																													// 11
																													// sec
			} else { // checking the distance to friends
				boolean isStart = true;
				for (EntityNPCInterface n : npcs) {
					if (n.aiOwnerNPC == null) { n.aiOwnerNPC = npc; }
					float dist = npc.getDistance(n);
					if (dist > minDist) {
						isStart = false;
						n.getNavigator().tryMoveToEntityLiving(npc, 1.0d);
					}
					else if (dist < 1.5d) { n.getNavigator().clearPath(); }
				}
				time--;
				if (isStart || time <= 0) { attack(); }
			}
		}
	}

}
