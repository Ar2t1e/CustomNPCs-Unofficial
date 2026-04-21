package noppes.npcs.api.wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.api.IPos;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityLiving;
import noppes.npcs.api.entity.IEntityLivingBase;

public class EntityLivingWrapper<T extends EntityLiving> extends EntityLivingBaseWrapper<T> implements IEntityLiving<T> {

	public EntityLivingWrapper(T entity) { super(entity); }

	@Override
	public boolean canSeeEntity(IEntity<T> entityIn) { return entity.getEntitySenses().canSee(entityIn.getMCEntity()); }

	@Override
	public void clearNavigation() { entity.getNavigator().clearPath(); }

	@Override
	@SuppressWarnings("unchecked")
	public IEntityLivingBase<T> getAttackTarget() {
		IEntityLivingBase<T> base = (IEntityLivingBase<T>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(entity.getAttackTarget());
		return (base != null) ? base : super.getAttackTarget();
	}

	@Override
	public IPos getNavigationPath() {
		if (!isNavigating()) { return null; }
		PathPoint point = Objects.requireNonNull(entity.getNavigator().getPath()).getFinalPathPoint();
		if (point == null) { return null; }
		return new BlockPosWrapper(new BlockPos(point.x, point.y, point.z));
	}

	@Override
	public boolean isAttacking() { return super.isAttacking() || entity.getAttackTarget() != null; }

	@Override
	public boolean isNavigating() { return !entity.getNavigator().noPath(); }

	@Override
	public void jump() { entity.getJumpHelper().setJumping(); }

	@Override
	public void navigateTo(double x, double y, double z, double speed) {
		entity.getNavigator().clearPath();
		entity.getNavigator().tryMoveToXYZ(x, y, z, speed * 0.7);
	}

	@Override
	public void navigateTo(IPos[] posses, double speed) {
		PathNavigate nav = entity.getNavigator();
		nav.clearPath();
		List<PathPoint> points = new ArrayList<>();
		for (IPos pos : posses) {
			if (pos == null) { return; }
			BlockPos bp = pos.getMCBlockPos();
			points.add(new PathPoint(bp.getX(), bp.getY(), bp.getZ()));
		}
		nav.setPath(new Path(points.toArray(new PathPoint[0])), speed);
	}

	@Override
	public void setAttackTarget(IEntityLivingBase<T> living) {
		entity.setAttackTarget(living == null ? null : living.getMCEntity());
		super.setAttackTarget(living);
	}

}
