package noppes.npcs.mixin.scoreboard;


import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(value = ServerScoreboard.class, priority = 502)
public interface IServerScoreboardMixin {

    @Accessor Set<ScoreObjective> getAddedObjectives();

}
