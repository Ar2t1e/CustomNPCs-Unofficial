package noppes.npcs.ai.selector;

import com.google.common.base.Predicate;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.constants.EnumSeeTarget;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobGuard;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.companion.CompanionGuard;

public class NPCAttackSelector implements Predicate<EntityLivingBase> {

	private final EntityNPCInterface npc;

	public NPCAttackSelector(EntityNPCInterface npcIn) {
		npc = npcIn;
	}

	@Override
	public boolean apply(EntityLivingBase entity) {
		if (entity != null && entity.isEntityAlive() &&
				entity != npc &&
				npc.isInRange(entity, npc.stats.aggroRange) &&
				entity.getHealth() >= 0.1F) {
			if (npc.ais.directLOS != EnumSeeTarget.NONE && !npc.canSee(entity)) { return false; }
			if (!npc.isFollower() && npc.ais.shouldReturnHome()) {
				int allowedDistance = npc.stats.aggroRange * 2;
				if (npc.ais.getMovingType() == 1) {
					allowedDistance += npc.ais.walkingRange;
				}
				double distance = entity.getDistanceSq(npc.getStartXPos(), npc.getStartYPos(), npc.getStartZPos());
				if (npc.ais.getMovingType() == 2) {
					int[] arr = npc.ais.getCurrentMovingPath();
					distance = entity.getDistanceSq(arr[0], arr[1], arr[2]);
				}
				if (distance > allowedDistance * allowedDistance) { return false; }
			}
			if (npc.job instanceof JobGuard && ((JobGuard) npc.job).isEntityApplicable(entity)) { return true; }
			if (npc.role instanceof RoleCompanion) {
				RoleCompanion role = (RoleCompanion) npc.role;
				if (role.job.getType() == EnumCompanionJobs.GUARD && ((CompanionGuard) role.job).isEntityApplicable(entity)) { return true; }
			}
			if (entity instanceof EntityPlayerMP) {
				if (npc.faction.isAggressiveToPlayer((EntityPlayerMP) entity) && !((EntityPlayerMP) entity).isCreative()) {
					if (CustomNpcs.EnableInvisibleNpcs && CustomNpcs.InvisibilityAlgorithm == 2) {
						return npc.display.isVisibleTo((EntityPlayerMP) entity) || ((EntityPlayerMP) entity).isSpectator() ||
								entity.getHeldItemMainhand().getItem() == CustomItems.wand;
					}
					return true;
				}
				return false;
			}
			if (entity instanceof EntityNPCInterface) {
				if (((EntityNPCInterface) entity).isKilled()) { return false; }
				if (npc.advanced.attackOtherFactions) { return npc.faction.isAggressiveToNpc((EntityNPCInterface) entity); }
			}
			return npc.aiAttackTarget == null || npc.aiAttackTarget.canNewAttack();
		}
		return false;
	}

}
