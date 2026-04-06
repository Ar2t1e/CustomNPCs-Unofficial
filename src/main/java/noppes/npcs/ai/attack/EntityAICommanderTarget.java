package noppes.npcs.ai.attack;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAICommanderTarget extends EntityAICustom {

	public int baseAnimation;

	private final List<EntityNPCInterface> npcs = new ArrayList<>();
	private boolean done = false;
	private int time = 0;
	private double minDist;

	public EntityAICommanderTarget(RangedAttackMob npcIn) {
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
			n.setTarget(target);
			if (n.aiAttackTarget instanceof EntityAICommanderTarget commander) { commander.done = true; }
		}
		npcs.clear();
	}

	private void reset() {
		done = false;
		time = 0;
		if (npc.currentAnimation != baseAnimation) { npc.setCurrentAnimation(baseAnimation); }
		for (EntityNPCInterface n : npcs) {
			if (n.isAlive()) {
				n.aiOwnerNPC = null;
				if (n.ais.returnToStart) { n.getNavigation().moveTo(n.getStartXPos(), n.getStartYPos(), n.getStartZPos(), 2.0d); }
			}
		}
		npcs.clear();
	}

	@Override
	public boolean canUse() {
		if (super.canUse()) { return true; }
		reset();
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		if (isFriend || npc.tickCount % (newGoalRate * 2) > newGoalRate) { return; }
		canSeeToAttack = npc.canSee(target);
		if (done) {
			if (canSeeToAttack && distance <= range) {
				if (inMove) { npc.getNavigation().stop(); }
			}
			else { tryMoveToTarget(); }
			tryToCauseDamage();
		}
		else {
			// target is close
			if (canSeeToAttack && distance <= range && distance <= tacticalRange) {
				attack();
				return;
			}
			// collect npc
			if (npcs.isEmpty()) {
				for (EntityNPCInterface n : npc.level().getEntitiesOfClass(EntityNPCInterface.class,
						new AABB(-tacticalRange, -tacticalRange, -tacticalRange, tacticalRange, tacticalRange, tacticalRange).move(npc.getX(), npc.getY(), npc.getZ()),
						entity -> npc.distanceTo(entity) < tacticalRange)) {
					if (npc.equals(n)) { continue; }
					if (npc.getFaction().id == n.getFaction().id && n.getTarget() == null
							&& (n.ais.onAttack == 0 || n.ais.onAttack == 2) && n.aiOwnerNPC == null) {
						Path path = n.getNavigation().createPath(npc, 1);
						if (path != null) {
							npcs.add(n);
							n.getNavigation().moveTo(path, 1.0d);
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
					float dist = npc.distanceTo(n);
					if (dist > minDist) {
						isStart = false;
						n.getNavigation().moveTo(npc, 1.0d);
					}
					else if (dist < 1.5d) { n.getNavigation().stop(); }
				}
				time--;
				if (isStart || time <= 0) { attack(); }
			}
		}
	}

}
