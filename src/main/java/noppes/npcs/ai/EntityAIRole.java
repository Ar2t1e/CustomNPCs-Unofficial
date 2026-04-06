package noppes.npcs.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

import javax.annotation.Nonnull;

public class EntityAIRole extends Goal {

   private final @Nonnull EntityNPCInterface npc;

   public EntityAIRole(@Nonnull EntityNPCInterface npcIn) { npc = npcIn; }

   @Override
   public boolean canUse() { return !npc.isKilled() && npc.role.aiShouldExecute(); }

   @Override
   public void start() { npc.role.aiStartExecuting(); }

   @Override
   public boolean canContinueToUse() { return !npc.isKilled() && npc.role.aiContinueExecute(); }

   @Override
   public void tick() { npc.role.aiUpdateTask(); }

}
