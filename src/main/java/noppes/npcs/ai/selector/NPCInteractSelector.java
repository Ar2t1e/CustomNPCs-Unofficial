package noppes.npcs.ai.selector;

import com.google.common.base.Predicate;
import net.minecraft.world.entity.Entity;
import noppes.npcs.entity.EntityNPCInterface;

public class NPCInteractSelector implements Predicate<Entity> {

   private final EntityNPCInterface npc;

   public NPCInteractSelector(EntityNPCInterface npc) {
      this.npc = npc;
   }

   @Override
   public boolean apply(Entity ob) {
      return ob instanceof EntityNPCInterface && isEntityApplicable((EntityNPCInterface) ob);
   }

   public boolean isEntityApplicable(EntityNPCInterface entity) {
      return entity != npc && npc.isAlive() && !entity.isAttacking()
              && !npc.getFaction().isAggressiveToNpc(entity) && npc.ais.stopAndInteract;
   }
}
