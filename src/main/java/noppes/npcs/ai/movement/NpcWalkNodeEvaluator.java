package noppes.npcs.ai.movement;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.jetbrains.annotations.NotNull;

public class NpcWalkNodeEvaluator extends WalkNodeEvaluator {

   public @NotNull BlockPathTypes getCachedBlockType(@NotNull Mob mob, int x, int y, int z) {
      return super.getCachedBlockType(mob, x, y, z);
   }

   public void done() {
      PathNavigationRegion level = this.level;
      Mob mob = this.mob;
      super.done();
      this.level = level;
      this.mob = mob;
   }

}
