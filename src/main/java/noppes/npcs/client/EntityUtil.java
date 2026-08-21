package noppes.npcs.client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.HashMap;

public class EntityUtil {

	private static HashMap<EntityEntry, Class<? extends Entity>> entityClasses = new HashMap<>();

	public static void Copy(EntityLivingBase copied, EntityLivingBase entity) {
		entity.world = copied.world;
		entity.deathTime = copied.deathTime;
		entity.distanceWalkedModified = copied.distanceWalkedModified;
		entity.prevDistanceWalkedModified = copied.distanceWalkedModified;
		entity.onGround = copied.onGround;
		entity.distanceWalkedOnStepModified = copied.distanceWalkedOnStepModified;
		entity.moveForward = copied.moveForward;
		entity.moveStrafing = copied.moveStrafing;
		entity.setPosition(copied.posX, copied.posY, copied.posZ);
		entity.setEntityBoundingBox(copied.getEntityBoundingBox());
		entity.prevPosX = copied.prevPosX;
		entity.prevPosY = copied.prevPosY;
		entity.prevPosZ = copied.prevPosZ;
		entity.motionX = copied.motionX;
		entity.motionY = copied.motionY;
		entity.motionZ = copied.motionZ;
		entity.rotationYaw = copied.rotationYaw;
		entity.prevRotationYaw = copied.prevRotationYaw;
		entity.rotationPitch = copied.rotationPitch;
		entity.prevRotationPitch = copied.prevRotationPitch;
		entity.rotationYawHead = copied.rotationYawHead;
		entity.prevRotationYawHead = copied.prevRotationYawHead;
		entity.renderYawOffset = copied.renderYawOffset;
		entity.prevRenderYawOffset = copied.prevRenderYawOffset;
		entity.cameraPitch = copied.cameraPitch;
		entity.prevCameraPitch = copied.prevCameraPitch;
		entity.lastTickPosX = copied.lastTickPosX;
		entity.lastTickPosY = copied.lastTickPosY;
		entity.lastTickPosZ = copied.lastTickPosZ;
		entity.limbSwingAmount = copied.limbSwingAmount;
		entity.prevLimbSwingAmount = copied.prevLimbSwingAmount;
		entity.limbSwing = copied.limbSwing;
		entity.swingProgress = copied.swingProgress;
		entity.prevSwingProgress = copied.prevSwingProgress;
		entity.isSwingInProgress = copied.isSwingInProgress;
		entity.swingProgressInt = copied.swingProgressInt;
		entity.setHealth(Math.min(copied.getHealth(), entity.getMaxHealth()));
		entity.isDead = copied.isDead;
		entity.deathTime = copied.deathTime;
		entity.ticksExisted = copied.ticksExisted;
		entity.getEntityData().merge(copied.getEntityData());
		if (entity instanceof EntityPlayer && copied instanceof EntityPlayer) {
			EntityPlayer ePlayer = (EntityPlayer) entity;
			EntityPlayer cPlayer = (EntityPlayer) copied;
			ePlayer.cameraYaw = cPlayer.cameraYaw;
			ePlayer.prevCameraYaw = cPlayer.prevCameraYaw;
			ePlayer.prevChasingPosX = cPlayer.prevChasingPosX;
			ePlayer.prevChasingPosY = cPlayer.prevChasingPosY;
			ePlayer.prevChasingPosZ = cPlayer.prevChasingPosZ;
			ePlayer.chasingPosX = cPlayer.chasingPosX;
			ePlayer.chasingPosY = cPlayer.chasingPosY;
			ePlayer.chasingPosZ = cPlayer.chasingPosZ;
		}
		if (entity instanceof EntityDragon) {
			entity.rotationYaw += 180.0f;
		}
		if (entity instanceof EntityChicken) {
			((EntityChicken) entity).destPos = (copied.onGround ? 0.0f : 1.0f);
		}
		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
			entity.setItemStackToSlot(slot, copied.getItemStackFromSlot(slot));
		}
		if (copied instanceof EntityNPCInterface && entity instanceof EntityNPCInterface) {
			EntityNPCInterface npc = (EntityNPCInterface) copied;
			EntityNPCInterface target = (EntityNPCInterface) entity;
			target.textureLocation = npc.textureLocation;
			target.textureGlowLocation = npc.textureGlowLocation;
			target.textureCloakLocation = npc.textureCloakLocation;
			target.display = npc.display;
			target.inventory = npc.inventory;
			target.currentAnimation = npc.currentAnimation;
			target.setDataWatcher(npc.getDataManager());
		}
		if (entity instanceof EntityCustomNpc && copied instanceof EntityCustomNpc) {
			EntityCustomNpc npc2 = (EntityCustomNpc) copied;
			EntityCustomNpc target2 = (EntityCustomNpc) entity;
			(target2.modelData = npc2.modelData.copy()).setEntityClass(null);
		}
	}

	public static HashMap<EntityEntry, Class<? extends Entity>> getAllEntitiesClasses(World world) {
		if (!entityClasses.isEmpty()) { return entityClasses; }
		HashMap<EntityEntry, Class<? extends Entity>> data = new HashMap<>();
		for (EntityEntry ent : ForgeRegistries.ENTITIES.getValuesCollection()) {
			try {
				Entity e = ent.newInstance(world);
				if (e != null) {
					if (EntityLiving.class.isAssignableFrom(e.getClass())) { data.put(ent, e.getClass()); }
					e.setDead();
				}
			} catch (Exception ignored) {}
		}
		entityClasses = data;
		return data;
	}

	public static HashMap<String, ResourceLocation> getAllEntities(World world, boolean withNpcs) {
		HashMap<String, ResourceLocation> data = new HashMap<>();
		for (EntityEntry ent : ForgeRegistries.ENTITIES.getValuesCollection()) {
			try {
				Entity e = ent.newInstance(world);
				if (e != null) {
					if (EntityLiving.class.isAssignableFrom(e.getClass()) && (withNpcs || !EntityNPCInterface.class.isAssignableFrom(e.getClass()))) {
						data.put(ent.getName(), ForgeRegistries.ENTITIES.getKey(ent));
					}
					e.setDead();
				}
			}
			catch (Throwable var6) { LogWriter.except(var6); }
		}
		return data;
	}

}
