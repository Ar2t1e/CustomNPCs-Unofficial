package noppes.npcs.entity.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NBTTags;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IPos;
import noppes.npcs.api.entity.data.INPCAi;
import noppes.npcs.api.wrapper.BlockPosWrapper;
import noppes.npcs.constants.EnumNpcTactics;
import noppes.npcs.constants.EnumSeeTarget;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobBuilder;
import noppes.npcs.roles.JobFarmer;
import noppes.npcs.util.ValueUtil;

public class DataAI
		implements INPCAi {

	protected final EntityNPCInterface npc;
	protected List<int[]> movingPath = new ArrayList<>();
	protected BlockPos startPos = null;
	protected int standingType = 0; // 0:NoRotation, 1:RotateBody, 2:Stalking, 3:HeadRotation, 4:EyeRotation
	protected int movingType = 0; // 0:Standing, 1:Wandering, 2:MovingPath -> EntityAIMovingPath
	protected int tacticalRadius = 8;
	protected int moveSpeed = 5;

	public boolean attackInvisible = false;
	public boolean avoidsSun = false;
	public boolean avoidsWater = false;
	public boolean canLeap = false; // can jump to target
	public boolean canSprint = false;
	public boolean canSwim = true;
	public boolean movingPause = true; // -> EntityAIMovingPath
	public boolean npcInteracting = true;
	public boolean reactsToFire = false;
	public boolean returnToStart = true;
	public boolean stopAndInteract = true;

	public int animationType = 0;
	public int doorInteract = 2;
	public int findShelter = 2; // 0:Night, 1:Day, 2:Disable
	public int movementType = 0; // 0:Ground, 1:Flying, 2:Swimming
	public int movingPattern = 0; // -> EntityAIMovingPath
	public int movingPos = 0; // -> EntityAIMovingPath
	public int onAttack = 0; // 0:Normal, 1:Panic, 2:Retreat, 3:Nothing
	public int orientation = 0;
	public int walkingRange = 10;

	public float bodyOffsetX = 5.0f;
	public float bodyOffsetY = 5.0f;
	public float bodyOffsetZ = 5.0f;

	// New from Unofficial (GoodBird)
	public int activeRange = 32;
	public boolean mountControl = false;

	// New fields from Unofficial (BetaZavr)
	public EnumNpcTactics tacticalVariant = EnumNpcTactics.RUSH;
	public EnumSeeTarget directLOS = EnumSeeTarget.NORMAL; // old: true
	protected int maxHurtResistantTime = CustomNpcs.DefaultHurtResistantTime * 2;
	public boolean aiDisabled = false;
	public boolean canBeCollide = true;
	public float stepheight = 0.6f;

	public DataAI(EntityNPCInterface npcIn) { npc = npcIn; }

	public void appendMovingPath(int[] pos) { movingPath.add(pos); }

	public void clearMovingPath() {
		movingPath.clear();
		movingPos = 0;
	}

	public void decreaseMovingPath() {
		List<int[]> list = getMovingPath();
		if (list.size() == 1) {
			movingPos = 0;
			return;
		}
		--movingPos;
		if (movingPos < 0) {
			if (movingPattern == 0) { movingPos = list.size() - 1; }
			else if (movingPattern == 1) { movingPos = list.size() * 2 - 2; }
		}
	}

	@Override
	public int getAnimation() { return animationType; }

	@Override
	public boolean getAttackInvisible() { return attackInvisible; }

	@Override
	public boolean getAvoidsWater() { return avoidsWater; }

	@Override
	public boolean getCanSwim() { return canSwim; }

	@Override
	public int getCurrentAnimation() { return npc.currentAnimation; }

	public int[] getCurrentMovingPath() {
		List<int[]> list = getMovingPath();
		int size = list.size();
		if (size == 1) { return list.get(0); }
		int pos = movingPos;
		if (movingPattern == 0 && pos >= size) { pos = movingPos = 0; }
		else if (movingPattern == 1) {
			int size2 = size * 2 - 1;
			if (pos >= size2) { pos = movingPos = 0; }
			else if (pos >= size) { pos = size2 - pos; }
		}
		return list.get(pos);
	}

	public double getDistanceSqToPathPoint() {
		int[] pos = getCurrentMovingPath();
		return npc.getDistanceSq(pos[0] + 0.5, pos[1], pos[2] + 0.5);
	}

	@Override
	public int getDoorInteract() { return doorInteract; }

	@Override
	public boolean getInteractWithNPCs() { return npcInteracting; }

	@Override
	public boolean getLeapAtTarget() { return canLeap; }

	public List<int[]> getMovingPath() {
		if (startPos != null) {
			if (movingPath.isEmpty()) { movingPath.add(getStartArray()); }
			else {
				int[] arr = movingPath.get(0);
				if (arr[0] != startPos.getX() || arr[1] != startPos.getY() || arr[2] != startPos.getZ()) {
					movingPath.remove(0);
					movingPath.add(0, getStartArray());
				}
			}
		}
		return movingPath;
	}

	@Override
	public boolean getMovingPathPauses() { return movingPause; }

	public int[] getMovingPathPos(int m_pos) { return movingPath.get(m_pos); }

	public int getMovingPathSize() { return movingPath.size(); }

	@Override
	public int getMovingPathType() { return movingPattern; }

	public int getMovingPos() { return movingPos; }

	/**
	 * @return
	 * 		0: Standing
	 * 		1: Wandering
	 * 		2: MovingPath -> EntityAIMovingPath
	 */
	@Override
	public int getMovingType() { return movingType; }

	/**
	 * @return 0:Ground, 1:Flying, 2:Swimming
	 */
	@Override
	public int getNavigationType() { return movementType; }

	/**
	 * @return 0:Normal, 1:Panic, 2:Retreat, 3:Nothing
	 */
	@Override
	public int getRetaliateType() { return onAttack; }

	@Override
	public boolean getReturnsHome() { return returnToStart; }

	/**
	 * 0:Night, 1:Day, 2:Disable
	 */
	@Override
	public int getSheltersFrom() { return findShelter; }

	/**
	 * 0:NoRotation, 1:RotateBody, 2:Stalking, 3:HeadRotation, 4:EyeRotation
	 */
	@Override
	public int getStandingType() { return standingType; }

	public int[] getStartArray() {
		BlockPos pos = startPos();
		return new int[] { pos.getX(), pos.getY(), pos.getZ() };
	}

	public IPos getStartPos() { return new BlockPosWrapper(startPos()); }

	@Override
	public boolean getStopOnInteract() { return stopAndInteract; }

	@Override
	public int getTacticalRange() { return tacticalRadius; }

	@Override
	public int getTacticalType() { return tacticalVariant.ordinal(); }

	@Override
	public int getWalkingSpeed() { return moveSpeed; }

	@Override
	public int getWanderingRange() { return walkingRange; }

	@Override
	public int getMaxHurtResistantTime() { return maxHurtResistantTime; }

	public void incrementMovingPath() {
		List<int[]> list = getMovingPath();
		if (list.size() == 1) {
			movingPos = 0;
			return;
		}
		++movingPos;
		if (movingPattern == 0) { movingPos %= list.size(); }
		else if (movingPattern == 1) {
			int size = list.size() * 2 - 1;
			movingPos %= size;
		}
	}

	public void load(NBTTagCompound compound) {
		canSwim = compound.getBoolean("CanSwim");
		reactsToFire = compound.getBoolean("ReactsToFire");
		setAvoidsWater(compound.getBoolean("AvoidsWater"));
		avoidsSun = compound.getBoolean("AvoidsSun");
		returnToStart = compound.getBoolean("ReturnToStart");
		onAttack = compound.getInteger("OnAttack");
		doorInteract = compound.getInteger("DoorInteract");
		findShelter = compound.getInteger("FindShelter");
		canLeap = compound.getBoolean("CanLeap");
		canSprint = compound.getBoolean("CanSprint");
		tacticalRadius = compound.getInteger("TacticalRadius");
		movingPause = compound.getBoolean("MovingPause");
		npcInteracting = compound.getBoolean("npcInteracting");
		stopAndInteract = compound.getBoolean("stopAndInteract");
		movementType = compound.getInteger("MovementType");
		animationType = compound.getInteger("MoveState");
		standingType = compound.getInteger("StandingState");
		movingType = compound.getInteger("MovingState");
		orientation = compound.getInteger("Orientation");
		bodyOffsetY = compound.getFloat("PositionOffsetY");
		bodyOffsetZ = compound.getFloat("PositionOffsetZ");
		bodyOffsetX = compound.getFloat("PositionOffsetX");
		walkingRange = compound.getInteger("WalkingRange");
		setWalkingSpeed(compound.getInteger("MoveSpeed"));
		setMovingPath(NBTTags.getIntegerArraySet(compound.getTagList("MovingPathNew", 10)));
		movingPos = compound.getInteger("MovingPos");
		movingPattern = compound.getInteger("MovingPatern");
		attackInvisible = compound.getBoolean("AttackInvisible");
		if (compound.hasKey("StartPosNew")) {
			int[] pos = compound.getIntArray("StartPosNew");
			startPos = new BlockPos(pos[0], pos[1], pos[2]);
		}
		if (standingType != 0 && standingType != 2) {
			npc.setRotationYawHead(orientation);
		}

		// New from Unofficial (GoodBird)
		if (compound.hasKey("ActiveRange", 3)) { activeRange = compound.getInteger("ActiveRange"); }
		mountControl = compound.getBoolean("MountControl");

		// New fields from Unofficial (BetaZavr)
		setTacticalType(compound.getInteger("TacticalVariant"));
		if (compound.hasKey("CanBeCollide", 1)) { canBeCollide = compound.getBoolean("CanBeCollide"); }
		if (compound.hasKey("StepHeight", 5)) { stepheight = compound.getFloat("StepHeight"); }
		npc.stepHeight = stepheight;
		if (compound.hasKey("MaxHurtResistantTime", 3)) { maxHurtResistantTime = compound.getInteger("MaxHurtResistantTime"); }
		npc.maxHurtResistantTime = maxHurtResistantTime;
		aiDisabled = compound.getBoolean("AIDisabled");
		if (compound.hasKey("DirectLOS", 1)) {
			directLOS = compound.getBoolean("DirectLOS") ? EnumSeeTarget.NORMAL : EnumSeeTarget.NONE;
		} // OLD
		else { directLOS = EnumSeeTarget.values()[ValueUtil.onlyPositiveInt(compound.getInteger("DirectLOS"), EnumSeeTarget.values().length - 1)]; }
	}

	@Override
	public void setAnimation(int type) { animationType = type; }

	@Override
	public void setAttackInvisible(boolean attack) { attackInvisible = attack; }

	@Override
	public void setAvoidsWater(boolean enabled) {
		if (npc.getNavigator() instanceof PathNavigateGround) { npc.setPathPriority(PathNodeType.WATER, enabled ? PathNodeType.WATER.getPriority() : 0.0f); }
		avoidsWater = enabled;
	}

	@Override
	public void setCanSwim(boolean canSwimIn) { canSwim = canSwimIn; }

	@Override
	public void setDoorInteract(int type) {
		doorInteract = type;
		npc.updateAI = true;
	}

	@Override
	public void setInteractWithNPCs(boolean interact) { npcInteracting = interact; }

	@Override
	public void setLeapAtTarget(boolean leap) {
		canLeap = leap;
		npc.updateAI = true;
	}

	public void setMovingPath(List<int[]> list) {
		movingPath = list;
		if (!movingPath.isEmpty()) {
			int[] pos = movingPath.get(0);
			startPos = new BlockPos(pos[0], pos[1], pos[2]);
		}
	}

	public void setMovingPathPos(int m_pos, int[] pos) {
		if (m_pos < 0) { m_pos = 0; }
		movingPath.set(m_pos, pos);
	}

	@Override
	public void setMovingPathType(int type, boolean pauses) {
		if (type != 0 && type != 1) { throw new CustomNPCsException("Moving path type: " + type); }
		movingPattern = type;
		movingPause = pauses;
	}

	public void setMovingPos(int pos) { movingPos = pos; }

	@Override
	public void setMovingType(int type) {
		if (type < 0 || type > 2) { throw new CustomNPCsException("Unknown moving type: " + type); }
		movingType = type;
		npc.updateAI = true;
	}

	@Override
	public void setNavigationType(int type) { movementType = type; }

	@Override
	public void setRetaliateType(int type) {
		if (type < 0 || type > 3) { throw new CustomNPCsException("[0 / 3] ]Unknown retaliation type: " + type); }
		onAttack = type;
		npc.updateAI = true;
	}

	@Override
	public void setReturnsHome(boolean bo) { returnToStart = bo; }

	@Override
	public void setSheltersFrom(int type) {
		findShelter = type;
		npc.updateAI = true;
	}

	@Override
	public void setStandingType(int type) {
		if (type < 0 || type > 4) { throw new CustomNPCsException("Unknown standing type: " + type); }
		standingType = type;
		npc.updateAI = true;
	}

	public void setStartPos(BlockPos pos) {
		startPos = pos;
		npc.setHomePosAndDistance(startPos, Math.max(npc.stats.aggroRange * 2, CustomNpcs.NpcNavRange * 2));
	}

	public void setStartPos(double x, double y, double z) {
		startPos = new BlockPos(x, y, z);
	}

	public void setStartPos(IPos pos) { startPos = pos.getMCBlockPos(); }

	@Override
	public void setStopOnInteract(boolean stopOnInteract) {
		stopAndInteract = stopOnInteract;
	}

	@Override
	public void setTacticalRange(int range) { tacticalRadius = range; }

	@Override
	public void setTacticalType(int type) {
		tacticalVariant = EnumNpcTactics.values()[ValueUtil.onlyPositiveInt(type, EnumNpcTactics.values().length - 1)];
		npc.updateAI = true;
	}

	@Override
	public void setWalkingSpeed(int speed) {
		if (speed < 0 || speed > 10) { throw new CustomNPCsException("Wrong speed: " + speed); }
		moveSpeed = speed;
		npc.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(npc.getSpeed());
		npc.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue((npc.getSpeed() * 2.0f));
	}

	@Override
	public void setWanderingRange(int range) {
		if (range < 1 || range > 50) { throw new CustomNPCsException("Bad wandering range: " + range + " (1 - 50)"); }
		walkingRange = range;
	}

	@Override
	public void setMaxHurtResistantTime(int ticks) {
		if (ticks < 0) { ticks *= -1; }
		if (ticks > 1200) { ticks = 1200; }
		maxHurtResistantTime = ticks;
	}

	public boolean shouldReturnHome() {
		return (!(npc.job instanceof JobBuilder) || !((JobBuilder) npc.job).isBuilding()) &&
				(!(npc.job instanceof JobFarmer) || !((JobFarmer) npc.job).isPlucking()) &&
				returnToStart;
	}

	public BlockPos startPos() {
		if (startPos == null) { startPos = new BlockPos(npc); }
		return startPos;
	}

	public NBTTagCompound save(NBTTagCompound compound) {
		setAvoidsWater(avoidsWater);
		compound.setBoolean("CanSwim", canSwim);
		compound.setBoolean("ReactsToFire", reactsToFire);
		compound.setBoolean("AvoidsWater", avoidsWater);
		compound.setBoolean("AvoidsSun", avoidsSun);
		compound.setBoolean("ReturnToStart", returnToStart);
		compound.setInteger("OnAttack", onAttack);
		compound.setInteger("DoorInteract", doorInteract);
		compound.setInteger("FindShelter", findShelter);
		compound.setBoolean("CanLeap", canLeap);
		compound.setBoolean("CanSprint", canSprint);
		compound.setBoolean("CanBeCollide", canBeCollide);
		compound.setInteger("TacticalRadius", tacticalRadius);
		compound.setBoolean("MovingPause", movingPause);
		compound.setBoolean("npcInteracting", npcInteracting);
		compound.setBoolean("stopAndInteract", stopAndInteract);
		compound.setInteger("MoveState", animationType);
		compound.setInteger("StandingState", standingType);
		compound.setInteger("MovingState", movingType);
		compound.setInteger("TacticalVariant", tacticalVariant.ordinal());
		compound.setInteger("MovementType", movementType);
		compound.setInteger("Orientation", orientation);
		compound.setFloat("PositionOffsetX", bodyOffsetX);
		compound.setFloat("PositionOffsetY", bodyOffsetY);
		compound.setFloat("PositionOffsetZ", bodyOffsetZ);
		compound.setFloat("StepHeight", stepheight);
		compound.setInteger("WalkingRange", walkingRange);
		compound.setInteger("MoveSpeed", moveSpeed);
		compound.setTag("MovingPathNew", NBTTags.nbtIntegerArraySet(movingPath));
		compound.setInteger("MovingPos", movingPos);
		compound.setInteger("MovingPatern", movingPattern);
		compound.setIntArray("StartPosNew", getStartArray());
		compound.setBoolean("AttackInvisible", attackInvisible);

		// New from Unofficial (GoodBird)
		compound.setInteger("ActiveRange", activeRange);
		compound.setBoolean("MountControl", mountControl);

		// New fields from Unofficial (BetaZavr)
		compound.setInteger("MaxHurtResistantTime", maxHurtResistantTime);
		compound.setBoolean("AIDisabled", aiDisabled);
		compound.setInteger("DirectLOS", directLOS.ordinal());
		return compound;
	}

	@Override
	public boolean isAIDisabled() { return aiDisabled; }

	@Override
	public void setIsAIDisabled(boolean bo) { aiDisabled = bo; }

	@Override
	public float getOffsetX() { return bodyOffsetX; }

	@Override
	public float getOffsetY() { return bodyOffsetY; }

	@Override
	public float getOffsetZ() { return bodyOffsetZ; }

	@Override
	public void setOffset(float x, float y, float z) {
		bodyOffsetX = ValueUtil.correctFloat(x, 0.0f, 9.99f);
		bodyOffsetY = ValueUtil.correctFloat(y, 0.0f, 9.99f);
		bodyOffsetZ = ValueUtil.correctFloat(z, 0.0f, 9.99f);
		npc.updateClient = true;
	}

	@Override
	public EnumSeeTarget getAttackLOS() { return directLOS; }

	@Override
	public void setAttackLOS(int type) {
		if (type < 0 || type >= EnumSeeTarget.values().length) { throw new CustomNPCsException("AttackLOS type mast be 0 to {}", EnumSeeTarget.values().length - 1); }
		directLOS = EnumSeeTarget.values()[type];
		npc.updateAI = true;
	}

	@Override
	public boolean canBeCollide() { return canBeCollide; }

	@Override
	public void setCanBeCollide(boolean bo) { canBeCollide = bo; }

	// New from Unofficial (GoodBird)
	@Override
	public void setMountControl(boolean enabled) { mountControl = enabled; }

}
