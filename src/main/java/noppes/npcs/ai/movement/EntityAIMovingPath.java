package noppes.npcs.ai.movement;

import java.util.List;

import net.minecraft.entity.ai.EntityAIBase;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIMovingPath extends EntityAIBase {
	
	private final EntityNPCInterface npc;
	private int[] pos;
	private int retries;

	public EntityAIMovingPath(EntityNPCInterface iNpc) {
		retries = 0;
		npc = iNpc;
		setMutexBits(AiMutex.PASSIVE);
	}

	public boolean shouldContinueExecuting() {
		if ((npc.isAttacking() && npc.ais.onAttack != 3) || npc.isInteracting()) {
			npc.ais.decreaseMovingPath();
			return false;
		}
		if (!npc.getNavigator().noPath()) {
			return true;
		}
		npc.getNavigator().clearPath();
		if (npc.getDistanceSq(pos[0], pos[1], pos[2]) < 3.0) {
			return false;
		}
		if (retries++ < 3) {
			startExecuting();
			return true;
		}
		return false;
	}

	public boolean shouldExecute() {
		CustomNpcs.debugData.start(npc);
		if ((npc.isAttacking() && npc.ais.onAttack != 3) || npc.isInteracting()
				|| (npc.getRNG().nextInt(40) != 0 && npc.ais.movingPause)
				|| !npc.getNavigator().noPath()) {
			CustomNpcs.debugData.end(npc);
			return false;
		}
		List<int[]> list = npc.ais.getMovingPath();
		if (list.size() < 2) {
			CustomNpcs.debugData.end(npc);
			return false;
		}
		npc.ais.incrementMovingPath();
		pos = npc.ais.getCurrentMovingPath();
		retries = 0;
		CustomNpcs.debugData.end(npc);
		return true;
	}

	public void startExecuting() {
		CustomNpcs.debugData.start(npc);
		npc.getNavigator().tryMoveToXYZ(pos[0] + 0.5, pos[1], pos[2] + 0.5, 1.0d);
		CustomNpcs.debugData.end(npc);
	}
}
