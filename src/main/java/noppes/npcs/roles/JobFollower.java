package noppes.npcs.roles;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.constants.JobType;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.data.role.IJobFollower;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

public class JobFollower extends JobInterface implements IJobFollower {

    public EntityNPCInterface following = null;
    public String name = "";
    protected int ticks = 40;

    public JobFollower(EntityNPCInterface npc) {
        super(npc);
        type = JobType.FOLLOWER;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        super.save(compound);
        compound.putString("FollowingEntityName", name);
        return compound;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        type = JobType.FOLLOWER;
        name = compound.getString("FollowingEntityName");
    }

    @Override
    public boolean aiShouldExecute() {
        if (npc != null && !npc.isAttacking()) {
            --ticks;
            if (ticks <= 0) {
                ticks = 10;

                // New from Unofficial (BetaZavr)
                if (following != null) {
                    double dist = npc.distanceTo(following);
                    if (dist <= 1.5d) {
                        if (!npc.getNavigation().isDone()) { npc.getNavigation().stop(); }
                        return true;
                    }
                    if (dist > getRange()) {
                        if (!npc.getNavigation().moveTo(following, 1.0d)) { following = null; }
                    }
                    else { following = null; }
                    if (following != null) { return true; }
                }

                List<EntityNPCInterface> list = npc.level().getEntitiesOfClass(EntityNPCInterface.class,
                        npc.getBoundingBox().inflate(getRange(), getRange(), getRange()));
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
    public boolean isFollowing() { return following != null; }

    @Override
    public void stop() { following = null; }

    @Override
    public String getFollowing() { return name; }

    @Override
    public void setFollowing(String nameIn) { name = nameIn; }

    @Override
    public ICustomNpc<?> getFollowingNpc() { return following == null ? null : following.wrappedNPC; }

    public boolean hasOwner() { return !name.isEmpty(); }

    // New from Unofficial (BetaZavr)
    @Override
    public void aiUpdateTask() {
        if (npc != null && isFollowing()) { npc.getLookControl().setLookAt(following.getEyePosition()); }
    }

    @Override
    public boolean isWorking() { return isFollowing(); }

    private int getRange() {
        return ValueUtil.correctInt(npc != null ? Math.min(npc.followRange(), npc.stats.aggroRange) : 4, 2, CustomNpcs.NpcNavRange);
    }

}
