package noppes.npcs.ai.target;

import java.util.Iterator;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.AnimationKind;
import noppes.npcs.api.util.IRayTraceRotate;
import noppes.npcs.constants.AiMutex;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.Util;

public class EntityAILook extends EntityAIBase {

	private final EntityNPCInterface npc;
	private int idle = 0;
	private double lookX;
	private double lookZ;
	private boolean forced = false;
	private Entity forcedEntity = null;

	// New from Unofficial (BetaZavr)
	private double lookY;
	boolean rotateBody;
	public boolean fastRotation = false;

	public EntityAILook(EntityNPCInterface npcIn) {
		npc = npcIn;
		setMutexBits(AiMutex.LOOK);
	}

	@Override
	public void resetTask() {
		rotateBody = false;
		forced = false;
		forcedEntity = null;
	}

	@Override
	public boolean shouldExecute() {
		if (forced) { return true; }
		if (!npc.isAttacking() && npc.getNavigator().noPath() && !npc.isPlayerSleeping() && npc.isEntityAlive() && (!CustomNpcs.ShowCustomAnimation ||
				!npc.animation.isAnimated(AnimationKind.ATTACKING, AnimationKind.INIT, AnimationKind.INTERACT, AnimationKind.DIES))) {
			if (!npc.isInteracting() && npc.ais.getStandingType() <= 0 && idle <= 0) { return npc.getRNG().nextFloat() < 0.004F; }
			return true;
		}
		return false;
	}

	@Override
	public void startExecuting() {
		rotateBody = npc.ais.getStandingType() == 0 || npc.ais.getStandingType() == 3;
		if (rotateBody) {
			double d0 = Math.PI * 2.0d * npc.getRNG().nextDouble();
			if (npc.ais.getStandingType() == 3) {
				double d1 = Math.PI / 180.0d;
				double d2 = Math.PI / 5.0d;
				double d3 = Math.PI * 3.0d / 5.0d;
				d0 = d1 * npc.ais.orientation + d2 + d3 * npc.getRNG().nextDouble();
			}
			lookX = Math.cos(d0);
			lookZ = Math.sin(d0);
			idle = 20 + npc.getRNG().nextInt(20);
		}
	}

	@Override
	public void updateTask() {
		Entity lookat = null;
		// has Target Entity
		if (forced && forcedEntity != null) { lookat = forcedEntity; }
		else if (npc.isInteracting()) {
			Iterator<EntityLivingBase> ita = npc.interactingEntities.iterator();
			double closestDistance = 12.0;
			while (ita.hasNext()) {
				EntityLivingBase entity = ita.next();
				double distance = entity.getDistance(npc);
				if (distance < closestDistance) {
					closestDistance = entity.getDistance(npc);
					lookat = entity;
				}
				else if (distance > 12.0D) { ita.remove(); }
			}
		}
		else if (npc.ais.getStandingType() == 2 || npc.ais.getStandingType() == 4) {
			lookat = npc.world.getClosestPlayerToEntity(npc, 16.0);
		} // Stalking or EyeRotation
		// looking at someone
		if (lookat != null) {
			npc.updateLook = npc.lookAt == null || !npc.lookAt.equals(lookat);
			npc.lookAt = lookat;
			double posY;
			if (lookat instanceof EntityLivingBase) { posY = lookat.posY + (double) lookat.getEyeHeight(); }
			else { posY = (lookat.getEntityBoundingBox().minY + lookat.getEntityBoundingBox().maxY) / 2.0D; }
			setLookPosition(lookat.posX, posY, lookat.posZ, npc.getVerticalFaceSpeed());
			return;
		}
		// looks in a random direction
		npc.updateLook = npc.lookAt != null;
		npc.lookAt = null;
		if (rotateBody) {
			if (idle == 0 && npc.getRNG().nextFloat() < 0.004f) {
				double d0 = Math.PI * npc.getRNG().nextDouble() * 2.0;
				if (npc.ais.getStandingType() == 3) { // only head
					double d1 = Math.PI / 180.0d;
					double d2 = Math.PI / 5.0d;
					double d3 = Math.PI * 3.0d / 5.0d;
					d0 = d1 * npc.ais.orientation + d2 + d3 * npc.getRNG().nextDouble();
				}
				lookX = Math.cos(d0);
				lookY = (npc.getRNG().nextFloat() - 0.5f) * 0.85f;
				lookZ = Math.sin(d0);

				IRayTraceRotate data = Util.instance.getAngles3D(npc.posX, npc.posY, npc.posZ, lookX, lookY, lookZ);
				npc.lookPos[0] = (float) data.getYaw();
				npc.lookPos[1] = (float) data.getPitch();
				npc.updateClient();
				idle = 20 + npc.getRNG().nextInt(20);
			} else if (npc.ais.getStandingType() == 3 || npc.ais.getStandingType() == 0) {
				if (lookX != 0.0f && lookY != 0.0f && lookZ != 0.0f) {
					setLookPosition(npc.posX + lookX, npc.posY + npc.getEyeHeight() + lookY, npc.posZ + lookZ, npc.getVerticalFaceSpeed());
				}
			}
			if (idle > 0) {
				--idle;
				setLookPosition(npc.posX + lookX, npc.posY + npc.getEyeHeight() + lookY, npc.posZ + lookZ, npc.getVerticalFaceSpeed());
			}
		}
		// doesn't look at anyone
		if ((npc.ais.getStandingType() == 1 || npc.ais.getStandingType() == 4) && !forced) {
			npc.renderYawOffset = npc.ais.orientation;
			npc.rotationYaw = npc.ais.orientation;
			npc.rotationYawHead = npc.ais.orientation;
		}
	}

	public void rotate(Entity entity) {
		forced = true;
		forcedEntity = entity;
	}

	public void rotate(float degrees) {
		forced = true;
		npc.renderYawOffset = degrees;
		npc.rotationYaw = degrees;
		npc.rotationYawHead = degrees;
	}

	// New from Unofficial (BetaZavr)
	private void setLookPosition(double x, double y, double z, int verticalFaceSpeed) {
		if (!CustomNpcs.ShowCustomAnimation || !npc.animation.isAnimated(AnimationKind.ATTACKING, AnimationKind.INIT, AnimationKind.INTERACT, AnimationKind.DIES)) {
			npc.getLookHelper().setLookPosition(x, y, z, 10.0f, verticalFaceSpeed);
		}
	}

}
