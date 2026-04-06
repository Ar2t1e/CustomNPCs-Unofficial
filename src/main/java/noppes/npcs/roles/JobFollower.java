package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.data.role.IJobFollower;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

public class JobFollower extends JobInterface implements IJobFollower {

	public EntityNPCInterface following = null;
	public String name = "";
	private int ticks = 40;

	public JobFollower(EntityNPCInterface npc) {
		super(npc);
		type = JobType.FOLLOWER;
	}

	@Override
	public void load(NBTTagCompound compound) {
		super.load(compound);
		type = JobType.FOLLOWER;
		name = compound.getString("FollowingEntityName");
	}

	@Override
	public NBTTagCompound save(NBTTagCompound compound) {
		super.save(compound);
		compound.setString("FollowingEntityName", name);
		return compound;
	}

	@Override
	public boolean aiShouldExecute() {
		if (npc != null && !npc.isAttacking()) {
			--ticks;
			if (ticks <= 0) {
				ticks = 10;

				// New from Unofficial (BetaZavr)
				if (following != null) {
					double dist = npc.getDistance(following);
					if (dist <= 1.5d) {
						if (!npc.getNavigator().noPath()) { npc.getNavigator().clearPath(); }
						return true;
					}
					if (dist > getRange()) {
						if (!npc.getNavigator().tryMoveToEntityLiving(following, 1.0d)) { following = null; }
					}
					else { following = null; }
					if (following != null) { return true; }
				}

				List<EntityNPCInterface> list = new ArrayList<>();
				try {
					list = npc.world.getEntitiesWithinAABB(EntityNPCInterface.class,
							npc.getEntityBoundingBox().grow(getRange(), getRange(), getRange()));
				}
				catch (Exception ignored) { }
				for (EntityNPCInterface entity : list) {
					if (entity != npc && !entity.isKilled() && entity.display.getName().equalsIgnoreCase(name)) {

						following = entity;
						break;
					}
				}
			}
		}
		return false;
	}

	@Override
	public String getFollowing() { return name; }

	@Override
	public ICustomNpc<?> getFollowingNpc() {
		if (following == null) {
			return null;
		}
		return following.wrappedNPC;
	}

	public boolean hasOwner() { return !name.isEmpty() && isFollowing(); }

	@Override
	public boolean isFollowing() { return following != null; }

    @Override
	public void stop() { following = null; }

	@Override
	public void setFollowing(String n) { name = n; }

	// New from Unofficial (BetaZavr)
	@Override
	public void aiUpdateTask() {
		if (npc != null) {
			npc.getLookHelper().setLookPosition(following.posX, following.posY + following.getEyeHeight(), following.posZ,
					10.0f, npc.getVerticalFaceSpeed());
		}
	}

	@Override
	public boolean isWorking() { return isFollowing(); }

	private int getRange() {
		return ValueUtil.correctInt(npc != null ? Math.min(npc.followRange(), npc.stats.aggroRange) : 4, 2, CustomNpcs.NpcNavRange);
	}

}
