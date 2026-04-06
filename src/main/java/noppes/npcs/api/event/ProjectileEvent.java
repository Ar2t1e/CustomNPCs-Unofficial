package noppes.npcs.api.event;

import noppes.npcs.api.interfaces.EventName;
import noppes.npcs.api.entity.IProjectile;
import noppes.npcs.constants.EnumScriptType;

public class ProjectileEvent extends CustomNPCsEvent {

   public IProjectile<?> projectile;

   public ProjectileEvent(IProjectile<?> projectile) {
      this.projectile = projectile;
   }

   @EventName(EnumScriptType.PROJECTILE_IMPACT)
   public static class ImpactEvent extends ProjectileEvent {
      public final int type;
      public final Object target;

      public ImpactEvent(IProjectile<?> projectile, int type, Object target) {
         super(projectile);
         this.type = type;
         this.target = target;
      }
   }

   @EventName(EnumScriptType.TICK)
   public static class UpdateEvent extends ProjectileEvent {
      public UpdateEvent(IProjectile<?> projectile) {
         super(projectile);
      }
   }

}
