package noppes.npcs.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

import javax.annotation.Nonnull;

public class EntityAIJob extends Goal {

   private final @Nonnull EntityNPCInterface npc;

   public EntityAIJob(@Nonnull EntityNPCInterface npcIn) { npc = npcIn; }

   @Override
   public boolean canUse() { return !npc.isKilled() && npc.job.aiShouldExecute(); }

   @Override
   public void start() { npc.job.aiStartExecuting(); }

   @Override
   public boolean canContinueToUse() { return !npc.isKilled() && npc.job.aiContinueExecute(); }

   @Override
   public void tick() { npc.job.aiUpdateTask(); }

   @Override
   public void stop() { npc.job.stop(); }

   @Override
   public @Nonnull EnumSet<Flag> getFlags() { return npc.job.getFlags(); }

}
