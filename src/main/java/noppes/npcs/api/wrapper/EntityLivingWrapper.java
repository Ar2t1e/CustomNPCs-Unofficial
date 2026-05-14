package noppes.npcs.api.wrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import noppes.npcs.api.IPos;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityLiving;
import noppes.npcs.api.entity.IMob;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntityLivingWrapper<T extends Mob> extends EntityLivingBaseWrapper<T> implements IMob<T> {

   public EntityLivingWrapper(T entity) {
      super(entity);
   }

   public void navigateTo(double x, double y, double z, double speed) {
      entity.getNavigation().stop();
      entity.getNavigation().moveTo(x, y, z, speed * 0.7D);
   }

   public void clearNavigation() {
      this.entity.getNavigation().stop();
   }

   public IPos getNavigationPath() {
      if (!this.isNavigating()) {
         return null;
      }
      Node point = Objects.requireNonNull(entity.getNavigation().getPath()).getEndNode();
      return point == null ? null : new BlockPosWrapper(entity.level(), new BlockPos(point.x, point.y, point.z));
   }

   public boolean isNavigating() {
      return !this.entity.getNavigation().isDone();
   }

   public boolean isAttacking() {
      return super.isAttacking() || this.entity.getTarget() != null;
   }

   public void setAttackTarget(IEntityLiving<T> living) {
      if (living == null) {
         this.entity.setTarget(null);
      } else {
         this.entity.setTarget(living.getMCEntity());
      }
      super.setAttackTarget(living);
   }

   @SuppressWarnings("unchecked")
   public IEntityLiving<T> getAttackTarget() {
      IEntityLiving<T> base = (IEntityLiving<T>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(entity.getTarget());
      return base != null ? base : super.getAttackTarget();
   }

   public boolean canSeeEntity(IEntity<?> entity) {
      return this.entity.getSensing().hasLineOfSight(entity.getMCEntity());
   }

   public void jump() {
      this.entity.getJumpControl().jump();
   }


   // New from Unofficial (BetaZavr)
   @Override
   public void navigateTo(IPos[] posses, double speed) {
      PathNavigation nav = entity.getNavigation();
      nav.stop();
      List<Node> points = new ArrayList<>();
      BlockPos endPos = BlockPos.ZERO;
      for (IPos pos : posses) {
         if (pos == null) { return; }
         BlockPos bp = pos.getMCBlockPos();
         points.add(new Node(bp.getX(), bp.getY(), bp.getZ()));
         endPos = bp;
      }
      nav.moveTo(new Path(points, endPos, false), speed * 0.7D);
   }

}
